/**
 * Copyright @ 2008 Quan Nguyen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Xml;
using Microsoft.Win32;
using System.Globalization;
using System.Threading;
using System.Drawing.Imaging;

using VietOCR.NET.Postprocessing;
using Vietpad.NET.Controls;
using VietOCR.NET.Controls;
using Net.SourceForge.Vietpad.InputMethod;
using VietOCR.NET.WIA;

namespace VietOCR.NET
{
    public partial class GUI : GUIWithRegistry
    {
        protected const string strProgName = "VietOCR.NET";
        public const string TO_BE_IMPLEMENTED = "To be implemented";

        protected string curLangCode;
        string[] langCodes;
        string[] langs;

        private int imageIndex;
        private int imageTotal;
        private IList<Image> imageList;

        private Rectangle rect = Rectangle.Empty;
        private Rectangle box = Rectangle.Empty;

        private float scaleX = 1f;
        private float scaleY = 1f;

        protected string selectedUILanguage;
        private int filterIndex;

        const string strUILang = "UILanguage";
        const string strOcrLanguage = "OcrLanguage";
        const string strWordWrap = "WordWrap";
        const string strFontFace = "FontFace";
        const string strFontSize = "FontSize";
        const string strFontStyle = "FontStyle";
        const string strForeColor = "ForeColor";
        const string strBackColor = "BackColor";
        const string strFilterIndex = "FilterIndex";
        const string strDangAmbigsPath = "DangAmbigsPath";
        const string strDangAmbigsOn = "DangAmbigsOn";

        private bool IsFitForZoomIn = false;
        private const float ZOOM_FACTOR = 1.25f;
        protected string dangAmbigsPath;
        protected bool dangAmbigsOn;

        public GUI()
        {
            // Access registry to determine which UI Language to be loaded.
            // The desired locale must be known before initializing visual components
            // with language text. Waiting until OnLoad would be too late.
            strRegKey += strProgName;

            RegistryKey regkey = Registry.CurrentUser.OpenSubKey(strRegKey);

            if (regkey == null)
                regkey = Registry.CurrentUser.CreateSubKey(strRegKey);

            selectedUILanguage = (string)regkey.GetValue(strUILang, "en-US");
            regkey.Close();

            // Sets the UI culture to the selected language.
            Thread.CurrentThread.CurrentUICulture = new CultureInfo(selectedUILanguage);

            InitializeComponent();

            //rectNormal = DesktopBounds;

            LoadLang();
            this.toolStripCbLang.Items.AddRange(langs);
        }

        void LoadLang()
        {
            XmlDocument doc = new XmlDocument();

            String workingDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            String xmlFilePath = Path.Combine(workingDir, "Data/ISO639-3.xml");
            Dictionary<string, string> ht = new Dictionary<string, string>();

            try
            {
                string tessdataDir = Path.Combine(workingDir, "tessdata");

                //if (!Directory.Exists(tessdataDir))
                //{
                //    string TESSDATA_PREFIX = Environment.GetEnvironmentVariable("TESSDATA_PREFIX");
                //    if (String.IsNullOrEmpty(TESSDATA_PREFIX))
                //    {
                //        TESSDATA_PREFIX = "/usr/local/share/tessdata"; // default path of tessdata on Linux (for Mono)
                //    }
                //    tessdataDir = TESSDATA_PREFIX;
                //}

                langCodes = Directory.GetFiles(tessdataDir, "*.inttemp");

                doc.Load(xmlFilePath);
                //doc.Load(System.Reflection.Assembly.GetExecutingAssembly().GetManifestResourceStream("VietOCR.NET.Data.ISO639-3.xml"));

                XmlNodeList list = doc.GetElementsByTagName("entry");
                foreach (XmlNode node in list)
                {
                    ht.Add(node.Attributes[0].Value, node.InnerText);
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show(this, "Missing ISO639-3.xml file. Cannot find it in " + Path.GetDirectoryName(xmlFilePath) + " directory.", strProgName);
                // this also applies to missing language data files in tessdata directory
                Console.WriteLine(ex.StackTrace);
            }
            finally
            {
                if (langCodes == null)
                {
                    langs = new String[0];
                }
                else
                {
                    langs = new String[langCodes.Length];
                }

                for (int i = 0; i < langs.Length; i++)
                {
                    langCodes[i] = Path.GetFileNameWithoutExtension(langCodes[i]);
                    // translate ISO codes to full English names for user-friendliness

                    if (ht.ContainsKey(langCodes[i]))
                    {
                        langs[i] = ht[langCodes[i]];
                    }
                    else
                    {
                        langs[i] = langCodes[i];
                    }
                }
            }
        }

        private void oCRToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (this.pictureBox1.Image == null)
            {
                MessageBox.Show(this, Properties.Resources.LoadImage, strProgName);
                return;
            }

            Rectangle rect = ((ScrollablePictureBox)this.pictureBox1).GetRect();

            if (rect != Rectangle.Empty)
            {
                try
                {
                    rect = new Rectangle((int)(rect.X * scaleX), (int)(rect.Y * scaleY), (int)(rect.Width * scaleX), (int)(rect.Height * scaleY));
                    performOCR(imageList, imageIndex, rect);
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.StackTrace);
                }
            }
            else
            {
                performOCR(imageList, imageIndex, Rectangle.Empty);
            }
        }

        private void oCRAllPagesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (this.pictureBox1.Image == null)
            {
                MessageBox.Show(this, Properties.Resources.LoadImage, strProgName);
                return;
            }

            performOCR(imageList, -1, Rectangle.Empty);
        }

        /// <summary>
        /// Perform OCR on pages of image.
        /// </summary>
        /// <param name="imageList"></param>
        /// <param name="index">-1 for all pages</param>
        /// <param name="rect">selection rectangle</param>
        void performOCR(IList<Image> imageList, int index, Rectangle rect)
        {
            try
            {
                if (this.toolStripCbLang.SelectedIndex == -1)
                {
                    MessageBox.Show(this, Properties.Resources.selectLanguage, strProgName);
                    return;
                }

                this.toolStripStatusLabel1.Text = Properties.Resources.OCRrunning;
                this.Cursor = Cursors.WaitCursor;
                this.pictureBox1.UseWaitCursor = true;
                this.textBox1.Cursor = Cursors.WaitCursor;
                this.toolStripBtnOCR.Enabled = false;
                this.oCRToolStripMenuItem.Enabled = false;
                this.oCRAllPagesToolStripMenuItem.Enabled = false;
                this.toolStripProgressBar1.Enabled = true;
                this.toolStripProgressBar1.Visible = true;
                this.toolStripProgressBar1.Style = ProgressBarStyle.Marquee;

                OCRImageEntity entity = new OCRImageEntity(imageList, index, rect, curLangCode);
                // Start the asynchronous operation.
                backgroundWorker1.RunWorkerAsync(entity);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }
        private void postprocessToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (curLangCode == null) return;

            try
            {
                string selectedText = this.textBox1.SelectedText;
                if (!String.IsNullOrEmpty(selectedText))
                {
                    selectedText = Processor.PostProcess(selectedText, curLangCode, dangAmbigsPath, dangAmbigsOn);
                    this.textBox1.SelectedText = selectedText;
                }
                else
                {
                    this.textBox1.Text = Processor.PostProcess(this.textBox1.Text, curLangCode, dangAmbigsPath, dangAmbigsOn);
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.StackTrace);
                MessageBox.Show(this, string.Format("Post-processing not supported for {0} language.", ex.Message), strProgName);
            }
        }

        protected virtual void wordWrapToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void fontToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        private void helpToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (OwnedForms.Length > 0)
            {
                foreach (Form form in this.OwnedForms)
                {
                    HtmlHelpForm helpForm1 = form as HtmlHelpForm;
                    if (helpForm1 != null)
                    {
                        helpForm1.Show();
                        helpForm1.WindowState = FormWindowState.Normal;
                        return;
                    }
                }
            }
            HtmlHelpForm helpForm = new HtmlHelpForm(Properties.Resources.readme, strProgName + Properties.Resources._Help);
            helpForm.Owner = this;
            helpForm.Show();
        }

        private void aboutToolStripMenuItem2_Click(object sender, EventArgs e)
        {
            string releaseDate = System.Configuration.ConfigurationManager.AppSettings["ReleaseDate"];
            string version = System.Configuration.ConfigurationManager.AppSettings["Version"];

            MessageBox.Show(this, strProgName + " " + version + " © 2008\n" +
                ".NET GUI Frontend for Tesseract OCR\n" +
                DateTime.Parse(releaseDate).ToString("D", System.Threading.Thread.CurrentThread.CurrentUICulture).Normalize() + "\n" +
                "http://vietocr.sourceforge.net",
                Properties.Resources.About_ + strProgName, MessageBoxButtons.OK, MessageBoxIcon.Information);
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog1 = new OpenFileDialog();

            //openFileDialog1.InitialDirectory = "c:\\";
            openFileDialog1.Title = Properties.Resources.OpenImageFile;
            openFileDialog1.Filter = "PDF Files (*.pdf)|*.pdf;|Image Files (*.tif;*.tiff)|*.tif;*.tiff|Image Files (*.bmp)|*.bmp|Image Files (*.jpg;*.jpeg)|*.jpg;*.jpeg|Image Files (*.png)|*.png|All Image Files|*.tif;*.tiff;*.bmp;*.jpg;*.jpeg;*.png|All Files (*.*)|*.*";
            openFileDialog1.FilterIndex = filterIndex;
            openFileDialog1.RestoreDirectory = true;

            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                openFile(openFileDialog1.FileName);
                scaleX = scaleY = 1f;
                filterIndex = openFileDialog1.FilterIndex;
            }

        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveFileDialog saveFileDialog1 = new SaveFileDialog();
            saveFileDialog1.Title = Properties.Resources.Save_As;
            saveFileDialog1.Filter = "Text files (*.txt)|*.txt|All files (*.*)|*.*";
            saveFileDialog1.FilterIndex = 1;
            saveFileDialog1.RestoreDirectory = true;

            if (saveFileDialog1.ShowDialog() == DialogResult.OK)
            {
                try
                {
                    this.Cursor = Cursors.WaitCursor;
                    StreamWriter sw = new StreamWriter(saveFileDialog1.FileName, false, new System.Text.UTF8Encoding());
                    sw.Write(this.textBox1.Text);
                    sw.Close();
                    this.textBox1.Modified = false;
                }
                catch (Exception exc)
                {
                    MessageBox.Show(exc.Message, strProgName, MessageBoxButtons.OK, MessageBoxIcon.Error);
                }
                finally
                {
                    this.Cursor = Cursors.Default;
                }
            }
        }

        private void toolStripCbLang_SelectedIndexChanged(object sender, EventArgs e)
        {
            curLangCode = langCodes[this.toolStripCbLang.SelectedIndex];
            VietKeyHandler.VietModeEnabled = curLangCode == "vie";
        }

        private void toolStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {
            if (e.ClickedItem is ToolStripSeparator || e.ClickedItem is ToolStripLabel) return;

            ToolStripButton tsb = (ToolStripButton)e.ClickedItem;
            ToolStripMenuItem mi = (ToolStripMenuItem)tsb.Tag;
            if (mi != null)
                mi.PerformClick();
        }

        private void toolStripBtnClear_Click(object sender, EventArgs e)
        {
            this.textBox1.Clear();
        }

        private void quitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            Application.Exit();
        }

        private void toolStripBtnPrev_Click(object sender, EventArgs e)
        {
            imageIndex--;
            if (imageIndex < 0)
            {
                imageIndex = 0;
            }
            else
            {
                this.toolStripStatusLabel1.Text = null;
                displayImage();
            }
            setButton();
            this.pictureBox1.Deselect();
        }

        private void toolStripBtnNext_Click(object sender, EventArgs e)
        {
            imageIndex++;
            if (imageIndex > imageTotal - 1)
            {
                imageIndex = imageTotal - 1;
            }
            else
            {
                this.toolStripStatusLabel1.Text = null;
                displayImage();
            }
            setButton();
            this.pictureBox1.Deselect();
        }

        void setButton()
        {
            if (imageIndex == 0)
            {
                this.toolStripBtnPrev.Enabled = false;
            }
            else
            {
                this.toolStripBtnPrev.Enabled = true;
            }

            if (imageIndex == imageList.Count - 1)
            {
                this.toolStripBtnNext.Enabled = false;
            }
            else
            {
                this.toolStripBtnNext.Enabled = true;
            }
        }


        private void toolStripBtnFitImage_Click(object sender, EventArgs e)
        {
            this.toolStripBtnFitImage.Enabled = false;
            this.toolStripBtnActualSize.Enabled = true;

            this.pictureBox1.Deselect();

            this.pictureBox1.Dock = DockStyle.Fill;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;
            scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
            scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
        }

        private void toolStripBtnActualSize_Click(object sender, EventArgs e)
        {
            this.toolStripBtnFitImage.Enabled = true;
            this.toolStripBtnActualSize.Enabled = false;

            this.pictureBox1.Deselect();

            this.pictureBox1.Dock = DockStyle.None;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.Normal;
            scaleX = scaleY = 1f;
        }
        /// <summary>
        /// Opens image file.
        /// </summary>
        /// <param name="selectedImageFile"></param>
        public void openFile(string selectedImageFile)
        {
            this.toolStripStatusLabel1.Text = Properties.Resources.Loading_image;
            this.Cursor = Cursors.WaitCursor;
            this.pictureBox1.UseWaitCursor = true;
            this.textBox1.Cursor = Cursors.WaitCursor;
            this.toolStripBtnOCR.Enabled = false;
            this.oCRToolStripMenuItem.Enabled = false;
            this.oCRAllPagesToolStripMenuItem.Enabled = false;
            this.toolStripProgressBar1.Enabled = true;
            this.toolStripProgressBar1.Visible = true;
            this.toolStripProgressBar1.Style = ProgressBarStyle.Marquee;

            this.backgroundWorker3.RunWorkerAsync(selectedImageFile);
        }

        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        private void backgroundWorker3_DoWork(object sender, DoWorkEventArgs e)
        {
            string selectedImageFile = (string)e.Argument;
            FileInfo imageFile = new FileInfo(selectedImageFile);

            if (selectedImageFile.ToLower().EndsWith(".pdf"))
            {
                string workingTiffFileName = null;

                try
                {
                    workingTiffFileName = Utilities.ConvertPdf2Tiff(selectedImageFile);
                    imageList = ImageIOHelper.GetImageList(new FileInfo(workingTiffFileName));
                }
                finally
                {
                    if (workingTiffFileName != null && File.Exists(workingTiffFileName))
                    {
                        File.Delete(workingTiffFileName);
                    }
                }
            }
            else
            {
                imageList = ImageIOHelper.GetImageList(imageFile);
            }

            e.Result = imageFile;
        }

        private void backgroundWorker3_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            this.toolStripProgressBar1.Enabled = false;
            this.toolStripProgressBar1.Visible = false;

            // First, handle the case where an exception was thrown.
            if (e.Error != null)
            {
                this.toolStripStatusLabel1.Text = String.Empty;
                MessageBox.Show(e.Error.Message, Properties.Resources.Load_image, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (e.Cancelled)
            {
                // Next, handle the case where the user canceled the operation.
                // Note that due to a race condition in the DoWork event handler, the Cancelled
                // flag may not have been set, even though CancelAsync was called.
                this.toolStripStatusLabel1.Text = Properties.Resources.Canceled;
            }
            else
            {
                // Finally, handle the case where the operation succeeded.
                loadImage((FileInfo) e.Result);
                this.toolStripStatusLabel1.Text = Properties.Resources.Loading_completed;               
            }

            this.Cursor = Cursors.Default;
            this.pictureBox1.UseWaitCursor = false;
            this.textBox1.Cursor = Cursors.Default;
            this.toolStripBtnOCR.Enabled = true;
            this.oCRToolStripMenuItem.Enabled = true;
            this.oCRAllPagesToolStripMenuItem.Enabled = true;
        }

        void loadImage(FileInfo imageFile)
        {
            if (imageList == null)
            {
                MessageBox.Show(this, Properties.Resources.Cannotloadimage, strProgName, MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }

            imageTotal = imageList.Count;
            imageIndex = 0;

            displayImage();

            this.Text = imageFile.Name + " - " + strProgName;
            //this.toolStripStatusLabel1.Text = null;
            this.pictureBox1.Deselect();

            this.toolStripBtnFitImage.Enabled = true;
            this.toolStripBtnZoomIn.Enabled = true;
            this.toolStripBtnZoomOut.Enabled = true;
            this.toolStripBtnRotateCCW.Enabled = true;
            this.toolStripBtnRotateCW.Enabled = true;

            if (imageList.Count == 1)
            {
                this.toolStripBtnNext.Enabled = false;
                this.toolStripBtnPrev.Enabled = false;
            }
            else
            {
                this.toolStripBtnNext.Enabled = true;
                this.toolStripBtnPrev.Enabled = true;
            }

            setButton();
        }

        void displayImage()
        {
            this.lblCurIndex.Text = Properties.Resources.Page_ + (imageIndex + 1) + Properties.Resources._of_ + imageTotal;
            this.pictureBox1.Image = new Bitmap(imageList[imageIndex]);
            this.pictureBox1.Size = this.pictureBox1.Image.Size;
            this.pictureBox1.Invalidate();
        }

        private void backgroundWorker1_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            this.toolStripProgressBar1.Enabled = false;
            this.toolStripProgressBar1.Visible = false;

            // First, handle the case where an exception was thrown.
            if (e.Error != null)
            {
                this.toolStripStatusLabel1.Text = String.Empty;
                MessageBox.Show(e.Error.Message, Properties.Resources.OCROperation, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (e.Cancelled)
            {
                // Next, handle the case where the user canceled the operation.
                // Note that due to a race condition in the DoWork event handler, the Cancelled
                // flag may not have been set, even though CancelAsync was called.
                this.toolStripStatusLabel1.Text = Properties.Resources.Canceled;
            }
            else
            {
                // Finally, handle the case where the operation succeeded.
                this.toolStripStatusLabel1.Text = Properties.Resources.OCRcompleted;
                this.textBox1.AppendText(e.Result.ToString());
            }

            this.Cursor = Cursors.Default;
            this.pictureBox1.UseWaitCursor = false;
            this.textBox1.Cursor = Cursors.Default;
            this.toolStripBtnOCR.Enabled = true;
            this.oCRToolStripMenuItem.Enabled = true;
            this.oCRAllPagesToolStripMenuItem.Enabled = true;
        }

        private void backgroundWorker2_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            this.toolStripProgressBar1.Enabled = false;
            this.toolStripProgressBar1.Visible = false;

            // First, handle the case where an exception was thrown.
            if (e.Error != null)
            {
                this.toolStripStatusLabel1.Text = String.Empty;
                MessageBox.Show(e.Error.Message, Properties.Resources.ScanningOperation, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }
            else if (e.Cancelled)
            {
                // Next, handle the case where the user canceled the operation.
                // Note that due to a race condition in the DoWork event handler, the Cancelled
                // flag may not have been set, even though CancelAsync was called.
                this.toolStripStatusLabel1.Text = Properties.Resources.Canceled;
            }
            else
            {
                // Finally, handle the case where the operation succeeded.
                openFile(e.Result.ToString());
                this.toolStripStatusLabel1.Text = Properties.Resources.Scancompleted;
            }

            this.Cursor = Cursors.Default;
            this.pictureBox1.UseWaitCursor = false;
            this.textBox1.Cursor = Cursors.Default;
            this.toolStripBtnScan.Enabled = true;
            this.scanToolStripMenuItem.Enabled = true;
        }

        private void backgroundWorker1_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            this.toolStripProgressBar1.Value = e.ProgressPercentage;
            //this.toolStripStatusLabel1.Text = e.UserState as String;
        }

        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        private void backgroundWorker1_DoWork(object sender, DoWorkEventArgs e)
        {
            // Get the BackgroundWorker that raised this event.
            BackgroundWorker worker = sender as BackgroundWorker;
            worker.ReportProgress(0, "OCR running...");

            OCRImageEntity entity = (OCRImageEntity)e.Argument;
            OCR ocrEngine = new OCR();

            // Assign the result of the computation to the Result property of the DoWorkEventArgs
            // object. This is will be available to the RunWorkerCompleted eventhandler.
            e.Result = ocrEngine.RecognizeText(entity.ClonedImages, entity.Lang, entity.Rect, worker, e);
        }

        private void scanToolStripMenuItem_Click(object sender, EventArgs e)
        {
            scaleX = scaleY = 1f;
            performScan();
        }

        /// <summary>
        /// Access scanner and scan documents via WIA.
        /// </summary>
        void performScan()
        {
            try
            {
                this.toolStripStatusLabel1.Text = Properties.Resources.Scanning;
                this.Cursor = Cursors.WaitCursor;
                this.pictureBox1.UseWaitCursor = true;
                this.textBox1.Cursor = Cursors.WaitCursor;
                this.toolStripBtnScan.Enabled = false;
                this.scanToolStripMenuItem.Enabled = false;
                this.toolStripProgressBar1.Enabled = true;
                this.toolStripProgressBar1.Visible = true;
                this.toolStripProgressBar1.Style = ProgressBarStyle.Marquee;

                // Start the asynchronous operation.
                backgroundWorker2.RunWorkerAsync();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        private void backgroundWorker2_DoWork(object sender, DoWorkEventArgs e)
        {
            using (WiaScannerAdapter adapter = new WiaScannerAdapter())
            {
                try
                {
                    string tempFileName = Path.GetTempFileName().Replace(".tmp", ".png");
                    FileInfo imageFile = new FileInfo(tempFileName);
                    if (imageFile.Exists)
                    {
                        imageFile.Delete();
                    }
                    adapter.ScanImage(ImageFormat.Png, imageFile.FullName);
                    e.Result = tempFileName;
                }
                catch (WiaOperationException ex)
                {
                    throw new Exception(System.Text.RegularExpressions.Regex.Replace(ex.ErrorCode.ToString(), "(?=\\p{Lu}+)", " ").Trim() + ".");
                }
            }
        }
        private void splitContainer2_Panel2_DragOver(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                if ((e.AllowedEffect & DragDropEffects.Move) != 0)
                    e.Effect = DragDropEffects.Move;

                if (((e.AllowedEffect & DragDropEffects.Copy) != 0) &&
                    ((e.KeyState & 0x08) != 0))    // Ctrl key
                    e.Effect = DragDropEffects.Copy;
            }
            //else if (e.Data.GetDataPresent(DataFormats.Bitmap))
            //{
            //    e.Effect = DragDropEffects.Copy;
            //}
        }

        private void splitContainer2_Panel2_DragDrop(object sender, DragEventArgs e)
        {
            if (e.Data.GetDataPresent(DataFormats.FileDrop))
            {
                string[] astr = (string[])e.Data.GetData(DataFormats.FileDrop);

                if (System.IO.File.Exists(astr[0]))
                {
                    openFile(astr[0]);
                }
            }
        }

        private void textBox1_MouseEnter(object sender, EventArgs e)
        {
            if (!this.textBox1.Focused)
            {
                textBox1.HideSelection = false;
                this.textBox1.Focus();
            }
        }

        private void toolStripBtnRotateCCW_Click(object sender, EventArgs e)
        {
            // Rotating 270 degrees is equivalent to rotating -90 degrees.
            this.pictureBox1.Image.RotateFlip(RotateFlipType.Rotate270FlipNone);
            imageList[imageIndex].RotateFlip(RotateFlipType.Rotate270FlipNone);
            this.pictureBox1.Deselect();
            this.pictureBox1.Size = this.pictureBox1.Image.Size;
            this.pictureBox1.Refresh();
        }

        private void toolStripBtnRotateCW_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Image.RotateFlip(RotateFlipType.Rotate90FlipNone);
            imageList[imageIndex].RotateFlip(RotateFlipType.Rotate90FlipNone);
            this.pictureBox1.Deselect();
            this.pictureBox1.Size = this.pictureBox1.Image.Size;
            this.pictureBox1.Refresh();
        }

        private void toolStripBtnZoomIn_Click(object sender, EventArgs e)
        {
            IsFitForZoomIn = true;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            // Zoom works best if you first fit the image according to its true aspect ratio.
            Fit();
            // Make the PictureBox dimensions larger by 25% to effect the Zoom.
            this.pictureBox1.Width = Convert.ToInt32(this.pictureBox1.Width * ZOOM_FACTOR);
            this.pictureBox1.Height = Convert.ToInt32(this.pictureBox1.Height * ZOOM_FACTOR);
            scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
            scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
            this.pictureBox1.Deselect();
        }

        private void toolStripBtnZoomOut_Click(object sender, EventArgs e)
        {
            // Zoom works best if you first fit the image according to its true aspect ratio.
            Fit();
            // StretchImage SizeMode works best for zooming.
            this.pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;
            // Make the PictureBox dimensions smaller by 25% to effect the Zoom.
            this.pictureBox1.Width = Convert.ToInt32(this.pictureBox1.Width / ZOOM_FACTOR);
            this.pictureBox1.Height = Convert.ToInt32(this.pictureBox1.Height / ZOOM_FACTOR);
            scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
            scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
            this.pictureBox1.Deselect();
        }

        // This method makes the image fit properly in the PictureBox. You might think 
        // that the AutoSize SizeMode enum would make the image appear in the PictureBox 
        // according to its true aspect ratio within the fixed bounds of the PictureBox.
        // However, it merely expands or shrinks the PictureBox.
        private void Fit()
        {
            // if Fit was called by the Zoom In button, then center the image. This is
            // only needed when working with images that are smaller than the PictureBox.
            // Feel free to uncomment the line that sets the SizeMode and then see how
            // it causes Zoom In for small images to show unexpected behavior.

            if ((this.pictureBox1.Image.Width < this.pictureBox1.Width) &&
                (this.pictureBox1.Image.Height < this.pictureBox1.Height))
            {
                if (!IsFitForZoomIn)
                {
                    this.pictureBox1.SizeMode = PictureBoxSizeMode.CenterImage;
                }
            }
            //CalculateAspectRatioAndSetDimensions();
        }

        // Calculates and returns the image's aspect ratio, and sets 
        // its proper dimensions. This is used for Fit() and for saving thumbnails
        // of images.
        private double CalculateAspectRatioAndSetDimensions()
        {
            // Calculate the proper aspect ratio and set the image's dimensions.
            double ratio;

            if (this.pictureBox1.Image.Width > this.pictureBox1.Image.Height)
            {
                ratio = this.pictureBox1.Image.Width / this.pictureBox1.Image.Height;
                this.pictureBox1.Height = Convert.ToInt32(Convert.ToDouble(this.pictureBox1.Width) / ratio);
            }
            else
            {
                ratio = this.pictureBox1.Image.Height / this.pictureBox1.Image.Width;
                this.pictureBox1.Width = Convert.ToInt32(Convert.ToDouble(this.pictureBox1.Height) / ratio);
            }
            return ratio;
        }

        /// <summary>
        /// Changes localized text and messages
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="firstTime"></param>
        protected virtual void ChangeUILanguage(string locale)
        {
            FormLocalizer localizer = new FormLocalizer(this, typeof(GUI));
            localizer.ApplyCulture(new CultureInfo(locale));

            if (imageTotal > 0)
            {
                this.lblCurIndex.Text = Properties.Resources.Page_ + (imageIndex + 1) + Properties.Resources._of_ + imageTotal;
            }

            this.toolStripStatusLabel1.Text = null;

            foreach (Form form in this.OwnedForms)
            {
                HtmlHelpForm helpForm = form as HtmlHelpForm;
                if (helpForm != null)
                {
                    helpForm.Text = strProgName + Properties.Resources._Help;
                }
            }
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);

            this.toolStripCbLang.SelectedItem = regkey.GetValue(strOcrLanguage, null);
            this.textBox1.WordWrap = Convert.ToBoolean(
                (int)regkey.GetValue(strWordWrap, Convert.ToInt32(true)));
            this.textBox1.Font = new Font((string)regkey.GetValue(strFontFace, "Microsoft Sans Serif"),
                float.Parse((string)regkey.GetValue(strFontSize, "10")),
                (FontStyle)regkey.GetValue(strFontStyle, FontStyle.Regular));
            this.textBox1.ForeColor = Color.FromArgb(
                (int)regkey.GetValue(strForeColor, Color.FromKnownColor(KnownColor.Black).ToArgb()));
            this.textBox1.BackColor = Color.FromArgb(
                (int)regkey.GetValue(strBackColor, Color.FromKnownColor(KnownColor.White).ToArgb()));
            filterIndex = (int)regkey.GetValue(strFilterIndex, 1);
            selectedUILanguage = (string)regkey.GetValue(strUILang, "en-US");

            String workingDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            dangAmbigsPath = (string)regkey.GetValue(strDangAmbigsPath, Path.Combine(workingDir, "Data"));
            dangAmbigsOn = Convert.ToBoolean(
                (int)regkey.GetValue(strDangAmbigsOn, Convert.ToInt32(true)));
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);

            if (this.toolStripCbLang.SelectedIndex != -1)
            {
                regkey.SetValue(strOcrLanguage, this.toolStripCbLang.SelectedItem.ToString());
            }
            regkey.SetValue(strWordWrap, Convert.ToInt32(this.textBox1.WordWrap));
            regkey.SetValue(strFontFace, this.textBox1.Font.Name);
            regkey.SetValue(strFontSize, this.textBox1.Font.SizeInPoints.ToString());
            regkey.SetValue(strFontStyle, (int)this.textBox1.Font.Style);
            regkey.SetValue(strForeColor, this.textBox1.ForeColor.ToArgb());
            regkey.SetValue(strBackColor, this.textBox1.BackColor.ToArgb());
            regkey.SetValue(strFilterIndex, filterIndex);
            regkey.SetValue(strUILang, selectedUILanguage);
            regkey.SetValue(strDangAmbigsPath, dangAmbigsPath);
            regkey.SetValue(strDangAmbigsOn, Convert.ToInt32(dangAmbigsOn));
        }

        private void formatToolStripMenuItem_DropDownOpening(object sender, EventArgs e)
        {
            this.wordWrapToolStripMenuItem.Checked = this.textBox1.WordWrap;
        }

        protected virtual void optionsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void changeCaseToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void removeLineBreaksToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void mergeTiffToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void splitPdfToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }
    }
}
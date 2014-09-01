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
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Xml;
using Microsoft.Win32;
using System.Globalization;
using System.Threading;
using System.Drawing.Imaging;
using Net.SourceForge.Vietpad.InputMethod;
using VietOCR.NET.Utilities;

namespace VietOCR.NET
{
    public partial class GUI : GUIWithRegistry
    {
        public const string strProgName = "VietOCR.NET";
        public const string TO_BE_IMPLEMENTED = "To be implemented";

        protected string curLangCode;
        private string[] installedLanguageCodes;
        private string[] installedLanguages;

        public string[] InstalledLanguages
        {
            get { return installedLanguages; }
            set { installedLanguages = value; }
        }

        private Dictionary<string, string> lookupISO639;

        public Dictionary<string, string> LookupISO639
        {
            get { return lookupISO639; }
        }

        private Dictionary<string, string> lookupISO_3_1_Codes;

        public Dictionary<string, string> LookupISO_3_1_Codes
        {
            get { return lookupISO_3_1_Codes; }
        }

        protected int imageIndex;
        private int imageTotal;
        protected IList<Image> imageList;

        private Rectangle rect = Rectangle.Empty;
        private Rectangle box = Rectangle.Empty;

        protected float scaleX = 1f;
        protected float scaleY = 1f;

        protected string selectedUILanguage;
        private int filterIndex;
        private string textFilename;
        private bool textModified;

        List<string> mruList = new List<string>();
        private string strClearRecentFiles;

        const string strUILang = "UILanguage";
        const string strOcrLanguage = "OcrLanguage";
        const string strWordWrap = "WordWrap";
        const string strFontFace = "FontFace";
        const string strFontSize = "FontSize";
        const string strFontStyle = "FontStyle";
        const string strForeColor = "ForeColor";
        const string strBackColor = "BackColor";
        const string strFilterIndex = "FilterIndex";
        const string strMruList = "MruList";

        private bool IsFitForZoomIn = false;
        private const float ZOOM_FACTOR = 1.25f;
        private Point curScrollPos;
        protected Point pointClicked;
        protected readonly string baseDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);

        protected System.CodeDom.Compiler.TempFileCollection tempFileCollection = new System.CodeDom.Compiler.TempFileCollection();

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

            GetInstalledLanguagePacks();
            PopulateOCRLanguageBox();

            //rectNormal = DesktopBounds;

            //// Set system event.
            SystemEvents.SessionEnding += new SessionEndingEventHandler(OnSessionEnding);
        }

        // Event overrides
        protected override void OnLoad(EventArgs ea)
        {
            base.OnLoad(ea);

            // Work around a bug which causes Modified property
            // to be true when system default locale is vi-VN.
            this.textBox1.Modified = false;

            updateMRUMenu();
        }
        protected override void OnClosing(CancelEventArgs cea)
        {
            base.OnClosing(cea);

            cea.Cancel = !OkToTrash();
        }

        // Event handlers
        void OnSessionEnding(object obj, SessionEndingEventArgs seea)
        {
            seea.Cancel = !OkToTrash();
        }

        /// <summary>
        /// Gets Tesseract's installed language data packs.
        /// </summary>
        void GetInstalledLanguagePacks()
        {
            lookupISO639 = new Dictionary<string, string>();
            lookupISO_3_1_Codes = new Dictionary<string, string>();

            try
            {
                string tessdataDir = Path.Combine(baseDir, "tessdata");
                installedLanguageCodes = Directory.GetFiles(tessdataDir, "*.inttemp");
                string xmlFilePath = Path.Combine(baseDir, "Data/ISO639-3.xml");
                Utilities.Utilities.LoadFromXML(lookupISO639, xmlFilePath);
                xmlFilePath = Path.Combine(baseDir, "Data/ISO639-1.xml");
                Utilities.Utilities.LoadFromXML(lookupISO_3_1_Codes, xmlFilePath);
            }
            catch (Exception e)
            {
                MessageBox.Show(this, e.Message, strProgName);
                // this also applies to missing language data files in tessdata directory
                Console.WriteLine(e.StackTrace);
            }
            finally
            {
                if (installedLanguageCodes == null)
                {
                    installedLanguages = new String[0];
                }
                else
                {
                    installedLanguages = new String[installedLanguageCodes.Length];
                }

                for (int i = 0; i < installedLanguages.Length; i++)
                {
                    installedLanguageCodes[i] = Path.GetFileNameWithoutExtension(installedLanguageCodes[i]);
                    // translate ISO codes to full English names for user-friendliness
                    if (lookupISO639.ContainsKey(installedLanguageCodes[i]))
                    {
                        installedLanguages[i] = lookupISO639[installedLanguageCodes[i]];
                    }
                    else
                    {
                        installedLanguages[i] = installedLanguageCodes[i];
                    }
                }
            }
        }

        /// <summary>
        /// Populates OCR Language box.
        /// </summary>
        void PopulateOCRLanguageBox()
        {
            this.toolStripCbLang.Items.AddRange(installedLanguages);
        }

        protected virtual void ocrToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void ocrAllPagesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void toolStripButtonCancelOCR_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void postprocessToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
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
            HtmlHelpForm helpForm = new HtmlHelpForm(Properties.Resources.readme, ((ToolStripMenuItem)sender).Text.Replace("&", string.Empty));
            helpForm.Owner = this;
            helpForm.Show();
        }

        private void aboutToolStripMenuItem2_Click(object sender, EventArgs e)
        {
            string releaseDate = System.Configuration.ConfigurationManager.AppSettings["ReleaseDate"];
            string version = System.Configuration.ConfigurationManager.AppSettings["Version"];

            MessageBox.Show(this, strProgName + " " + version + " © 2008\n" +
                ".NET GUI Frontend for Tesseract 2.04 OCR Engine\n" +
                DateTime.Parse(releaseDate).ToString("D", System.Threading.Thread.CurrentThread.CurrentUICulture).Normalize() + "\n" +
                "http://vietocr.sourceforge.net",
                ((ToolStripMenuItem)sender).Text.Replace("&", string.Empty), MessageBoxButtons.OK, MessageBoxIcon.Information);
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog1 = new OpenFileDialog();

            //openFileDialog1.InitialDirectory = "c:\\";
            openFileDialog1.Title = Properties.Resources.OpenImageFile;
            openFileDialog1.Filter = "All Image Files|*.bmp;*.gif;*.jpg;*.jpeg;*.png;*.tif;*.tiff;*.pdf|Image Files (*.bmp)|*.bmp|Image Files (*.gif)|*.gif|Image Files (*.jpg;*.jpeg)|*.jpg;*.jpeg|Image Files (*.png)|*.png|Image Files (*.tif;*.tiff)|*.tif;*.tiff|PDF Files (*.pdf)|*.pdf|UTF-8 Text Files (*.txt)|*.txt";
            openFileDialog1.FilterIndex = filterIndex;
            openFileDialog1.RestoreDirectory = true;

            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                openFile(openFileDialog1.FileName);
                filterIndex = openFileDialog1.FilterIndex;
            }

        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            saveAction();
        }

        bool saveAction()
        {
            if (textFilename == null || textFilename.Length == 0)
            {
                return SaveFileDlg();
            }
            else
            {
                return SaveTextFile();
            }
        }

        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            SaveFileDlg();
        }

        bool SaveFileDlg()
        {
            SaveFileDialog saveFileDialog1 = new SaveFileDialog();
            saveFileDialog1.Title = Properties.Resources.Save_As;
            saveFileDialog1.Filter = "UTF-8 Text Files (*.txt)|*.txt|All Files (*.*)|*.*";
            saveFileDialog1.FilterIndex = 1;
            saveFileDialog1.RestoreDirectory = true;
            if (textFilename != null && textFilename.Length > 1)
            {
                saveFileDialog1.InitialDirectory = Path.GetDirectoryName(textFilename);
                saveFileDialog1.FileName = Path.GetFileName(textFilename);
            }

            if (saveFileDialog1.ShowDialog() == DialogResult.OK)
            {
                textFilename = saveFileDialog1.FileName;
                return SaveTextFile();
            }
            else
            {
                return false;
            }
        }

        bool SaveTextFile()
        {
            this.Cursor = Cursors.WaitCursor;

            try
            {
                using (StreamWriter sw = new StreamWriter(textFilename, false, new System.Text.UTF8Encoding()))
                {
                    sw.Write(this.textBox1.Text);
                    updateMRUList(textFilename);
                }
            }
            catch (Exception exc)
            {
                MessageBox.Show(exc.Message, strProgName, MessageBoxButtons.OK, MessageBoxIcon.Error);
            }

            textModified = false;
            this.textBox1.Modified = false;
            this.Cursor = Cursors.Default;

            return true;
        }

        /// <summary>
        /// Update MRU Submenu.
        /// </summary>
        private void updateMRUMenu()
        {
            this.recentFilesToolStripMenuItem.DropDownItems.Clear();

            if (mruList.Count == 0)
            {
                this.recentFilesToolStripMenuItem.DropDownItems.Add(Properties.Resources.No_Recent_Files);
            }
            else
            {
                EventHandler eh = new EventHandler(MenuRecentFilesOnClick);

                foreach (string fileName in mruList)
                {
                    ToolStripItem item = this.recentFilesToolStripMenuItem.DropDownItems.Add(fileName);
                    item.Click += eh;
                }
                this.recentFilesToolStripMenuItem.DropDownItems.Add("-");
                strClearRecentFiles = Properties.Resources.Clear_Recent_Files;
                ToolStripItem clearItem = this.recentFilesToolStripMenuItem.DropDownItems.Add(strClearRecentFiles);
                clearItem.Click += eh;
            }
        }

        void MenuRecentFilesOnClick(object obj, EventArgs ea)
        {
            ToolStripDropDownItem item = (ToolStripDropDownItem)obj;
            string fileName = item.Text;

            if (fileName == strClearRecentFiles)
            {
                mruList.Clear();
                this.recentFilesToolStripMenuItem.DropDownItems.Clear();
                this.recentFilesToolStripMenuItem.DropDownItems.Add(Properties.Resources.No_Recent_Files);
            }
            else
            {
                openFile(fileName);
            }
        }

        /// <summary>
        /// Update MRU List.
        /// </summary>
        /// <param name="fileName"></param>
        private void updateMRUList(String fileName)
        {
            if (mruList.Contains(fileName))
            {
                mruList.Remove(fileName);
            }

            mruList.Insert(0, fileName);

            if (mruList.Count > 10)
            {
                mruList.RemoveAt(10);
            }

            updateMRUMenu();
        }

        private void toolStripCbLang_SelectedIndexChanged(object sender, EventArgs e)
        {
            curLangCode = installedLanguageCodes[this.toolStripCbLang.SelectedIndex];

            // Hide Viet Input Method submenu if selected OCR Language is not Vietnamese
            bool vie = curLangCode.StartsWith("vie");
            VietKeyHandler.VietModeEnabled = vie;
            this.vietInputMethodToolStripMenuItem.Visible = vie;
            this.toolStripMenuItemInputMethod.Visible = vie;

            if (this.toolStripButtonSpellCheck.Checked)
            {
                this.toolStripButtonSpellCheck.PerformClick();
                this.toolStripButtonSpellCheck.PerformClick();
            }
        }

        private void toolStrip1_ItemClicked(object sender, ToolStripItemClickedEventArgs e)
        {
            if (!(e.ClickedItem is ToolStripButton)) return;

            ToolStripButton tsb = (ToolStripButton)e.ClickedItem;
            ToolStripMenuItem mi = (ToolStripMenuItem)tsb.Tag;
            if (mi != null)
                mi.PerformClick();
        }

        private void toolStripBtnClear_Click(object sender, EventArgs e)
        {
            if (textFilename == null || OkToTrash())
            {
                this.textBox1.Clear();
                this.textBox1.ClearUndo();
                textModified = false;
                this.textBox1.Modified = false;
                textFilename = null;
            }
        }

        private void quitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (OkToTrash())
            {
                Application.Exit();
            }
        }

        private void toolStripBtnPrev_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            imageIndex--;
            if (imageIndex < 0)
            {
                imageIndex = 0;
            }
            else
            {
                this.toolStripStatusLabel1.Text = null;
                displayImage();

                // recalculate scale factors if in Fit Image mode
                if (this.pictureBox1.SizeMode == PictureBoxSizeMode.StretchImage)
                {
                    scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
                    scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
                }
            }
            setButton();
        }

        private void toolStripBtnNext_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            imageIndex++;
            if (imageIndex > imageTotal - 1)
            {
                imageIndex = imageTotal - 1;
            }
            else
            {
                this.toolStripStatusLabel1.Text = null;
                displayImage();

                // recalculate scale factors if in Fit Image mode
                if (this.pictureBox1.SizeMode == PictureBoxSizeMode.StretchImage)
                {
                    scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
                    scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
                }
            }
            setButton();
        }

        protected bool OkToTrash()
        {
            if (!textModified)
            {
                return true;
            }

            DialogResult dr =
                MessageBox.Show(Properties.Resources.Do_you_want_to_save_the_changes_to_ + FileTitle() + "?",
                strProgName,
                MessageBoxButtons.YesNoCancel,
                MessageBoxIcon.Exclamation);
            switch (dr)
            {
                case DialogResult.Yes:
                    return saveAction();
                case DialogResult.No:
                    return true;
                case DialogResult.Cancel:
                    return false;
            }
            return false;
        }
        protected string FileTitle()
        {
            return (this.textFilename != null && this.textFilename.Length > 1) ?
                Path.GetFileName(this.textFilename) : Properties.Resources.Untitled;
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
            this.toolStripBtnZoomIn.Enabled = false;
            this.toolStripBtnZoomOut.Enabled = false;
            curScrollPos = this.splitContainer2.Panel2.AutoScrollPosition;
            this.splitContainer2.Panel2.AutoScrollPosition = Point.Empty;
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
            this.toolStripBtnZoomIn.Enabled = true;
            this.toolStripBtnZoomOut.Enabled = true;

            this.pictureBox1.Deselect();
            this.pictureBox1.Size = this.pictureBox1.Image.Size;
            this.pictureBox1.Dock = DockStyle.None;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.Normal;
            scaleX = scaleY = 1f;
            this.centerPicturebox();
            this.splitContainer2.Panel2.AutoScrollPosition = new Point(Math.Abs(curScrollPos.X), Math.Abs(curScrollPos.Y));
        }

        /// <summary>
        /// Opens image or text file.
        /// </summary>
        /// <param name="selectedFile"></param>
        public void openFile(string selectedFile)
        {
            // if text file, load it into textbox
            if (selectedFile.EndsWith(".txt"))
            {
                if (!OkToTrash())
                    return;

                try
                {
                    using (StreamReader sr = new StreamReader(selectedFile, Encoding.UTF8, true))
                    {
                        textModified = false;
                        this.textBox1.Text = sr.ReadToEnd();
                        updateMRUList(selectedFile);
                        textFilename = selectedFile;
                        this.textBox1.Modified = false;
                    }
                }
                catch
                {
                    //ignore
                }
                return;
            }

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

            this.backgroundWorkerLoad.RunWorkerAsync(selectedFile);
            updateMRUList(selectedFile);
        }

        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        private void backgroundWorkerLoad_DoWork(object sender, DoWorkEventArgs e)
        {
            string selectedImageFile = (string)e.Argument;
            FileInfo imageFile = new FileInfo(selectedImageFile);
            imageList = ImageIOHelper.GetImageList(imageFile);
            e.Result = imageFile;
        }

        private void backgroundWorkerLoad_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
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
                this.toolStripStatusLabel1.Text = "Image loading " + Properties.Resources.canceled;
            }
            else
            {
                // Finally, handle the case where the operation succeeded.
                loadImage((FileInfo)e.Result);
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

            this.pictureBox1.Dock = DockStyle.None;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.Normal;
            scaleX = scaleY = 1f;
            this.splitContainer2.Panel2.AutoScrollPosition = curScrollPos = Point.Empty;

            displayImage();

            this.Text = imageFile.Name + " - " + strProgName;
            //this.toolStripStatusLabel1.Text = null;
            this.pictureBox1.Deselect();

            this.toolStripBtnFitImage.Enabled = true;
            this.toolStripBtnActualSize.Enabled = false;
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
            if (this.scaleX != 1f)
            {
                this.pictureBox1.Width = Convert.ToInt32(this.pictureBox1.Width / scaleX);
                this.pictureBox1.Height = Convert.ToInt32(this.pictureBox1.Height / scaleY);
            }
            this.splitContainer2.Panel2.AutoScrollPosition = Point.Empty;
            this.centerPicturebox();
        }

        private void centerPicturebox()
        {
            int x = 0;
            int y = 0;

            if (this.pictureBox1.Width < this.splitContainer2.Panel2.Width)
            {
                x = (this.splitContainer2.Panel2.Width - this.pictureBox1.Width) / 2;
            }

            if (this.pictureBox1.Height < this.splitContainer2.Panel2.Height)
            {
                y = (this.splitContainer2.Panel2.Height - this.pictureBox1.Height) / 2;
            }

            this.pictureBox1.Location = new Point(x, y);
            this.pictureBox1.Invalidate();
        }

        protected virtual void scanToolStripMenuItem_Click(object sender, EventArgs e)
        {
            // To be implemented in subclass.
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

                bool isTextSelected = false;
                if (this.textBox1.SelectionLength > 0)
                {
                    isTextSelected = true;
                }

                this.textBox1.Focus();

                if (!isTextSelected)
                {
                    this.textBox1.SelectionLength = 0;
                }
            }
        }

        private void toolStripBtnRotateCCW_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            // Rotating 270 degrees is equivalent to rotating -90 degrees.
            imageList[imageIndex].RotateFlip(RotateFlipType.Rotate270FlipNone);
            this.pictureBox1.Image = new Bitmap(imageList[imageIndex]);
            adjustPictureBoxAfterFlip();
        }

        private void toolStripBtnRotateCW_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            imageList[imageIndex].RotateFlip(RotateFlipType.Rotate90FlipNone);
            this.pictureBox1.Image = new Bitmap(imageList[imageIndex]);
            adjustPictureBoxAfterFlip();
        }

        private void adjustPictureBoxAfterFlip()
        {
            this.pictureBox1.Size = new Size(this.pictureBox1.Height, this.pictureBox1.Width);
            this.pictureBox1.Refresh();
            // recalculate scale factors if in Fit Image mode
            if (this.pictureBox1.SizeMode == PictureBoxSizeMode.StretchImage)
            {
                scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
                scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
            }
            this.centerPicturebox();
        }

        private void toolStripBtnZoomIn_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            IsFitForZoomIn = true;
            this.pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;

            // Zoom works best if you first fit the image according to its true aspect ratio.
            Fit();
            // Make the PictureBox dimensions larger by 25% to effect the Zoom.
            this.pictureBox1.Width = Convert.ToInt32(this.pictureBox1.Width * ZOOM_FACTOR);
            this.pictureBox1.Height = Convert.ToInt32(this.pictureBox1.Height * ZOOM_FACTOR);
            scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
            scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
            this.centerPicturebox();
        }

        private void toolStripBtnZoomOut_Click(object sender, EventArgs e)
        {
            this.pictureBox1.Deselect();
            // StretchImage SizeMode works best for zooming.
            this.pictureBox1.SizeMode = PictureBoxSizeMode.StretchImage;
            // Zoom works best if you first fit the image according to its true aspect ratio.
            Fit();
            // Make the PictureBox dimensions smaller by 25% to effect the Zoom.
            this.pictureBox1.Width = Convert.ToInt32(this.pictureBox1.Width / ZOOM_FACTOR);
            this.pictureBox1.Height = Convert.ToInt32(this.pictureBox1.Height / ZOOM_FACTOR);
            scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
            scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
            this.centerPicturebox();
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
                    helpForm.Text = Properties.Resources.VietOCR_Help;
                }
            }

            this.contextMenuStrip1.ChangeUILanguage();
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
            string[] fileNames = ((string)regkey.GetValue(strMruList, String.Empty)).Split(new[] { Path.PathSeparator }, StringSplitOptions.RemoveEmptyEntries);

            foreach (string fileName in fileNames)
            {
                mruList.Add(fileName);
            }
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
            StringBuilder strB = new StringBuilder();
            foreach (string name in mruList)
            {
                strB.Append(name).Append(';');
            }
            regkey.SetValue(strMruList, strB.ToString());
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

        protected virtual void mergePdfToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected override void OnKeyDown(KeyEventArgs e)
        {
            if (e.Control && e.KeyCode == Keys.V)
            {
                Image image = ImageHelper.GetClipboardImage();
                if (image != null)
                {
                    string tempFileName = Path.GetTempFileName();
                    File.Delete(tempFileName);
                    tempFileName = Path.ChangeExtension(tempFileName, ".png");
                    tempFileCollection.AddFile(tempFileName, false);
                    image.Save(tempFileName, ImageFormat.Png);
                    openFile(tempFileName);
                    e.Handled = true;
                }
            }
            base.OnKeyDown(e);
        }

        protected override void OnResize(EventArgs e)
        {
            base.OnResize(e);
            if (this.pictureBox1.Image != null)
            {
                this.pictureBox1.Deselect();
                scaleX = (float)this.pictureBox1.Image.Width / (float)this.pictureBox1.Width;
                scaleY = (float)this.pictureBox1.Image.Height / (float)this.pictureBox1.Height;
                this.centerPicturebox();
            }
        }

        private void textBox1_ModifiedChanged(object sender, EventArgs e)
        {
            if (textModified && !this.textBox1.Modified)
            {
                this.textBox1.Modified = textModified;
            }
            textModified = this.textBox1.Modified;
            this.toolStripBtnSave.Enabled = this.textBox1.Modified;
            this.saveToolStripMenuItem.Enabled = this.textBox1.Modified;
        }

        protected virtual void metadataToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void screenshotModeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void toolStripButtonSpellCheck_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        private void textBox1_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                pointClicked = e.Location;
            }
        }

        protected virtual void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {
            // To be implemented in subclass.
        }

        private void GUI_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.KeyCode == Keys.F7)
            {
                this.toolStripButtonSpellCheck.PerformClick();
            }
        }

        protected virtual void downloadLangDataToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }

        protected virtual void deskewToolStripMenuItem_Click(object sender, EventArgs e)
        {
            MessageBox.Show(TO_BE_IMPLEMENTED, strProgName);
        }
    }
}
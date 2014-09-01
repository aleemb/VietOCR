using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Windows.Forms;
using VietOCR.NET.Postprocessing;
using VietOCR.NET.Controls;
using System.IO;
using Microsoft.Win32;

namespace VietOCR.NET
{
    public partial class GUIWithCommand : VietOCR.NET.GUI
    {
        const string strDangAmbigsPath = "DangAmbigsPath";
        const string strDangAmbigsOn = "DangAmbigsOn";

        protected string dangAmbigsPath;
        protected bool dangAmbigsOn;

        public GUIWithCommand()
        {
            InitializeComponent();
        }

        protected override void oCRToolStripMenuItem_Click(object sender, EventArgs e)
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

        protected override void oCRAllPagesToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (this.pictureBox1.Image == null)
            {
                MessageBox.Show(this, Properties.Resources.LoadImage, strProgName);
                return;
            }

            this.toolStripBtnOCR.Visible = false;
            this.toolStripButtonCancelOCR.Visible = true;
            this.toolStripButtonCancelOCR.Enabled = true;
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
                backgroundWorkerOcr.RunWorkerAsync(entity);
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
            }
        }

        protected override void toolStripButtonCancelOCR_Click(object sender, EventArgs e)
        {
            backgroundWorkerOcr.CancelAsync();
            this.toolStripButtonCancelOCR.Enabled = false;
        }

        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        private void backgroundWorkerOcr_DoWork(object sender, DoWorkEventArgs e)
        {
            // Get the BackgroundWorker that raised this event.
            BackgroundWorker worker = sender as BackgroundWorker;

            OCRImageEntity entity = (OCRImageEntity)e.Argument;
            OCR ocrEngine = new OCR();

            // Assign the result of the computation to the Result property of the DoWorkEventArgs
            // object. This is will be available to the RunWorkerCompleted eventhandler.
            //e.Result = ocrEngine.RecognizeText(entity.ClonedImages, entity.Lang, entity.Rect, worker, e);

            for (int i = 0; i < entity.ClonedImages.Count; i++)
            {
                if (worker.CancellationPending)
                {
                    e.Cancel = true;
                    break;
                }

                string result = ocrEngine.RecognizeText(((List<Image>)entity.ClonedImages).GetRange(i, 1), entity.Lang, entity.Rect, worker, e);
                worker.ReportProgress(i, result); // i is not really percentage
            }
        }

        private void backgroundWorkerOcr_ProgressChanged(object sender, ProgressChangedEventArgs e)
        {
            //this.toolStripProgressBar1.Value = e.ProgressPercentage;
            this.textBox1.AppendText((string)e.UserState);
        }

        private void backgroundWorkerOcr_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
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
                this.toolStripStatusLabel1.Text = "OCR " + Properties.Resources.canceled;
            }
            else
            {
                // Finally, handle the case where the operation succeeded.
                this.toolStripStatusLabel1.Text = Properties.Resources.OCRcompleted;
                this.textBox1.Focus();
                //this.textBox1.AppendText(e.Result.ToString());
            }

            this.Cursor = Cursors.Default;
            this.pictureBox1.UseWaitCursor = false;
            this.textBox1.Cursor = Cursors.Default;
            this.toolStripBtnOCR.Visible = true;
            this.toolStripBtnOCR.Enabled = true;
            this.oCRToolStripMenuItem.Enabled = true;
            this.oCRAllPagesToolStripMenuItem.Enabled = true;
            this.toolStripButtonCancelOCR.Visible = false;
        }

        protected override void postprocessToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (curLangCode == null) return;

            this.toolStripStatusLabel1.Text = Properties.Resources.Correcting_errors;
            this.Cursor = Cursors.WaitCursor;
            this.pictureBox1.UseWaitCursor = true;
            this.textBox1.Cursor = Cursors.WaitCursor;
            this.postprocessToolStripMenuItem.Enabled = false;
            this.toolStripProgressBar1.Enabled = true;
            this.toolStripProgressBar1.Visible = true;
            this.toolStripProgressBar1.Style = ProgressBarStyle.Marquee;

            this.backgroundWorkerCorrect.RunWorkerAsync(this.textBox1.SelectionLength > 0 ? this.textBox1.SelectedText : this.textBox1.Text);
        }
        
        private void backgroundWorkerCorrect_DoWork(object sender, DoWorkEventArgs e)
        {
            // Perform post-OCR corrections
            string text = (string)e.Argument;
            e.Result = Processor.PostProcess(text, curLangCode, dangAmbigsPath, dangAmbigsOn);
        }

        private void backgroundWorkerCorrect_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
        {
            this.toolStripProgressBar1.Enabled = false;
            this.toolStripProgressBar1.Visible = false;

            // First, handle the case where an exception was thrown.
            if (e.Error != null)
            {
                Console.WriteLine(e.Error.StackTrace);
                string why;

                if (e.Error.GetBaseException() is NotSupportedException)
                {
                    why = string.Format("Post-processing not supported for {0} language.\nYou can provide one via a \"{1}.DangAmbigs.txt\" file.", this.toolStripCbLang.Text, curLangCode);
                }
                else
                {
                    why = e.Error.Message;
                }
                MessageBox.Show(this, why, strProgName);
            }
            else if (e.Cancelled)
            {
                // Next, handle the case where the user canceled 
                // the operation.
                // Note that due to a race condition in 
                // the DoWork event handler, the Cancelled
                // flag may not have been set, even though
                // CancelAsync was called.
                this.toolStripStatusLabel1.Text = "Post-OCR correction" + Properties.Resources.canceled;
            }
            else
            {
                // Finally, handle the case where the operation 
                // succeeded.
                string result = e.Result.ToString();

                if (this.textBox1.SelectionLength > 0)
                {
                    int start = this.textBox1.SelectionStart;
                    this.textBox1.SelectedText = result;
                    this.textBox1.Select(start, result.Length);
                }
                else
                {
                    this.textBox1.Text = result;
                }
                this.toolStripStatusLabel1.Text = Properties.Resources.Correcting_completed;
            }

            this.Cursor = Cursors.Default;
            this.pictureBox1.UseWaitCursor = false;
            this.textBox1.Cursor = Cursors.Default;
            this.postprocessToolStripMenuItem.Enabled = true;
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);

            String workingDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            dangAmbigsPath = (string)regkey.GetValue(strDangAmbigsPath, Path.Combine(workingDir, "Data"));
            dangAmbigsOn = Convert.ToBoolean(
                (int)regkey.GetValue(strDangAmbigsOn, Convert.ToInt32(true)));
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);

            regkey.SetValue(strDangAmbigsPath, dangAmbigsPath);
            regkey.SetValue(strDangAmbigsOn, Convert.ToInt32(dangAmbigsOn));
        }
    }
}

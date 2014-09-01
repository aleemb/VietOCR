namespace VietOCR.NET
{
    partial class GUIWithCommand
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.backgroundWorkerCorrect = new System.ComponentModel.BackgroundWorker();
            this.backgroundWorkerOcr = new System.ComponentModel.BackgroundWorker();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.SuspendLayout();
            // 
            // backgroundWorkerCorrect
            // 
            this.backgroundWorkerCorrect.WorkerReportsProgress = true;
            this.backgroundWorkerCorrect.WorkerSupportsCancellation = true;
            this.backgroundWorkerCorrect.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorkerCorrect_DoWork);
            this.backgroundWorkerCorrect.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorkerCorrect_RunWorkerCompleted);
            // 
            // backgroundWorkerOcr
            // 
            this.backgroundWorkerOcr.WorkerReportsProgress = true;
            this.backgroundWorkerOcr.WorkerSupportsCancellation = true;
            this.backgroundWorkerOcr.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorkerOcr_DoWork);
            this.backgroundWorkerOcr.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorkerOcr_RunWorkerCompleted);
            this.backgroundWorkerOcr.ProgressChanged += new System.ComponentModel.ProgressChangedEventHandler(this.backgroundWorkerOcr_ProgressChanged);
            // 
            // GUIWithCommand
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.ClientSize = new System.Drawing.Size(1284, 835);
            this.Location = new System.Drawing.Point(0, 0);
            this.Name = "GUIWithCommand";
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.ComponentModel.BackgroundWorker backgroundWorkerCorrect;
        private System.ComponentModel.BackgroundWorker backgroundWorkerOcr;
    }
}

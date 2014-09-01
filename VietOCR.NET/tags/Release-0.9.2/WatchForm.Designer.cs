namespace VietOCR.NET
{
    partial class WatchForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(WatchForm));
            this.textBoxWatch = new System.Windows.Forms.TextBox();
            this.textBoxOutput = new System.Windows.Forms.TextBox();
            this.btnWatch = new System.Windows.Forms.Button();
            this.btnOutput = new System.Windows.Forms.Button();
            this.checkBoxWatchEnabled = new System.Windows.Forms.CheckBox();
            this.btnClose = new System.Windows.Forms.Button();
            this.label1 = new System.Windows.Forms.Label();
            this.label2 = new System.Windows.Forms.Label();
            this.btnOK = new System.Windows.Forms.Button();
            this.folderBrowserDialog1 = new System.Windows.Forms.FolderBrowserDialog();
            this.SuspendLayout();
            // 
            // textBoxWatch
            // 
            this.textBoxWatch.AccessibleDescription = null;
            this.textBoxWatch.AccessibleName = null;
            resources.ApplyResources(this.textBoxWatch, "textBoxWatch");
            this.textBoxWatch.BackColor = System.Drawing.SystemColors.ControlLightLight;
            this.textBoxWatch.BackgroundImage = null;
            this.textBoxWatch.Font = null;
            this.textBoxWatch.Name = "textBoxWatch";
            this.textBoxWatch.ReadOnly = true;
            // 
            // textBoxOutput
            // 
            this.textBoxOutput.AccessibleDescription = null;
            this.textBoxOutput.AccessibleName = null;
            resources.ApplyResources(this.textBoxOutput, "textBoxOutput");
            this.textBoxOutput.BackColor = System.Drawing.SystemColors.ControlLightLight;
            this.textBoxOutput.BackgroundImage = null;
            this.textBoxOutput.Font = null;
            this.textBoxOutput.Name = "textBoxOutput";
            this.textBoxOutput.ReadOnly = true;
            // 
            // btnWatch
            // 
            this.btnWatch.AccessibleDescription = null;
            this.btnWatch.AccessibleName = null;
            resources.ApplyResources(this.btnWatch, "btnWatch");
            this.btnWatch.BackgroundImage = null;
            this.btnWatch.Font = null;
            this.btnWatch.Name = "btnWatch";
            this.btnWatch.UseVisualStyleBackColor = true;
            this.btnWatch.Click += new System.EventHandler(this.btnWatch_Click);
            // 
            // btnOutput
            // 
            this.btnOutput.AccessibleDescription = null;
            this.btnOutput.AccessibleName = null;
            resources.ApplyResources(this.btnOutput, "btnOutput");
            this.btnOutput.BackgroundImage = null;
            this.btnOutput.Font = null;
            this.btnOutput.Name = "btnOutput";
            this.btnOutput.UseVisualStyleBackColor = true;
            this.btnOutput.Click += new System.EventHandler(this.btnOutput_Click);
            // 
            // checkBoxWatchEnabled
            // 
            this.checkBoxWatchEnabled.AccessibleDescription = null;
            this.checkBoxWatchEnabled.AccessibleName = null;
            resources.ApplyResources(this.checkBoxWatchEnabled, "checkBoxWatchEnabled");
            this.checkBoxWatchEnabled.BackgroundImage = null;
            this.checkBoxWatchEnabled.Font = null;
            this.checkBoxWatchEnabled.Name = "checkBoxWatchEnabled";
            this.checkBoxWatchEnabled.UseVisualStyleBackColor = true;
            // 
            // btnClose
            // 
            this.btnClose.AccessibleDescription = null;
            this.btnClose.AccessibleName = null;
            resources.ApplyResources(this.btnClose, "btnClose");
            this.btnClose.BackgroundImage = null;
            this.btnClose.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnClose.Font = null;
            this.btnClose.Name = "btnClose";
            this.btnClose.UseVisualStyleBackColor = true;
            // 
            // label1
            // 
            this.label1.AccessibleDescription = null;
            this.label1.AccessibleName = null;
            resources.ApplyResources(this.label1, "label1");
            this.label1.Font = null;
            this.label1.Name = "label1";
            // 
            // label2
            // 
            this.label2.AccessibleDescription = null;
            this.label2.AccessibleName = null;
            resources.ApplyResources(this.label2, "label2");
            this.label2.Font = null;
            this.label2.Name = "label2";
            // 
            // btnOK
            // 
            this.btnOK.AccessibleDescription = null;
            this.btnOK.AccessibleName = null;
            resources.ApplyResources(this.btnOK, "btnOK");
            this.btnOK.BackgroundImage = null;
            this.btnOK.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.btnOK.Font = null;
            this.btnOK.Name = "btnOK";
            this.btnOK.UseVisualStyleBackColor = true;
            // 
            // folderBrowserDialog1
            // 
            resources.ApplyResources(this.folderBrowserDialog1, "folderBrowserDialog1");
            // 
            // WatchForm
            // 
            this.AccessibleDescription = null;
            this.AccessibleName = null;
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackgroundImage = null;
            this.Controls.Add(this.btnOK);
            this.Controls.Add(this.label2);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.btnClose);
            this.Controls.Add(this.checkBoxWatchEnabled);
            this.Controls.Add(this.btnOutput);
            this.Controls.Add(this.btnWatch);
            this.Controls.Add(this.textBoxOutput);
            this.Controls.Add(this.textBoxWatch);
            this.Font = null;
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedDialog;
            this.Icon = null;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "WatchForm";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.TextBox textBoxWatch;
        private System.Windows.Forms.TextBox textBoxOutput;
        private System.Windows.Forms.Button btnWatch;
        private System.Windows.Forms.Button btnOutput;
        private System.Windows.Forms.CheckBox checkBoxWatchEnabled;
        private System.Windows.Forms.Button btnClose;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.Label label2;
        private System.Windows.Forms.Button btnOK;
        private System.Windows.Forms.FolderBrowserDialog folderBrowserDialog1;
    }
}
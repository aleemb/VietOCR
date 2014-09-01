namespace VietOCR.NET
{
    partial class GUIWithInputMethod
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GUIWithInputMethod));
            this.toolStripMenuItem3 = new System.Windows.Forms.ToolStripSeparator();
            this.vietInputMethodToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
            this.uILanguageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.SuspendLayout();
            // 
            // settingsToolStripMenuItem
            // 
            this.settingsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripMenuItem3,
            this.vietInputMethodToolStripMenuItem,
            this.toolStripMenuItem1,
            this.uILanguageToolStripMenuItem
            });

            // 
            // toolStripMenuItem3
            // 
            this.toolStripMenuItem3.Name = "toolStripMenuItem3";
            resources.ApplyResources(this.toolStripMenuItem3, "toolStripMenuItem3");
            // 
            // vietInputMethodToolStripMenuItem
            // 
            this.vietInputMethodToolStripMenuItem.Name = "vietInputMethodToolStripMenuItem";
            resources.ApplyResources(this.vietInputMethodToolStripMenuItem, "vietInputMethodToolStripMenuItem");
            // 
            // toolStripMenuItem1
            // 
            this.toolStripMenuItem1.Name = "toolStripMenuItem1";
            resources.ApplyResources(this.toolStripMenuItem1, "toolStripMenuItem1");
            // 
            // uILanguageToolStripMenuItem
            // 
            this.uILanguageToolStripMenuItem.Name = "uILanguageToolStripMenuItem";
            resources.ApplyResources(this.uILanguageToolStripMenuItem, "uILanguageToolStripMenuItem");
            // 
            // GUIWithInputMethod
            // 
            resources.ApplyResources(this, "$this");
            this.Name = "GUIWithInputMethod";
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem3;
        private System.Windows.Forms.ToolStripMenuItem vietInputMethodToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem uILanguageToolStripMenuItem;
    }
}

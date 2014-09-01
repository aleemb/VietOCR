namespace VietOCR.NET
{
    partial class GUI
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GUI));
            this.splitContainer2 = new System.Windows.Forms.SplitContainer();
            this.lblCurIndex = new System.Windows.Forms.Label();
            this.pictureBox1 = new VietOCR.NET.Controls.ScrollablePictureBox();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.panel1 = new System.Windows.Forms.Panel();
            this.toolStrip2 = new System.Windows.Forms.ToolStrip();
            this.toolStripBtnPrev = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnNext = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator1 = new System.Windows.Forms.ToolStripSeparator();
            this.toolStripBtnFitImage = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnActualSize = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator2 = new System.Windows.Forms.ToolStripSeparator();
            this.toolStripBtnZoomIn = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnZoomOut = new System.Windows.Forms.ToolStripButton();
            this.toolStripSeparator3 = new System.Windows.Forms.ToolStripSeparator();
            this.toolStripBtnRotateCCW = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnRotateCW = new System.Windows.Forms.ToolStripButton();
            this.textBox1 = new System.Windows.Forms.TextBox();
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.scanToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.saveAsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem4 = new System.Windows.Forms.ToolStripSeparator();
            this.recentFilesToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
            this.quitToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.commandToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.oCRToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.oCRAllPagesToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem2 = new System.Windows.Forms.ToolStripSeparator();
            this.postprocessToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.imageToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.metadataToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem6 = new System.Windows.Forms.ToolStripSeparator();
            this.screenshotModeToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.formatToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.wordWrapToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.fontToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem5 = new System.Windows.Forms.ToolStripSeparator();
            this.changeCaseToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.removeLineBreaksToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.settingsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.optionsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolsToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.mergeTiffToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.mergePdfToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.splitPdfToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem1 = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem3 = new System.Windows.Forms.ToolStripSeparator();
            this.aboutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.toolStripStatusLabel1 = new System.Windows.Forms.ToolStripStatusLabel();
            this.toolStripProgressBar1 = new System.Windows.Forms.ToolStripProgressBar();
            this.toolStrip1 = new System.Windows.Forms.ToolStrip();
            this.toolStripBtnOpen = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnScan = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnSave = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnOCR = new System.Windows.Forms.ToolStripButton();
            this.toolStripButtonCancelOCR = new System.Windows.Forms.ToolStripButton();
            this.toolStripBtnClear = new System.Windows.Forms.ToolStripButton();
            this.toolStripCbLang = new System.Windows.Forms.ToolStripComboBox();
            this.toolStripLabel1 = new System.Windows.Forms.ToolStripLabel();
            this.backgroundWorkerScan = new System.ComponentModel.BackgroundWorker();
            this.backgroundWorkerLoad = new System.ComponentModel.BackgroundWorker();
            this.splitContainer2.Panel1.SuspendLayout();
            this.splitContainer2.Panel2.SuspendLayout();
            this.splitContainer2.SuspendLayout();
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).BeginInit();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.panel1.SuspendLayout();
            this.toolStrip2.SuspendLayout();
            this.menuStrip1.SuspendLayout();
            this.statusStrip1.SuspendLayout();
            this.toolStrip1.SuspendLayout();
            this.SuspendLayout();
            // 
            // splitContainer2
            // 
            this.splitContainer2.AccessibleDescription = null;
            this.splitContainer2.AccessibleName = null;
            resources.ApplyResources(this.splitContainer2, "splitContainer2");
            this.splitContainer2.BackgroundImage = null;
            this.splitContainer2.FixedPanel = System.Windows.Forms.FixedPanel.Panel1;
            this.splitContainer2.Font = null;
            this.splitContainer2.Name = "splitContainer2";
            // 
            // splitContainer2.Panel1
            // 
            this.splitContainer2.Panel1.AccessibleDescription = null;
            this.splitContainer2.Panel1.AccessibleName = null;
            resources.ApplyResources(this.splitContainer2.Panel1, "splitContainer2.Panel1");
            this.splitContainer2.Panel1.BackgroundImage = null;
            this.splitContainer2.Panel1.Controls.Add(this.lblCurIndex);
            this.splitContainer2.Panel1.Font = null;
            // 
            // splitContainer2.Panel2
            // 
            this.splitContainer2.Panel2.AccessibleDescription = null;
            this.splitContainer2.Panel2.AccessibleName = null;
            this.splitContainer2.Panel2.AllowDrop = true;
            resources.ApplyResources(this.splitContainer2.Panel2, "splitContainer2.Panel2");
            this.splitContainer2.Panel2.BackgroundImage = null;
            this.splitContainer2.Panel2.Controls.Add(this.pictureBox1);
            this.splitContainer2.Panel2.Font = null;
            this.splitContainer2.Panel2.DragOver += new System.Windows.Forms.DragEventHandler(this.splitContainer2_Panel2_DragOver);
            this.splitContainer2.Panel2.DragDrop += new System.Windows.Forms.DragEventHandler(this.splitContainer2_Panel2_DragDrop);
            // 
            // lblCurIndex
            // 
            this.lblCurIndex.AccessibleDescription = null;
            this.lblCurIndex.AccessibleName = null;
            resources.ApplyResources(this.lblCurIndex, "lblCurIndex");
            this.lblCurIndex.Font = null;
            this.lblCurIndex.Name = "lblCurIndex";
            // 
            // pictureBox1
            // 
            this.pictureBox1.AccessibleDescription = null;
            this.pictureBox1.AccessibleName = null;
            resources.ApplyResources(this.pictureBox1, "pictureBox1");
            this.pictureBox1.BackgroundImage = null;
            this.pictureBox1.Font = null;
            this.pictureBox1.ImageLocation = null;
            this.pictureBox1.Name = "pictureBox1";
            this.pictureBox1.TabStop = false;
            // 
            // splitContainer1
            // 
            this.splitContainer1.AccessibleDescription = null;
            this.splitContainer1.AccessibleName = null;
            resources.ApplyResources(this.splitContainer1, "splitContainer1");
            this.splitContainer1.BackgroundImage = null;
            this.splitContainer1.Font = null;
            this.splitContainer1.Name = "splitContainer1";
            // 
            // splitContainer1.Panel1
            // 
            this.splitContainer1.Panel1.AccessibleDescription = null;
            this.splitContainer1.Panel1.AccessibleName = null;
            resources.ApplyResources(this.splitContainer1.Panel1, "splitContainer1.Panel1");
            this.splitContainer1.Panel1.BackgroundImage = null;
            this.splitContainer1.Panel1.Controls.Add(this.panel1);
            this.splitContainer1.Panel1.Controls.Add(this.toolStrip2);
            this.splitContainer1.Panel1.Font = null;
            // 
            // splitContainer1.Panel2
            // 
            this.splitContainer1.Panel2.AccessibleDescription = null;
            this.splitContainer1.Panel2.AccessibleName = null;
            resources.ApplyResources(this.splitContainer1.Panel2, "splitContainer1.Panel2");
            this.splitContainer1.Panel2.BackgroundImage = null;
            this.splitContainer1.Panel2.Controls.Add(this.textBox1);
            this.splitContainer1.Panel2.Font = null;
            // 
            // panel1
            // 
            this.panel1.AccessibleDescription = null;
            this.panel1.AccessibleName = null;
            resources.ApplyResources(this.panel1, "panel1");
            this.panel1.BackgroundImage = null;
            this.panel1.Controls.Add(this.splitContainer2);
            this.panel1.Font = null;
            this.panel1.Name = "panel1";
            // 
            // toolStrip2
            // 
            this.toolStrip2.AccessibleDescription = null;
            this.toolStrip2.AccessibleName = null;
            resources.ApplyResources(this.toolStrip2, "toolStrip2");
            this.toolStrip2.BackgroundImage = null;
            this.toolStrip2.Font = null;
            this.toolStrip2.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripBtnPrev,
            this.toolStripBtnNext,
            this.toolStripSeparator1,
            this.toolStripBtnFitImage,
            this.toolStripBtnActualSize,
            this.toolStripSeparator2,
            this.toolStripBtnZoomIn,
            this.toolStripBtnZoomOut,
            this.toolStripSeparator3,
            this.toolStripBtnRotateCCW,
            this.toolStripBtnRotateCW});
            this.toolStrip2.Name = "toolStrip2";
            // 
            // toolStripBtnPrev
            // 
            this.toolStripBtnPrev.AccessibleDescription = null;
            this.toolStripBtnPrev.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnPrev, "toolStripBtnPrev");
            this.toolStripBtnPrev.BackgroundImage = null;
            this.toolStripBtnPrev.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnPrev.Name = "toolStripBtnPrev";
            this.toolStripBtnPrev.Click += new System.EventHandler(this.toolStripBtnPrev_Click);
            // 
            // toolStripBtnNext
            // 
            this.toolStripBtnNext.AccessibleDescription = null;
            this.toolStripBtnNext.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnNext, "toolStripBtnNext");
            this.toolStripBtnNext.BackgroundImage = null;
            this.toolStripBtnNext.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnNext.Name = "toolStripBtnNext";
            this.toolStripBtnNext.Click += new System.EventHandler(this.toolStripBtnNext_Click);
            // 
            // toolStripSeparator1
            // 
            this.toolStripSeparator1.AccessibleDescription = null;
            this.toolStripSeparator1.AccessibleName = null;
            resources.ApplyResources(this.toolStripSeparator1, "toolStripSeparator1");
            this.toolStripSeparator1.Name = "toolStripSeparator1";
            // 
            // toolStripBtnFitImage
            // 
            this.toolStripBtnFitImage.AccessibleDescription = null;
            this.toolStripBtnFitImage.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnFitImage, "toolStripBtnFitImage");
            this.toolStripBtnFitImage.BackgroundImage = null;
            this.toolStripBtnFitImage.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnFitImage.Name = "toolStripBtnFitImage";
            this.toolStripBtnFitImage.Click += new System.EventHandler(this.toolStripBtnFitImage_Click);
            // 
            // toolStripBtnActualSize
            // 
            this.toolStripBtnActualSize.AccessibleDescription = null;
            this.toolStripBtnActualSize.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnActualSize, "toolStripBtnActualSize");
            this.toolStripBtnActualSize.BackgroundImage = null;
            this.toolStripBtnActualSize.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnActualSize.Name = "toolStripBtnActualSize";
            this.toolStripBtnActualSize.Click += new System.EventHandler(this.toolStripBtnActualSize_Click);
            // 
            // toolStripSeparator2
            // 
            this.toolStripSeparator2.AccessibleDescription = null;
            this.toolStripSeparator2.AccessibleName = null;
            resources.ApplyResources(this.toolStripSeparator2, "toolStripSeparator2");
            this.toolStripSeparator2.Name = "toolStripSeparator2";
            // 
            // toolStripBtnZoomIn
            // 
            this.toolStripBtnZoomIn.AccessibleDescription = null;
            this.toolStripBtnZoomIn.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnZoomIn, "toolStripBtnZoomIn");
            this.toolStripBtnZoomIn.BackgroundImage = null;
            this.toolStripBtnZoomIn.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnZoomIn.Name = "toolStripBtnZoomIn";
            this.toolStripBtnZoomIn.Click += new System.EventHandler(this.toolStripBtnZoomIn_Click);
            // 
            // toolStripBtnZoomOut
            // 
            this.toolStripBtnZoomOut.AccessibleDescription = null;
            this.toolStripBtnZoomOut.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnZoomOut, "toolStripBtnZoomOut");
            this.toolStripBtnZoomOut.BackgroundImage = null;
            this.toolStripBtnZoomOut.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnZoomOut.Name = "toolStripBtnZoomOut";
            this.toolStripBtnZoomOut.Click += new System.EventHandler(this.toolStripBtnZoomOut_Click);
            // 
            // toolStripSeparator3
            // 
            this.toolStripSeparator3.AccessibleDescription = null;
            this.toolStripSeparator3.AccessibleName = null;
            resources.ApplyResources(this.toolStripSeparator3, "toolStripSeparator3");
            this.toolStripSeparator3.Name = "toolStripSeparator3";
            // 
            // toolStripBtnRotateCCW
            // 
            this.toolStripBtnRotateCCW.AccessibleDescription = null;
            this.toolStripBtnRotateCCW.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnRotateCCW, "toolStripBtnRotateCCW");
            this.toolStripBtnRotateCCW.BackgroundImage = null;
            this.toolStripBtnRotateCCW.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnRotateCCW.Name = "toolStripBtnRotateCCW";
            this.toolStripBtnRotateCCW.Click += new System.EventHandler(this.toolStripBtnRotateCCW_Click);
            // 
            // toolStripBtnRotateCW
            // 
            this.toolStripBtnRotateCW.AccessibleDescription = null;
            this.toolStripBtnRotateCW.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnRotateCW, "toolStripBtnRotateCW");
            this.toolStripBtnRotateCW.BackgroundImage = null;
            this.toolStripBtnRotateCW.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Image;
            this.toolStripBtnRotateCW.Name = "toolStripBtnRotateCW";
            this.toolStripBtnRotateCW.Click += new System.EventHandler(this.toolStripBtnRotateCW_Click);
            // 
            // textBox1
            // 
            this.textBox1.AccessibleDescription = null;
            this.textBox1.AccessibleName = null;
            this.textBox1.AllowDrop = true;
            resources.ApplyResources(this.textBox1, "textBox1");
            this.textBox1.BackgroundImage = null;
            this.textBox1.Name = "textBox1";
            this.textBox1.DragDrop += new System.Windows.Forms.DragEventHandler(this.splitContainer2_Panel2_DragDrop);
            this.textBox1.MouseEnter += new System.EventHandler(this.textBox1_MouseEnter);
            this.textBox1.ModifiedChanged += new System.EventHandler(this.textBox1_ModifiedChanged);
            this.textBox1.DragOver += new System.Windows.Forms.DragEventHandler(this.splitContainer2_Panel2_DragOver);
            // 
            // menuStrip1
            // 
            this.menuStrip1.AccessibleDescription = null;
            this.menuStrip1.AccessibleName = null;
            resources.ApplyResources(this.menuStrip1, "menuStrip1");
            this.menuStrip1.BackgroundImage = null;
            this.menuStrip1.Font = null;
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.commandToolStripMenuItem,
            this.imageToolStripMenuItem,
            this.formatToolStripMenuItem,
            this.settingsToolStripMenuItem,
            this.toolsToolStripMenuItem,
            this.helpToolStripMenuItem});
            this.menuStrip1.Name = "menuStrip1";
            // 
            // fileToolStripMenuItem
            // 
            this.fileToolStripMenuItem.AccessibleDescription = null;
            this.fileToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.fileToolStripMenuItem, "fileToolStripMenuItem");
            this.fileToolStripMenuItem.BackgroundImage = null;
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.openToolStripMenuItem,
            this.scanToolStripMenuItem,
            this.saveToolStripMenuItem,
            this.saveAsToolStripMenuItem,
            this.toolStripMenuItem4,
            this.recentFilesToolStripMenuItem,
            this.toolStripMenuItem1,
            this.quitToolStripMenuItem});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // openToolStripMenuItem
            // 
            this.openToolStripMenuItem.AccessibleDescription = null;
            this.openToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.openToolStripMenuItem, "openToolStripMenuItem");
            this.openToolStripMenuItem.BackgroundImage = null;
            this.openToolStripMenuItem.Name = "openToolStripMenuItem";
            this.openToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.openToolStripMenuItem.Click += new System.EventHandler(this.openToolStripMenuItem_Click);
            // 
            // scanToolStripMenuItem
            // 
            this.scanToolStripMenuItem.AccessibleDescription = null;
            this.scanToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.scanToolStripMenuItem, "scanToolStripMenuItem");
            this.scanToolStripMenuItem.BackgroundImage = null;
            this.scanToolStripMenuItem.Name = "scanToolStripMenuItem";
            this.scanToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.scanToolStripMenuItem.Click += new System.EventHandler(this.scanToolStripMenuItem_Click);
            // 
            // saveToolStripMenuItem
            // 
            this.saveToolStripMenuItem.AccessibleDescription = null;
            this.saveToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.saveToolStripMenuItem, "saveToolStripMenuItem");
            this.saveToolStripMenuItem.BackgroundImage = null;
            this.saveToolStripMenuItem.Name = "saveToolStripMenuItem";
            this.saveToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.saveToolStripMenuItem.Click += new System.EventHandler(this.saveToolStripMenuItem_Click);
            // 
            // saveAsToolStripMenuItem
            // 
            this.saveAsToolStripMenuItem.AccessibleDescription = null;
            this.saveAsToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.saveAsToolStripMenuItem, "saveAsToolStripMenuItem");
            this.saveAsToolStripMenuItem.BackgroundImage = null;
            this.saveAsToolStripMenuItem.Name = "saveAsToolStripMenuItem";
            this.saveAsToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.saveAsToolStripMenuItem.Click += new System.EventHandler(this.saveAsToolStripMenuItem_Click);
            // 
            // toolStripMenuItem4
            // 
            this.toolStripMenuItem4.AccessibleDescription = null;
            this.toolStripMenuItem4.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem4, "toolStripMenuItem4");
            this.toolStripMenuItem4.Name = "toolStripMenuItem4";
            // 
            // recentFilesToolStripMenuItem
            // 
            this.recentFilesToolStripMenuItem.AccessibleDescription = null;
            this.recentFilesToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.recentFilesToolStripMenuItem, "recentFilesToolStripMenuItem");
            this.recentFilesToolStripMenuItem.BackgroundImage = null;
            this.recentFilesToolStripMenuItem.Name = "recentFilesToolStripMenuItem";
            this.recentFilesToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // toolStripMenuItem1
            // 
            this.toolStripMenuItem1.AccessibleDescription = null;
            this.toolStripMenuItem1.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem1, "toolStripMenuItem1");
            this.toolStripMenuItem1.Name = "toolStripMenuItem1";
            // 
            // quitToolStripMenuItem
            // 
            this.quitToolStripMenuItem.AccessibleDescription = null;
            this.quitToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.quitToolStripMenuItem, "quitToolStripMenuItem");
            this.quitToolStripMenuItem.BackgroundImage = null;
            this.quitToolStripMenuItem.Name = "quitToolStripMenuItem";
            this.quitToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.quitToolStripMenuItem.Click += new System.EventHandler(this.quitToolStripMenuItem_Click);
            // 
            // commandToolStripMenuItem
            // 
            this.commandToolStripMenuItem.AccessibleDescription = null;
            this.commandToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.commandToolStripMenuItem, "commandToolStripMenuItem");
            this.commandToolStripMenuItem.BackgroundImage = null;
            this.commandToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.oCRToolStripMenuItem,
            this.oCRAllPagesToolStripMenuItem,
            this.toolStripMenuItem2,
            this.postprocessToolStripMenuItem});
            this.commandToolStripMenuItem.Name = "commandToolStripMenuItem";
            this.commandToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // oCRToolStripMenuItem
            // 
            this.oCRToolStripMenuItem.AccessibleDescription = null;
            this.oCRToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.oCRToolStripMenuItem, "oCRToolStripMenuItem");
            this.oCRToolStripMenuItem.BackgroundImage = null;
            this.oCRToolStripMenuItem.Name = "oCRToolStripMenuItem";
            this.oCRToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.oCRToolStripMenuItem.Click += new System.EventHandler(this.oCRToolStripMenuItem_Click);
            // 
            // oCRAllPagesToolStripMenuItem
            // 
            this.oCRAllPagesToolStripMenuItem.AccessibleDescription = null;
            this.oCRAllPagesToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.oCRAllPagesToolStripMenuItem, "oCRAllPagesToolStripMenuItem");
            this.oCRAllPagesToolStripMenuItem.BackgroundImage = null;
            this.oCRAllPagesToolStripMenuItem.Name = "oCRAllPagesToolStripMenuItem";
            this.oCRAllPagesToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.oCRAllPagesToolStripMenuItem.Click += new System.EventHandler(this.oCRAllPagesToolStripMenuItem_Click);
            // 
            // toolStripMenuItem2
            // 
            this.toolStripMenuItem2.AccessibleDescription = null;
            this.toolStripMenuItem2.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem2, "toolStripMenuItem2");
            this.toolStripMenuItem2.Name = "toolStripMenuItem2";
            // 
            // postprocessToolStripMenuItem
            // 
            this.postprocessToolStripMenuItem.AccessibleDescription = null;
            this.postprocessToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.postprocessToolStripMenuItem, "postprocessToolStripMenuItem");
            this.postprocessToolStripMenuItem.BackgroundImage = null;
            this.postprocessToolStripMenuItem.Name = "postprocessToolStripMenuItem";
            this.postprocessToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.postprocessToolStripMenuItem.Click += new System.EventHandler(this.postprocessToolStripMenuItem_Click);
            // 
            // imageToolStripMenuItem
            // 
            this.imageToolStripMenuItem.AccessibleDescription = null;
            this.imageToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.imageToolStripMenuItem, "imageToolStripMenuItem");
            this.imageToolStripMenuItem.BackgroundImage = null;
            this.imageToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.metadataToolStripMenuItem,
            this.toolStripMenuItem6,
            this.screenshotModeToolStripMenuItem});
            this.imageToolStripMenuItem.Name = "imageToolStripMenuItem";
            this.imageToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // metadataToolStripMenuItem
            // 
            this.metadataToolStripMenuItem.AccessibleDescription = null;
            this.metadataToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.metadataToolStripMenuItem, "metadataToolStripMenuItem");
            this.metadataToolStripMenuItem.BackgroundImage = null;
            this.metadataToolStripMenuItem.Name = "metadataToolStripMenuItem";
            this.metadataToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.metadataToolStripMenuItem.Click += new System.EventHandler(this.metadataToolStripMenuItem_Click);
            // 
            // toolStripMenuItem6
            // 
            this.toolStripMenuItem6.AccessibleDescription = null;
            this.toolStripMenuItem6.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem6, "toolStripMenuItem6");
            this.toolStripMenuItem6.Name = "toolStripMenuItem6";
            // 
            // screenshotModeToolStripMenuItem
            // 
            this.screenshotModeToolStripMenuItem.AccessibleDescription = null;
            this.screenshotModeToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.screenshotModeToolStripMenuItem, "screenshotModeToolStripMenuItem");
            this.screenshotModeToolStripMenuItem.BackgroundImage = null;
            this.screenshotModeToolStripMenuItem.Name = "screenshotModeToolStripMenuItem";
            this.screenshotModeToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.screenshotModeToolStripMenuItem.Click += new System.EventHandler(this.screenshotModeToolStripMenuItem_Click);
            // 
            // formatToolStripMenuItem
            // 
            this.formatToolStripMenuItem.AccessibleDescription = null;
            this.formatToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.formatToolStripMenuItem, "formatToolStripMenuItem");
            this.formatToolStripMenuItem.BackgroundImage = null;
            this.formatToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.wordWrapToolStripMenuItem,
            this.fontToolStripMenuItem,
            this.toolStripMenuItem5,
            this.changeCaseToolStripMenuItem,
            this.removeLineBreaksToolStripMenuItem});
            this.formatToolStripMenuItem.Name = "formatToolStripMenuItem";
            this.formatToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.formatToolStripMenuItem.DropDownOpening += new System.EventHandler(this.formatToolStripMenuItem_DropDownOpening);
            // 
            // wordWrapToolStripMenuItem
            // 
            this.wordWrapToolStripMenuItem.AccessibleDescription = null;
            this.wordWrapToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.wordWrapToolStripMenuItem, "wordWrapToolStripMenuItem");
            this.wordWrapToolStripMenuItem.BackgroundImage = null;
            this.wordWrapToolStripMenuItem.Name = "wordWrapToolStripMenuItem";
            this.wordWrapToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.wordWrapToolStripMenuItem.Click += new System.EventHandler(this.wordWrapToolStripMenuItem_Click);
            // 
            // fontToolStripMenuItem
            // 
            this.fontToolStripMenuItem.AccessibleDescription = null;
            this.fontToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.fontToolStripMenuItem, "fontToolStripMenuItem");
            this.fontToolStripMenuItem.BackgroundImage = null;
            this.fontToolStripMenuItem.Name = "fontToolStripMenuItem";
            this.fontToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.fontToolStripMenuItem.Click += new System.EventHandler(this.fontToolStripMenuItem_Click);
            // 
            // toolStripMenuItem5
            // 
            this.toolStripMenuItem5.AccessibleDescription = null;
            this.toolStripMenuItem5.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem5, "toolStripMenuItem5");
            this.toolStripMenuItem5.Name = "toolStripMenuItem5";
            // 
            // changeCaseToolStripMenuItem
            // 
            this.changeCaseToolStripMenuItem.AccessibleDescription = null;
            this.changeCaseToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.changeCaseToolStripMenuItem, "changeCaseToolStripMenuItem");
            this.changeCaseToolStripMenuItem.BackgroundImage = null;
            this.changeCaseToolStripMenuItem.Name = "changeCaseToolStripMenuItem";
            this.changeCaseToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.changeCaseToolStripMenuItem.Click += new System.EventHandler(this.changeCaseToolStripMenuItem_Click);
            // 
            // removeLineBreaksToolStripMenuItem
            // 
            this.removeLineBreaksToolStripMenuItem.AccessibleDescription = null;
            this.removeLineBreaksToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.removeLineBreaksToolStripMenuItem, "removeLineBreaksToolStripMenuItem");
            this.removeLineBreaksToolStripMenuItem.BackgroundImage = null;
            this.removeLineBreaksToolStripMenuItem.Name = "removeLineBreaksToolStripMenuItem";
            this.removeLineBreaksToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.removeLineBreaksToolStripMenuItem.Click += new System.EventHandler(this.removeLineBreaksToolStripMenuItem_Click);
            // 
            // settingsToolStripMenuItem
            // 
            this.settingsToolStripMenuItem.AccessibleDescription = null;
            this.settingsToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.settingsToolStripMenuItem, "settingsToolStripMenuItem");
            this.settingsToolStripMenuItem.BackgroundImage = null;
            this.settingsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.optionsToolStripMenuItem});
            this.settingsToolStripMenuItem.Name = "settingsToolStripMenuItem";
            this.settingsToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // optionsToolStripMenuItem
            // 
            this.optionsToolStripMenuItem.AccessibleDescription = null;
            this.optionsToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.optionsToolStripMenuItem, "optionsToolStripMenuItem");
            this.optionsToolStripMenuItem.BackgroundImage = null;
            this.optionsToolStripMenuItem.Name = "optionsToolStripMenuItem";
            this.optionsToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.optionsToolStripMenuItem.Click += new System.EventHandler(this.optionsToolStripMenuItem_Click);
            // 
            // toolsToolStripMenuItem
            // 
            this.toolsToolStripMenuItem.AccessibleDescription = null;
            this.toolsToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.toolsToolStripMenuItem, "toolsToolStripMenuItem");
            this.toolsToolStripMenuItem.BackgroundImage = null;
            this.toolsToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.mergeTiffToolStripMenuItem,
            this.mergePdfToolStripMenuItem,
            this.splitPdfToolStripMenuItem});
            this.toolsToolStripMenuItem.Name = "toolsToolStripMenuItem";
            this.toolsToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // mergeTiffToolStripMenuItem
            // 
            this.mergeTiffToolStripMenuItem.AccessibleDescription = null;
            this.mergeTiffToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.mergeTiffToolStripMenuItem, "mergeTiffToolStripMenuItem");
            this.mergeTiffToolStripMenuItem.BackgroundImage = null;
            this.mergeTiffToolStripMenuItem.Name = "mergeTiffToolStripMenuItem";
            this.mergeTiffToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.mergeTiffToolStripMenuItem.Click += new System.EventHandler(this.mergeTiffToolStripMenuItem_Click);
            // 
            // mergePdfToolStripMenuItem
            // 
            this.mergePdfToolStripMenuItem.AccessibleDescription = null;
            this.mergePdfToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.mergePdfToolStripMenuItem, "mergePdfToolStripMenuItem");
            this.mergePdfToolStripMenuItem.BackgroundImage = null;
            this.mergePdfToolStripMenuItem.Name = "mergePdfToolStripMenuItem";
            this.mergePdfToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.mergePdfToolStripMenuItem.Click += new System.EventHandler(this.mergePdfToolStripMenuItem_Click);
            // 
            // splitPdfToolStripMenuItem
            // 
            this.splitPdfToolStripMenuItem.AccessibleDescription = null;
            this.splitPdfToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.splitPdfToolStripMenuItem, "splitPdfToolStripMenuItem");
            this.splitPdfToolStripMenuItem.BackgroundImage = null;
            this.splitPdfToolStripMenuItem.Name = "splitPdfToolStripMenuItem";
            this.splitPdfToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.splitPdfToolStripMenuItem.Click += new System.EventHandler(this.splitPdfToolStripMenuItem_Click);
            // 
            // helpToolStripMenuItem
            // 
            this.helpToolStripMenuItem.AccessibleDescription = null;
            this.helpToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.helpToolStripMenuItem, "helpToolStripMenuItem");
            this.helpToolStripMenuItem.BackgroundImage = null;
            this.helpToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.helpToolStripMenuItem1,
            this.toolStripMenuItem3,
            this.aboutToolStripMenuItem});
            this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
            this.helpToolStripMenuItem.ShortcutKeyDisplayString = null;
            // 
            // helpToolStripMenuItem1
            // 
            this.helpToolStripMenuItem1.AccessibleDescription = null;
            this.helpToolStripMenuItem1.AccessibleName = null;
            resources.ApplyResources(this.helpToolStripMenuItem1, "helpToolStripMenuItem1");
            this.helpToolStripMenuItem1.BackgroundImage = null;
            this.helpToolStripMenuItem1.Name = "helpToolStripMenuItem1";
            this.helpToolStripMenuItem1.ShortcutKeyDisplayString = null;
            this.helpToolStripMenuItem1.Click += new System.EventHandler(this.helpToolStripMenuItem_Click);
            // 
            // toolStripMenuItem3
            // 
            this.toolStripMenuItem3.AccessibleDescription = null;
            this.toolStripMenuItem3.AccessibleName = null;
            resources.ApplyResources(this.toolStripMenuItem3, "toolStripMenuItem3");
            this.toolStripMenuItem3.Name = "toolStripMenuItem3";
            // 
            // aboutToolStripMenuItem
            // 
            this.aboutToolStripMenuItem.AccessibleDescription = null;
            this.aboutToolStripMenuItem.AccessibleName = null;
            resources.ApplyResources(this.aboutToolStripMenuItem, "aboutToolStripMenuItem");
            this.aboutToolStripMenuItem.BackgroundImage = null;
            this.aboutToolStripMenuItem.Name = "aboutToolStripMenuItem";
            this.aboutToolStripMenuItem.ShortcutKeyDisplayString = null;
            this.aboutToolStripMenuItem.Click += new System.EventHandler(this.aboutToolStripMenuItem2_Click);
            // 
            // statusStrip1
            // 
            this.statusStrip1.AccessibleDescription = null;
            this.statusStrip1.AccessibleName = null;
            resources.ApplyResources(this.statusStrip1, "statusStrip1");
            this.statusStrip1.BackgroundImage = null;
            this.statusStrip1.Font = null;
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripStatusLabel1,
            this.toolStripProgressBar1});
            this.statusStrip1.Name = "statusStrip1";
            // 
            // toolStripStatusLabel1
            // 
            this.toolStripStatusLabel1.AccessibleDescription = null;
            this.toolStripStatusLabel1.AccessibleName = null;
            resources.ApplyResources(this.toolStripStatusLabel1, "toolStripStatusLabel1");
            this.toolStripStatusLabel1.BackgroundImage = null;
            this.toolStripStatusLabel1.Name = "toolStripStatusLabel1";
            // 
            // toolStripProgressBar1
            // 
            this.toolStripProgressBar1.AccessibleDescription = null;
            this.toolStripProgressBar1.AccessibleName = null;
            resources.ApplyResources(this.toolStripProgressBar1, "toolStripProgressBar1");
            this.toolStripProgressBar1.Name = "toolStripProgressBar1";
            // 
            // toolStrip1
            // 
            this.toolStrip1.AccessibleDescription = null;
            this.toolStrip1.AccessibleName = null;
            resources.ApplyResources(this.toolStrip1, "toolStrip1");
            this.toolStrip1.BackgroundImage = null;
            this.toolStrip1.Font = null;
            this.toolStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.toolStripBtnOpen,
            this.toolStripBtnScan,
            this.toolStripBtnSave,
            this.toolStripBtnOCR,
            this.toolStripButtonCancelOCR,
            this.toolStripBtnClear,
            this.toolStripCbLang,
            this.toolStripLabel1});
            this.toolStrip1.Name = "toolStrip1";
            this.toolStrip1.ItemClicked += new System.Windows.Forms.ToolStripItemClickedEventHandler(this.toolStrip1_ItemClicked);
            // 
            // toolStripBtnOpen
            // 
            this.toolStripBtnOpen.AccessibleDescription = null;
            this.toolStripBtnOpen.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnOpen, "toolStripBtnOpen");
            this.toolStripBtnOpen.BackgroundImage = null;
            this.toolStripBtnOpen.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripBtnOpen.Name = "toolStripBtnOpen";
            this.toolStripBtnOpen.Tag = this.openToolStripMenuItem;
            // 
            // toolStripBtnScan
            // 
            this.toolStripBtnScan.AccessibleDescription = null;
            this.toolStripBtnScan.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnScan, "toolStripBtnScan");
            this.toolStripBtnScan.BackgroundImage = null;
            this.toolStripBtnScan.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripBtnScan.Name = "toolStripBtnScan";
            this.toolStripBtnScan.Tag = this.scanToolStripMenuItem;
            // 
            // toolStripBtnSave
            // 
            this.toolStripBtnSave.AccessibleDescription = null;
            this.toolStripBtnSave.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnSave, "toolStripBtnSave");
            this.toolStripBtnSave.BackgroundImage = null;
            this.toolStripBtnSave.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripBtnSave.Name = "toolStripBtnSave";
            this.toolStripBtnSave.Tag = this.saveToolStripMenuItem;
            // 
            // toolStripBtnOCR
            // 
            this.toolStripBtnOCR.AccessibleDescription = null;
            this.toolStripBtnOCR.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnOCR, "toolStripBtnOCR");
            this.toolStripBtnOCR.BackgroundImage = null;
            this.toolStripBtnOCR.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripBtnOCR.Name = "toolStripBtnOCR";
            this.toolStripBtnOCR.Tag = this.oCRToolStripMenuItem;
            // 
            // toolStripButtonCancelOCR
            // 
            this.toolStripButtonCancelOCR.AccessibleDescription = null;
            this.toolStripButtonCancelOCR.AccessibleName = null;
            resources.ApplyResources(this.toolStripButtonCancelOCR, "toolStripButtonCancelOCR");
            this.toolStripButtonCancelOCR.BackgroundImage = null;
            this.toolStripButtonCancelOCR.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripButtonCancelOCR.Name = "toolStripButtonCancelOCR";
            this.toolStripButtonCancelOCR.Click += new System.EventHandler(this.toolStripButtonCancelOCR_Click);
            // 
            // toolStripBtnClear
            // 
            this.toolStripBtnClear.AccessibleDescription = null;
            this.toolStripBtnClear.AccessibleName = null;
            resources.ApplyResources(this.toolStripBtnClear, "toolStripBtnClear");
            this.toolStripBtnClear.BackgroundImage = null;
            this.toolStripBtnClear.DisplayStyle = System.Windows.Forms.ToolStripItemDisplayStyle.Text;
            this.toolStripBtnClear.Name = "toolStripBtnClear";
            this.toolStripBtnClear.Click += new System.EventHandler(this.toolStripBtnClear_Click);
            // 
            // toolStripCbLang
            // 
            this.toolStripCbLang.AccessibleDescription = null;
            this.toolStripCbLang.AccessibleName = null;
            this.toolStripCbLang.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            resources.ApplyResources(this.toolStripCbLang, "toolStripCbLang");
            this.toolStripCbLang.DropDownStyle = System.Windows.Forms.ComboBoxStyle.DropDownList;
            this.toolStripCbLang.Name = "toolStripCbLang";
            this.toolStripCbLang.SelectedIndexChanged += new System.EventHandler(this.toolStripCbLang_SelectedIndexChanged);
            // 
            // toolStripLabel1
            // 
            this.toolStripLabel1.AccessibleDescription = null;
            this.toolStripLabel1.AccessibleName = null;
            this.toolStripLabel1.Alignment = System.Windows.Forms.ToolStripItemAlignment.Right;
            resources.ApplyResources(this.toolStripLabel1, "toolStripLabel1");
            this.toolStripLabel1.BackgroundImage = null;
            this.toolStripLabel1.Name = "toolStripLabel1";
            // 
            // backgroundWorkerScan
            // 
            this.backgroundWorkerScan.WorkerReportsProgress = true;
            this.backgroundWorkerScan.WorkerSupportsCancellation = true;
            this.backgroundWorkerScan.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorkerScan_DoWork);
            this.backgroundWorkerScan.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorkerScan_RunWorkerCompleted);
            // 
            // backgroundWorkerLoad
            // 
            this.backgroundWorkerLoad.WorkerReportsProgress = true;
            this.backgroundWorkerLoad.WorkerSupportsCancellation = true;
            this.backgroundWorkerLoad.DoWork += new System.ComponentModel.DoWorkEventHandler(this.backgroundWorkerLoad_DoWork);
            this.backgroundWorkerLoad.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.backgroundWorkerLoad_RunWorkerCompleted);
            // 
            // GUI
            // 
            this.AccessibleDescription = null;
            this.AccessibleName = null;
            resources.ApplyResources(this, "$this");
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.BackgroundImage = null;
            this.Controls.Add(this.splitContainer1);
            this.Controls.Add(this.toolStrip1);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.menuStrip1);
            this.Font = null;
            this.Icon = null;
            this.KeyPreview = true;
            this.MainMenuStrip = this.menuStrip1;
            this.Name = "GUI";
            this.splitContainer2.Panel1.ResumeLayout(false);
            this.splitContainer2.Panel2.ResumeLayout(false);
            this.splitContainer2.ResumeLayout(false);
            ((System.ComponentModel.ISupportInitialize)(this.pictureBox1)).EndInit();
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.Panel2.PerformLayout();
            this.splitContainer1.ResumeLayout(false);
            this.panel1.ResumeLayout(false);
            this.toolStrip2.ResumeLayout(false);
            this.toolStrip2.PerformLayout();
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.toolStrip1.ResumeLayout(false);
            this.toolStrip1.PerformLayout();
            this.ResumeLayout(false);
            this.PerformLayout();

        }

        #endregion

        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveAsToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem quitToolStripMenuItem;
        private System.Windows.Forms.StatusStrip statusStrip1;
        private System.Windows.Forms.ToolStrip toolStrip1;
        private System.Windows.Forms.ToolStripButton toolStripBtnOpen;
        protected System.Windows.Forms.ToolStripButton toolStripBtnOCR;
        private System.Windows.Forms.ToolStripButton toolStripBtnClear;
        private System.Windows.Forms.SplitContainer splitContainer1;
        protected System.Windows.Forms.TextBox textBox1;
        private System.Windows.Forms.ToolStripMenuItem commandToolStripMenuItem;
        protected System.Windows.Forms.ToolStripMenuItem oCRToolStripMenuItem;
        protected System.Windows.Forms.ToolStripMenuItem oCRAllPagesToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem2;
        protected System.Windows.Forms.ToolStripMenuItem postprocessToolStripMenuItem;
        protected System.Windows.Forms.ToolStripMenuItem settingsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem1;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem3;
        private System.Windows.Forms.ToolStripMenuItem aboutToolStripMenuItem;
        private System.Windows.Forms.ToolStripLabel toolStripLabel1;
        protected System.Windows.Forms.ToolStripComboBox toolStripCbLang;
        private System.Windows.Forms.ToolStrip toolStrip2;
        private System.Windows.Forms.ToolStripButton toolStripBtnPrev;
        private System.Windows.Forms.ToolStripButton toolStripBtnNext;
        private System.Windows.Forms.ToolStripButton toolStripBtnFitImage;
        private System.Windows.Forms.ToolStripButton toolStripBtnActualSize;
        private System.Windows.Forms.ToolStripButton toolStripBtnZoomIn;
        private System.Windows.Forms.ToolStripButton toolStripBtnZoomOut;
        protected System.Windows.Forms.ToolStripStatusLabel toolStripStatusLabel1;
        private System.Windows.Forms.Label lblCurIndex;
        private System.Windows.Forms.SplitContainer splitContainer2;
        private System.Windows.Forms.Panel panel1;
        protected System.Windows.Forms.ToolStripProgressBar toolStripProgressBar1;
        protected VietOCR.NET.Controls.ScrollablePictureBox pictureBox1;
        private System.Windows.Forms.ToolStripButton toolStripBtnRotateCCW;
        private System.Windows.Forms.ToolStripButton toolStripBtnRotateCW;
        private System.Windows.Forms.ToolStripMenuItem scanToolStripMenuItem;
        private System.Windows.Forms.ToolStripButton toolStripBtnScan;
        private System.ComponentModel.BackgroundWorker backgroundWorkerScan;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator1;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator2;
        private System.Windows.Forms.ToolStripSeparator toolStripSeparator3;
        private System.Windows.Forms.ToolStripMenuItem optionsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem formatToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem wordWrapToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem fontToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem5;
        private System.Windows.Forms.ToolStripMenuItem changeCaseToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem removeLineBreaksToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem toolsToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem mergeTiffToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem splitPdfToolStripMenuItem;
        private System.ComponentModel.BackgroundWorker backgroundWorkerLoad;
        protected System.Windows.Forms.ToolStripButton toolStripButtonCancelOCR;
        private System.Windows.Forms.ToolStripMenuItem mergePdfToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem saveToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem4;
        private System.Windows.Forms.ToolStripMenuItem recentFilesToolStripMenuItem;
        private System.Windows.Forms.ToolStripButton toolStripBtnSave;
        private System.Windows.Forms.ToolStripMenuItem imageToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem metadataToolStripMenuItem;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem6;
        protected System.Windows.Forms.ToolStripMenuItem screenshotModeToolStripMenuItem;
    }
}
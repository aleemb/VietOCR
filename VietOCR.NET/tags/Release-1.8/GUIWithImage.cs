using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;

namespace VietOCR.NET
{
    public partial class GUIWithImage : VietOCR.NET.GUIWithCommand
    {
        const string strScreenshotMode = "ScreenshotMode";

        public GUIWithImage()
        {
            InitializeComponent();
        }

        protected override void metadataToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (imageList == null)
            {
                MessageBox.Show(this, Properties.Resources.LoadImage, strProgName);
                return;
            }

            ImageInfoDialog dialog = new ImageInfoDialog();
            dialog.Image = imageList[imageIndex];

            if (dialog.ShowDialog() == DialogResult.OK)
            {

            }
        }

        protected override void screenshotModeToolStripMenuItem_Click(object sender, EventArgs e)
        {
            ToolStripMenuItem mi = (ToolStripMenuItem)sender;
            mi.Checked ^= true;
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);

            this.screenshotModeToolStripMenuItem.Checked = Convert.ToBoolean(
                (int)regkey.GetValue(strScreenshotMode, Convert.ToInt32(false)));
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);

            regkey.SetValue(strScreenshotMode, Convert.ToInt32(this.screenshotModeToolStripMenuItem.Checked));
        }
    }
}

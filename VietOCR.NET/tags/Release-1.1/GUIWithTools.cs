using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.IO;

namespace VietOCR.NET
{
    public partial class GUIWithTools : VietOCR.NET.GUIWithSettings
    {
        const string strImageFolder = "ImageFolder";

        string imageFolder;
        int filterIndex;

        public GUIWithTools()
        {
            InitializeComponent();
        }

        protected override void mergeTiffToolStripMenuItem_Click(object sender, EventArgs e)
        {
            OpenFileDialog openFileDialog1 = new OpenFileDialog();

            openFileDialog1.InitialDirectory = imageFolder;
            openFileDialog1.Title = Properties.Resources.Select + " Input Images";
            openFileDialog1.Filter = "Image Files (*.tif;*.tiff)|*.tif;*.tiff|Image Files (*.bmp)|*.bmp|Image Files (*.jpg;*.jpeg)|*.jpg;*.jpeg|Image Files (*.png)|*.png|All Image Files|*.tif;*.tiff;*.bmp;*.jpg;*.jpeg;*.png";
            openFileDialog1.FilterIndex = filterIndex;
            openFileDialog1.RestoreDirectory = true;
            openFileDialog1.Multiselect = true;

            if (openFileDialog1.ShowDialog() == DialogResult.OK)
            {
                filterIndex = openFileDialog1.FilterIndex;
                imageFolder = Path.GetDirectoryName(openFileDialog1.FileName); 
                SaveFileDialog saveFileDialog1 = new SaveFileDialog();
                saveFileDialog1.InitialDirectory = imageFolder;
                saveFileDialog1.Title = Properties.Resources.Save + " Multi-page TIFF Image";
                saveFileDialog1.Filter = "Image Files (*.tif;*.tiff)|*.tif;*.tiff";
                saveFileDialog1.RestoreDirectory = true;

                if (saveFileDialog1.ShowDialog() == DialogResult.OK)
                {
                    File.Delete(saveFileDialog1.FileName);

                    ImageIOHelper.MergeTiff(openFileDialog1.FileNames, saveFileDialog1.FileName);
                    MessageBox.Show(this, Properties.Resources.Mergecompleted + Path.GetFileName(saveFileDialog1.FileName) + Properties.Resources.created, strProgName, MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
            }
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);
            imageFolder = (string)regkey.GetValue(strImageFolder, Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments));
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);
            regkey.SetValue(strImageFolder, imageFolder);
        }
    }
}

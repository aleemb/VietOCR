using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace VietOCR.NET
{
    public partial class ImageInfoDialog : Form
    {
        Image image;
        bool isProgrammatic;

        public Image Image
        {
            get { return image; }
            set { image = value; }
        }

        public ImageInfoDialog()
        {
            InitializeComponent();
        }

        protected override void OnLoad(EventArgs ea)
        {
            base.OnLoad(ea);

            this.textBoxXRes.Text = Math.Round(this.image.HorizontalResolution).ToString();
            this.textBoxYRes.Text = Math.Round(this.image.VerticalResolution).ToString();
            this.textBoxWidth.Text = this.image.Width.ToString();
            this.textBoxHeight.Text = this.image.Height.ToString();
            this.comboBox3.SelectedIndex = 0;
            this.comboBox4.SelectedIndex = 0;
        }

        protected override void OnClosed(EventArgs ea)
        {
            base.OnClosed(ea);
        }

        private void comboBox3_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (!isProgrammatic)
            {
                isProgrammatic = true;
                this.comboBox4.SelectedItem = this.comboBox3.SelectedItem;
                ConvertUnits(this.comboBox3.SelectedItem.ToString());
                isProgrammatic = false;
            }
        }

        private void comboBox4_SelectedIndexChanged(object sender, EventArgs e)
        {
            if (!isProgrammatic)
            {
                isProgrammatic = true;
                this.comboBox3.SelectedItem = this.comboBox4.SelectedItem;
                ConvertUnits(this.comboBox4.SelectedItem.ToString());
                isProgrammatic = false;
            }
        }

        private void ConvertUnits(string unit)
        {
            switch (unit)
            {
                case "inches":
                    this.textBoxWidth.Text = Math.Round(this.image.Width / this.image.HorizontalResolution, 1).ToString();
                    this.textBoxHeight.Text = Math.Round(this.image.Height / this.image.VerticalResolution, 1).ToString();
                    break;

                case "cm":
                    this.textBoxWidth.Text = Math.Round(this.image.Width / this.image.HorizontalResolution * 2.54, 2).ToString();
                    this.textBoxHeight.Text = Math.Round(this.image.Height / this.image.VerticalResolution * 2.54, 2).ToString();
                    break;

                default: // "pixel"
                    this.textBoxWidth.Text = this.image.Width.ToString();
                    this.textBoxHeight.Text = this.image.Height.ToString();
                    break;
            }
        }
    }
}

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
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using Microsoft.Win32;
using System.Globalization;
using System.Threading;

using Net.SourceForge.Vietpad.InputMethod;

using Vietpad.NET.Controls;
using VietOCR.NET.Controls;

namespace VietOCR.NET
{
    public partial class GUIWithInputMethod : VietOCR.NET.GUIWithRegistry
    {
        string selectedInputMethod;
        ToolStripMenuItem miimChecked;
        ToolStripMenuItem miuilChecked;

        const string strInputMethod = "InputMethod";

        public GUIWithInputMethod()
        {
            // Sets the UI culture to the selected language.
            Thread.CurrentThread.CurrentUICulture = new CultureInfo(selectedUILanguage);

            InitializeComponent();

            //
            // Keyboard InputMethod submenu
            //
            EventHandler eh = new EventHandler(MenuKeyboardInputMethodOnClick);

            List<ToolStripRadioButtonMenuItem> ar = new List<ToolStripRadioButtonMenuItem>();

            foreach (string inputMethod in Enum.GetNames(typeof(InputMethods)))
            {
                ToolStripRadioButtonMenuItem miim = new ToolStripRadioButtonMenuItem();
                miim.Text = inputMethod;
                miim.CheckOnClick = true;
                miim.Click += eh;
                ar.Add(miim);
            }

            this.vietInputMethodToolStripMenuItem.DropDownItems.AddRange(ar.ToArray());
            this.textBox1.KeyPress += new KeyPressEventHandler(new VietKeyHandler(this.textBox1).OnKeyPress);


            //
            // Keyboard UI Language submenu
            //
            EventHandler eh1 = new EventHandler(MenuKeyboardUILangOnClick);

            ar.Clear();

            String[] uiLangs = { "en-US", "vi-VN" };
            foreach (string uiLang in uiLangs)
            {
                ToolStripRadioButtonMenuItem miuil = new ToolStripRadioButtonMenuItem();
                CultureInfo ci = new CultureInfo(uiLang);
                miuil.Tag = ci.Name;
                miuil.Text = ci.Parent.DisplayName;
                miuil.CheckOnClick = true;
                miuil.Click += eh1;
                ar.Add(miuil);
            }
            this.uILanguageToolStripMenuItem.DropDownItems.AddRange(ar.ToArray());

        }

        protected override void OnLoad(EventArgs ea)
        {
            base.OnLoad(ea);

            for (int i = 0; i < this.vietInputMethodToolStripMenuItem.DropDownItems.Count; i++)
            {
                if (this.vietInputMethodToolStripMenuItem.DropDownItems[i].Text == selectedInputMethod)
                {
                    // Select InputMethod last saved
                    miimChecked = (ToolStripMenuItem)vietInputMethodToolStripMenuItem.DropDownItems[i];
                    miimChecked.Checked = true;
                    break;
                }
            }

            VietKeyHandler.InputMethod = (InputMethods)Enum.Parse(typeof(InputMethods), selectedInputMethod);
            VietKeyHandler.SmartMark = true;
            VietKeyHandler.ConsumeRepeatKey = true;

            for (int i = 0; i < this.uILanguageToolStripMenuItem.DropDownItems.Count; i++)
            {
                if (this.uILanguageToolStripMenuItem.DropDownItems[i].Tag.ToString() == selectedUILanguage)
                {
                    // Select UI Language last saved
                    miuilChecked = (ToolStripMenuItem)uILanguageToolStripMenuItem.DropDownItems[i];
                    miuilChecked.Checked = true;
                    break;
                }
            }
        }

        void MenuKeyboardInputMethodOnClick(object obj, EventArgs ea)
        {
            miimChecked.Checked = false;
            miimChecked = (ToolStripMenuItem)obj;
            miimChecked.Checked = true;
            selectedInputMethod = miimChecked.Text;
            VietKeyHandler.InputMethod = (InputMethods)Enum.Parse(typeof(InputMethods), selectedInputMethod);
        }

        void MenuKeyboardUILangOnClick(object obj, EventArgs ea)
        {
            miuilChecked.Checked = false;
            miuilChecked = (ToolStripMenuItem)obj;
            miuilChecked.Checked = true;
            if (selectedUILanguage != miuilChecked.Tag.ToString())
            {
                selectedUILanguage = miuilChecked.Tag.ToString();
                System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(GUIWithInputMethod));
                UpdateUI(selectedUILanguage);
                //MessageBox.Show(this, resources.GetString("Restart_me"), "VietOCR.NET", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }

        /// <summary>
        /// Changes localized text and messages
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="firstTime"></param>
        protected override void UpdateUI(string locale)
        {
            base.UpdateUI(locale);
            FormLocalizer localizer = new FormLocalizer(this, typeof(GUIWithInputMethod));
            localizer.ApplyCulture(new CultureInfo(locale));

            //this.overviewToolStripMenuItem.Text = strProgName + " " + Properties.Resources.Help;
            //this.aboutToolStripMenuItem.Text = Properties.Resources.About + " " + strProgName;

            //if (!String.IsNullOrEmpty(sourceEncoding))
            //{
            //    this.comboBox1.Text = sourceEncoding;
            //}
            //this.comboBox1.SelectedItem = sourceEncoding;
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);
            selectedInputMethod = (string)regkey.GetValue(strInputMethod, Enum.GetName(typeof(InputMethods), InputMethods.Telex));
            selectedUILanguage = (string)regkey.GetValue(strUILang, "en-US");
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);
            regkey.SetValue(strInputMethod, selectedInputMethod);
            regkey.SetValue(strUILang, selectedUILanguage);
        }
        
    }
}


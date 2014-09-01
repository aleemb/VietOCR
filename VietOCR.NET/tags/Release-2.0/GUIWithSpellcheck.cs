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
using Net.SourceForge.Vietpad.Utilities;

namespace VietOCR.NET
{
    public partial class GUIWithSpellcheck : VietOCR.NET.GUIWithSettings
    {
        private int start, end;
        private SpellChecker sp;
        private string curWord;

        public GUIWithSpellcheck()
        {
            InitializeComponent();
        }

        protected override void contextMenuStrip1_Opening(object sender, CancelEventArgs e)
        {
            try
            {
                this.contextMenuStrip1.Items.Clear();
                if (this.toolStripButtonSpellCheck.Checked)
                {
                    int offset = this.textBox1.GetCharIndexFromPosition(pointClicked);
                    BreakIterator boundary = BreakIterator.GetWordInstance();
                    boundary.Text = this.textBox1.Text;
                    end = boundary.Following(offset);

                    if (end != BreakIterator.DONE)
                    {
                        start = boundary.Previous();
                    }

                    curWord = this.textBox1.Text.Substring(start, end - start);
                    makeSuggestions(curWord);
                }
            }
            finally
            {
                // load standard menu items
                this.contextMenuStrip1.RepopulateContextMenu();
            }
        }

        /// <summary>
        /// Populates suggestions at top of context menu.
        /// </summary>
        /// <param name="curWord"></param>
        void makeSuggestions(string curWord)
        {
            if (sp == null || curWord == null || curWord.Trim().Length == 0)
            {
                return;
            }

            List<String> suggests = sp.Suggest(curWord);
            if (suggests == null || suggests.Count == 0)
            {
                return;
            }

            foreach (string word in suggests)
            {
                ToolStripMenuItem item = new ToolStripMenuItem(word);
                item.Font = new Font(item.Font, FontStyle.Bold);
                item.Click += new EventHandler(item_Click);
                this.contextMenuStrip1.Items.Add(item);
            }
            this.contextMenuStrip1.Items.Add("-");

            ToolStripMenuItem item1 = new ToolStripMenuItem(Properties.Resources.Ignore_All);
            item1.Tag = "ignore.word";
            item1.Click += new EventHandler(item_Click);
            this.contextMenuStrip1.Items.Add(item1);

            item1 = new ToolStripMenuItem(Properties.Resources.Add_to_Dictionary);
            item1.Tag = "add.word";
            item1.Click += new EventHandler(item_Click);
            this.contextMenuStrip1.Items.Add(item1);
            this.contextMenuStrip1.Items.Add("-");
        }

        void item_Click(object sender, EventArgs e)
        {
            ToolStripItem item = (ToolStripItem)sender;
            object command = item.Tag;

            if (command == null)
            {
                this.textBox1.Select(start, end - start);
                this.textBox1.SelectedText = item.Text;
            }
            else if (command.ToString() == "ignore.word")
            {
                sp.IgnoreWord(curWord);
            }
            else if (command.ToString() == "add.word")
            {
                sp.AddWord(curWord);
            }

            sp.SpellCheck();
        }

        protected override void toolStripButtonSpellCheck_Click(object sender, EventArgs e)
        {
            string localeId = null;

            if (LookupISO_3_1_Codes.ContainsKey(curLangCode))
            {
                localeId = LookupISO_3_1_Codes[curLangCode];
            }
            else if (LookupISO_3_1_Codes.ContainsKey(curLangCode.Substring(0, 3)))
            {
                localeId = LookupISO_3_1_Codes[curLangCode.Substring(0, 3)];
            }

            if (localeId == null)
            {
                MessageBox.Show("Need to add an entry in Data/ISO639-1.xml file.");
                return;
            }

            sp = new SpellChecker(this.textBox1, localeId);

            if (this.toolStripButtonSpellCheck.Checked)
            {
                sp.EnableSpellCheck();
            }
            else
            {
                sp.DisableSpellCheck();
            }
            this.textBox1.Refresh();
        }
    }
}

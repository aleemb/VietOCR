﻿/**
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
using System.Threading;
using System.IO;
using Microsoft.Win32;
using VietOCR.NET.Postprocessing;
using System.Globalization;

namespace VietOCR.NET
{
    public partial class GUIWithWatch : VietOCR.NET.GUIWithInputMethod
    {
        const string strWatchEnable = "WatchEnable";
        const string strWatchFolder = "WatchFolder";
        const string strOutputFolder = "OutputFolder";

        private Queue<String> queue;
        private string watchFolder;
        private string outputFolder;
        private bool watchEnabled;

        private WatchForm watchForm;
        private Watcher watcher;

        private StatusForm statusForm;

        delegate void UpdateStatusEvent(string fileName); 

        public GUIWithWatch()
        {
            InitializeComponent();
            statusForm = new StatusForm();
            statusForm.Text = Properties.Resources.BatchProcessStatus;
        }

        protected override void OnLoad(EventArgs ea)
        {
            base.OnLoad(ea);

            queue = new Queue<String>();
            watcher = new Watcher(queue, watchFolder);
            watcher.Enabled = watchEnabled;

            System.Windows.Forms.Timer aTimer = new System.Windows.Forms.Timer();
            aTimer.Interval = 15000;
            aTimer.Tick += new EventHandler(OnTimedEvent);
            aTimer.Start();
        }

        private void OnTimedEvent(Object sender, EventArgs e)
        {
            if (queue.Count > 0)
            {
                if (this.statusForm.IsDisposed)
                {
                    this.statusForm = new StatusForm();
                    statusForm.Text = Properties.Resources.BatchProcessStatus;
                }
                if (!this.statusForm.Visible)
                {
                    this.statusForm.Show();
                }

                Thread t = new Thread(new ThreadStart(AutoOCR));
                t.Start();
            }
        }

        private void AutoOCR()
        {
            FileInfo imageFile = new FileInfo(queue.Dequeue());
            this.statusForm.TextBox.BeginInvoke(new UpdateStatusEvent(this.WorkerUpdate), new Object[] { imageFile.FullName });
            
            IList<Image> imageList = ImageIOHelper.GetImageList(imageFile);
            if (imageList == null)
            {
                this.statusForm.TextBox.BeginInvoke(new UpdateStatusEvent(this.WorkerUpdate), new Object[] { "    **  " + Properties.Resources.Cannotprocess + imageFile.Name + "  **" });
                return;
            }
            if (curLangCode == null)
            {
                this.statusForm.TextBox.BeginInvoke(new UpdateStatusEvent(this.WorkerUpdate), new Object[] { "    **  " + Properties.Resources.selectLanguage + "  **" });
                //queue.Clear();
                return;
            }
            
            try
            {
                OCR ocrEngine = new OCR();
                string result = ocrEngine.RecognizeText(imageList, -1, curLangCode);

                // postprocess to correct common OCR errors
                result = Processor.PostProcess(result, curLangCode);

                using (StreamWriter sw = new StreamWriter(Path.Combine(outputFolder, imageFile.Name + ".txt"), false, new System.Text.UTF8Encoding()))
                {
                    sw.Write(result);
                }
            }
            catch (Exception e)
            {
                // Sets the UI culture to the selected language.
                Thread.CurrentThread.CurrentUICulture = new CultureInfo(selectedUILanguage);

                this.statusForm.TextBox.BeginInvoke(new UpdateStatusEvent(this.WorkerUpdate), new Object[] { "    **  " + Properties.Resources.Cannotprocess + imageFile.Name + "  **"});
                Console.WriteLine(e.StackTrace);
            }
        }

        void WorkerUpdate(string fileName)
        {
            this.statusForm.TextBox.AppendText(fileName + Environment.NewLine);
        }

        protected override void watchToolStripMenuItem_Click(object sender, EventArgs e)
        {
            if (watchForm == null)
            {
                watchForm = new WatchForm();
            }

            watchForm.WatchFolder = watchFolder;
            watchForm.OutputFolder = outputFolder;
            watchForm.WatchEnabled = watchEnabled;

            if (watchForm.ShowDialog() == DialogResult.OK)
            {
                watchFolder = watchForm.WatchFolder;
                outputFolder = watchForm.OutputFolder;
                watchEnabled = watchForm.WatchEnabled;
                watcher.Path = watchFolder;
                watcher.Enabled = watchEnabled;
            }
        }

        /// <summary>
        /// Changes localized text and messages
        /// </summary>
        /// <param name="locale"></param>
        /// <param name="firstTime"></param>
        protected override void ChangeUILanguage(string locale)
        {
            base.ChangeUILanguage(locale);

            if (watchForm != null)
            {
                watchForm.ChangeUILanguage(locale);
            }
            statusForm.Text = Properties.Resources.BatchProcessStatus;
        }

        protected override void LoadRegistryInfo(RegistryKey regkey)
        {
            base.LoadRegistryInfo(regkey);
            watchEnabled = Convert.ToBoolean((int)regkey.GetValue(strWatchEnable, Convert.ToInt32(false)));
            watchFolder = (string)regkey.GetValue(strWatchFolder, Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments));
            outputFolder = (string)regkey.GetValue(strOutputFolder, Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments));
        }

        protected override void SaveRegistryInfo(RegistryKey regkey)
        {
            base.SaveRegistryInfo(regkey);
            regkey.SetValue(strWatchEnable, Convert.ToInt32(watchEnabled));
            regkey.SetValue(strWatchFolder, watchFolder);
            regkey.SetValue(strOutputFolder, outputFolder);
        }
    }
}

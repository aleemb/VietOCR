using System;
using System.Collections.Generic;
using System.Text;
using System.Xml;
using System.IO;
using System.Windows.Forms;
using System.Drawing;
using NHunspell;
using System.Text.RegularExpressions;
using System.Collections;

namespace VietOCR.NET
{
    class SpellChecker
    {
        TextBoxBase textbox;
        string localeId;
        String workingDir;
        static ArrayList listeners = new ArrayList();
        static List<CharacterRange> spellingErrorRanges = new List<CharacterRange>();
        static List<String> userWordList = new List<String>();
        static DateTime mapLastModified = DateTime.MinValue;
        Hunspell spellDict;
        CustomPaintTextBox cntl;

        public CharacterRange[] GetSpellingErrorRanges()
        {
            return spellingErrorRanges.ToArray();
        }


        public SpellChecker(TextBoxBase textbox, string langCode)
        {
            this.textbox = textbox;

            XmlDocument doc = new XmlDocument();

            workingDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
            String xmlFilePath = Path.Combine(workingDir, "Data/ISO639-1.xml");
            Dictionary<string, string> ht = new Dictionary<string, string>();
            doc.Load(xmlFilePath);

            XmlNodeList list = doc.GetElementsByTagName("entry");
            foreach (XmlNode node in list)
            {
                ht.Add(node.Attributes[0].Value, node.InnerText);
            }

            localeId = ht[langCode.Substring(0, 3)];
        }

        public void enableSpellCheck()
        {
            if (localeId == null)
            {
                return;
            }
            try
            {
                string dictPath = workingDir + "/dict/" + localeId;
                spellDict = new Hunspell(dictPath + ".aff", dictPath + ".dic");
                LoadUserDictionary();

                listeners.Add(new System.EventHandler(this.textbox_TextChanged));

                this.textbox.TextChanged += (System.EventHandler)listeners[0];
                cntl = new CustomPaintTextBox(this.textbox, this);

                spellCheck();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.Message);
            }
        }

        void spellCheck()
        {
            spellingErrorRanges.Clear();
            List<String> words = parseText(textbox.Text);
            List<String> misspelledWords = spellCheck(words);
            if (misspelledWords.Count == 0)
            {
                return;
            }


            StringBuilder sb = new StringBuilder();
            foreach (String word in misspelledWords)
            {
                sb.Append(word).Append("|");
            }
            sb.Length -= 1; //remove last |

            // build regex
            String patternStr = "\\b(" + sb.ToString() + ")\\b";
            Regex regex = new Regex(patternStr, RegexOptions.IgnoreCase);
            MatchCollection mc = regex.Matches(textbox.Text);

            // Loop through  the match collection to retrieve all 
            // matches and positions.
            for (int i = 0; i < mc.Count; i++)
            {
                spellingErrorRanges.Add(new CharacterRange(mc[i].Index, mc[i].Length));
            }
            //new CustomPaintTextBox(textbox, this);
        }

        List<String> spellCheck(List<String> words)
        {
            List<String> misspelled = new List<String>();

            foreach (String word in words)
            {
                if (!spellDict.Spell(word))
                {
                    // is mispelled word in user.dic?
                    if (!userWordList.Contains(word.ToLower()))
                    {
                        misspelled.Add(word);
                    }

                }
            }

            return misspelled;
        }

        List<String> parseText(String text)
        {
            List<String> words = new List<String>();
            BreakIterator boundary = BreakIterator.GetWordInstance();
            boundary.Text = text;
            int start = boundary.First();
            for (int end = boundary.Next(); end != BreakIterator.DONE; start = end, end = boundary.Next())
            {
                if (!Char.IsLetter(text[start]))
                {
                    continue;
                }
                words.Add(text.Substring(start, end - start));
            }

            return words;
        }

        public void disableSpellCheck()
        {
            if (localeId == null)
            {
                return;
            }

            if (listeners.Count > 0)
            {
                this.textbox.TextChanged -= (System.EventHandler)listeners[0];
                listeners.RemoveAt(0);
            }
            spellingErrorRanges.Clear();
        }

        private void textbox_TextChanged(object sender, EventArgs e)
        {
            spellCheck();
            this.textbox.Invalidate();
        }

        void LoadUserDictionary()
        {
            try
            {
                String baseDir = Path.GetDirectoryName(System.Reflection.Assembly.GetExecutingAssembly().Location);
                string strUserDictFile = Path.Combine(baseDir, @"dict\user.dic");
                FileInfo userDict = new FileInfo(strUserDictFile);
                DateTime fileLastModified = userDict.LastWriteTime;

                if (fileLastModified <= mapLastModified)
                {
                    return; // no need to reload dictionary
                }

                mapLastModified = fileLastModified;
                userWordList.Clear();

                using (StreamReader sr = new StreamReader(strUserDictFile, Encoding.UTF8))
                {
                    string str;

                    while ((str = sr.ReadLine()) != null)
                    {
                        userWordList.Add(str.ToLower());
                    }
                    //sr.Close();
                }
            }
            catch (IOException e)
            {
                MessageBox.Show(e.Message);
            }
        }
    }
}

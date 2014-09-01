/**
 * Copyright @ 2010 Quan Nguyen
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
package net.sourceforge.vietocr;

import com.stibocatalog.hunspell.Hunspell;
import java.awt.Color;
//import java.util.logging.*;
import java.io.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.*;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;

public class SpellChecker {

    JTextComponent textComp;
    // define the highlighter
    Highlighter.HighlightPainter myPainter = new WavyLineHighlighter(Color.red);
    String localeId;
    File baseDir;
    static Properties prop;
    static List<DocumentListener> lstList = new ArrayList<DocumentListener>();
    Hunspell.Dictionary spellDict;
    static List<String> userWordList = new ArrayList<String>();
    static long mapLastModified = Long.MIN_VALUE;

    public SpellChecker(JTextComponent textComp, String langCode) {
        this.textComp = textComp;
        baseDir = Utilities.getBaseDir(SpellChecker.this);

        File xmlFile = new File(baseDir, "data/ISO639-1.xml");

        try {
            if (prop == null) {
                prop = new Properties();
                prop.loadFromXML(new FileInputStream(xmlFile));
            }
            localeId = prop.getProperty(langCode);
            if (localeId == null) {
                localeId = prop.getProperty(langCode.substring(0, 3));
                if (localeId == null) {
                    JOptionPane.showMessageDialog(null, "Need to add an entry in data/ISO639-1.xml file.", Gui.APP_NAME, JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Missing ISO639-1.xml file. Cannot find it in " + new File(baseDir, "data").getPath() + " directory.", Gui.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
    }

    public void enableSpellCheck() {
        if (localeId == null) {
            return;
        }
        try {
            spellDict = Hunspell.getInstance().getDictionary(new File(baseDir, "dict/" + localeId).getPath());
            loadUserDictionary();

            SpellcheckDocumentListener docListener = new SpellcheckDocumentListener();
            lstList.add(docListener);
            this.textComp.getDocument().addDocumentListener(docListener);
            spellCheck();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), Gui.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
    }

    void spellCheck() {
        List<String> words = parseText(textComp.getText());
        List<String> misspelledWords = spellCheck(words);
        if (misspelledWords.isEmpty()) {
            return; // perfect writer!
        }

        StringBuilder sb = new StringBuilder();
        for (String word : misspelledWords) {
            sb.append(word).append("|");
        }
        sb.setLength(sb.length() - 1); //remove last |

        // build regex
        String patternStr = "\\b(" + sb.toString() + ")\\b";

        Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(textComp.getText());

        Highlighter hi = textComp.getHighlighter();
        hi.removeAllHighlights();

        while (matcher.find()) {
            try {
                hi.addHighlight(matcher.start(), matcher.end(), myPainter);
            } catch (BadLocationException ex) {
//                Logger.getLogger(Gui.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    List<String> spellCheck(List<String> words) {
        List<String> misspelled = new ArrayList<String>();
        for (String word : words) {
            if (spellDict.misspelled(word)) {
                // is mispelled word in user.dic?
                if (!userWordList.contains(word.toLowerCase())) {
                    misspelled.add(word);
                }
            }
        }

        return misspelled;
    }

    List<String> parseText(String text) {
        List<String> words = new ArrayList<String>();
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(text);
        int start = boundary.first();
        for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {
            if (!Character.isLetter(text.charAt(start))) {
                continue;
            }
            words.add(text.substring(start, end));
        }

        return words;
    }

    public void disableSpellCheck() {
        if (localeId == null) {
            return;
        }
        this.textComp.getDocument().removeDocumentListener(lstList.remove(0));
        this.textComp.getHighlighter().removeAllHighlights();
    }

    /**
     * Loads user dictionary only once.
     */
    void loadUserDictionary() {
        try {
            File userDict = new File(baseDir, "dict/user.dic");
            long fileLastModified = userDict.lastModified();

            if (fileLastModified <= mapLastModified) {
                return;// no need to reload dictionary
            }

            mapLastModified = fileLastModified;
            userWordList.clear();
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(userDict), "UTF8"));
            String str;
            while ((str = in.readLine()) != null) {
                userWordList.add(str.toLowerCase());
            }
            in.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot find \"user.dic\" in " + new File(baseDir, "dict").getPath() + " directory.", Gui.APP_NAME, JOptionPane.ERROR_MESSAGE);
        }
    }

    class SpellcheckDocumentListener implements DocumentListener {

        @Override
        public void removeUpdate(DocumentEvent e) {
            spellCheck();
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            spellCheck();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
        }
    }
}

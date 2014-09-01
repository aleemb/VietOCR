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
package net.sourceforge.vietocr;

import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Properties;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;

public class GuiWithSpellcheck extends GuiWithSettings {

    private int start, end;
    private SpellChecker sp;

    @Override
    void populatePopupMenuWithSuggestions(Point pointClicked) {
        try {
            popup.removeAll();
            if (this.jToggleButtonSpellCheck.isSelected()) {
                int offset = jTextArea1.viewToModel(pointClicked);
                start = javax.swing.text.Utilities.getWordStart(jTextArea1, offset);
                end = javax.swing.text.Utilities.getWordEnd(jTextArea1, offset);
                String curWord = jTextArea1.getDocument().getText(start, end - start);
                makeSuggestions(curWord);
            }
        } catch (BadLocationException e) {
        } finally {
            // load standard menu items
            repopulatePopupMenu();
        }
    }

    /**
     * Populates suggestions at top of context menu.
     * @param curWord
     */
    void makeSuggestions(final String curWord) {
        if (sp == null || curWord == null || curWord.trim().length() == 0) {
            return;
        }

        List<String> suggests = sp.suggest(curWord);
        if (suggests == null || suggests.isEmpty()) {
            return;
        }

        ActionListener correctLst = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String selectedWord = ae.getActionCommand();
                if (selectedWord.equals("ignore.word")) {
                    sp.ignoreWord(curWord);
                } else if (selectedWord.equals("add.word")) {
                    sp.addWord(curWord);
                } else {
                    jTextArea1.select(start, end);
                    jTextArea1.replaceSelection(selectedWord);
                }
                sp.spellCheck();
            }
        };

        for (String word : suggests) {
            JMenuItem item = new JMenuItem(word);
            item.setFont(item.getFont().deriveFont(Font.BOLD));
            item.addActionListener(correctLst);
            popup.add(item);
        }

        popup.addSeparator();
        JMenuItem item = new JMenuItem(bundle.getString("Ignore_All"));
        item.setActionCommand("ignore.word");
        item.addActionListener(correctLst);
        popup.add(item);
        item = new JMenuItem(bundle.getString("Add_to_Dictionary"));
        item.setActionCommand("add.word");
        item.addActionListener(correctLst);
        popup.add(item);
        popup.addSeparator();
    }

    @Override
    void spellCheckActionPerformed() {
        Properties lookupISO_3_1_Codes = getLookupISO_3_1_Codes();
        String localeId = null;

        if (lookupISO_3_1_Codes.containsKey(curLangCode)) {
            localeId = lookupISO_3_1_Codes.getProperty(curLangCode);
        } else if (lookupISO_3_1_Codes.containsKey(curLangCode.substring(0, 3))) {
            localeId = lookupISO_3_1_Codes.getProperty(curLangCode.substring(0, 3));
        }
        if (localeId == null) {
            JOptionPane.showMessageDialog(null, "Need to add an entry in data/ISO639-1.xml file.", Gui.APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        sp = new SpellChecker(this.jTextArea1, localeId);
        if (this.jToggleButtonSpellCheck.isSelected()) {
            sp.enableSpellCheck();
        } else {
            sp.disableSpellCheck();
        }
        this.jTextArea1.repaint();
    }
}

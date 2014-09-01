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

import java.util.Locale;
import java.util.regex.*;
import javax.swing.SwingUtilities;
import net.sourceforge.vietpad.*;

public class GuiWithFormat extends Gui {
    private ChangeCaseDialog changeCaseDlg;

    @Override
    void changeUILanguage(final Locale locale) {
        super.changeUILanguage(locale);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (changeCaseDlg != null) {
                    changeCaseDlg.changeUILanguage(locale);
                }
            }
        });
    }

    @Override
    void setLineWrap() {
        this.jTextArea1.setLineWrap(wordWrapOn = jCheckBoxMenuWordWrap.isSelected());
    }

    @Override
    void openFontDialog() {
        FontDialog dlg = new FontDialog(this);
        dlg.setAttributes(font);
        dlg.setVisible(true);
        if (dlg.succeeded()) {
            jTextArea1.setFont(font = dlg.getFont());
            jTextArea1.validate();
        }
    }

    @Override
    void openChangeCaseDialog() {
        if (changeCaseDlg == null) {
            changeCaseDlg = new ChangeCaseDialog(GuiWithFormat.this, false);
            // non-modal
            changeCaseDlg.setSelectedCase(prefs.get("selectedCase", "UPPER CASE"));
            changeCaseDlg.setLocation(
                    prefs.getInt("changeCaseX", changeCaseDlg.getX()),
                    prefs.getInt("changeCaseY", changeCaseDlg.getY()));
        }
        if (jTextArea1.getSelectedText() == null) {
            jTextArea1.selectAll();
        }
        changeCaseDlg.setVisible(true);
    }

    @Override
    void quit() {
        super.quit();

        if (changeCaseDlg != null) {
            prefs.put("selectedCase", changeCaseDlg.getSelectedCase());
        }
    }

    /**
     *  Changes case
     *
     *@param  typeOfCase  The type that the case should be changed to
     */
    public void changeCase(String typeOfCase) {
        if (jTextArea1.getSelectedText() == null) {
            jTextArea1.selectAll();

            if (jTextArea1.getSelectedText() == null) {
                return;
            }
        }

        String result = jTextArea1.getSelectedText();

        if (typeOfCase.equals("UPPER CASE")) {
            result = result.toUpperCase();
        } else if (typeOfCase.equals("lower case")) {
            result = result.toLowerCase();
        } else if (typeOfCase.equals("Title Case")) {
            StringBuffer strB = new StringBuffer(result.toLowerCase());
            Pattern pattern = Pattern.compile("(?<!\\p{InCombiningDiacriticalMarks}|\\p{L})\\p{L}");
            // word boundary
            Matcher matcher = pattern.matcher(result);
            while (matcher.find()) {
                int index = matcher.start();
                strB.setCharAt(index, Character.toTitleCase(strB.charAt(index)));
            }
            result = strB.toString();
        } else if (typeOfCase.equals("Sentence case")) {
            StringBuffer strB = new StringBuffer(result.toUpperCase().equals(result) ? result.toLowerCase() : result);
            Matcher matcher = Pattern.compile("\\p{L}(\\p{L}+)").matcher(result);
            while (matcher.find()) {
                if (!(matcher.group(0).toUpperCase().equals(matcher.group(0)) ||
                        matcher.group(1).toLowerCase().equals(matcher.group(1)))) {
                    for (int i = matcher.start(); i < matcher.end(); i++) {
                        strB.setCharAt(i, Character.toLowerCase(strB.charAt(i)));
                    }
                }
            }
            final String QUOTE = "\"'`,<>\u00AB\u00BB\u2018-\u203A";
            matcher = Pattern.compile("(?:[.?!\u203C-\u2049][])}" + QUOTE + "]*|^|\n|:\\s+[" + QUOTE + "])[-=_*\u2010-\u2015\\s]*[" + QUOTE + "\\[({]*\\p{L}").matcher(result);
            // begin of a sentence
            while (matcher.find()) {
                int i = matcher.end() - 1;
                strB.setCharAt(i, Character.toUpperCase(strB.charAt(i)));
            }
            result = strB.toString();
        }

        undoSupport.beginUpdate();
        int start = jTextArea1.getSelectionStart();
        jTextArea1.replaceSelection(result);
        jTextArea1.setSelectionStart(start);
        jTextArea1.setSelectionEnd(start + result.length());
        undoSupport.endUpdate();
    }

    @Override
    void removeLineBreaks() {
        if (jTextArea1.getSelectedText() == null) {
            jTextArea1.selectAll();

            if (jTextArea1.getSelectedText() == null) {
                return;
            }
        }
        String result = jTextArea1.getSelectedText().replace("\r\n", "\n").replaceAll("(?<=\n|^)[\t ]+|[\t ]+(?=$|\n)", "").replaceAll("(?<=.)\n(?=.)", " ");

        undoSupport.beginUpdate();
        int start = jTextArea1.getSelectionStart();
        jTextArea1.replaceSelection(result);
        jTextArea1.setSelectionStart(start);
        jTextArea1.setSelectionEnd(start + result.length());
        undoSupport.endUpdate();
    }
}

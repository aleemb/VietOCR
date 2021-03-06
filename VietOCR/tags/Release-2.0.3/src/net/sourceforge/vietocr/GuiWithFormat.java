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


import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import net.sourceforge.vietpad.utilities.TextUtilities;
import net.sourceforge.vietpad.components.FontDialog;

public class GuiWithFormat extends GuiWithImage {
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
    void jCheckBoxMenuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {
        this.jTextArea1.setLineWrap(wordWrapOn = jCheckBoxMenuWordWrap.isSelected());
    }

    @Override
    void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {
        FontDialog dlg = new FontDialog(this);
        dlg.setAttributes(font);

        Properties prop = new Properties();

        try {
            File xmlFile = new File(baseDir, "data/pangram.xml");
            prop.loadFromXML(new FileInputStream(xmlFile));
            dlg.setPreviewText(prop.getProperty(curLangCode));
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, ioe.getMessage(), APP_NAME, JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        }
        
        dlg.setVisible(true);
        if (dlg.succeeded()) {
            jTextArea1.setFont(font = dlg.getFont());
            jTextArea1.validate();
        }
    }

    @Override
    void jMenuItemChangeCaseActionPerformed(java.awt.event.ActionEvent evt) {
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
        if (changeCaseDlg != null) {
            prefs.put("selectedCase", changeCaseDlg.getSelectedCase());
        }
        super.quit();
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

        String result = TextUtilities.changeCase(jTextArea1.getSelectedText(), typeOfCase);

        undoSupport.beginUpdate();
        int start = jTextArea1.getSelectionStart();
        jTextArea1.replaceSelection(result);
        jTextArea1.setSelectionStart(start);
        jTextArea1.setSelectionEnd(start + result.length());
        undoSupport.endUpdate();
    }

    @Override
    void jMenuItemRemoveLineBreaksActionPerformed(java.awt.event.ActionEvent evt) {
        if (jTextArea1.getSelectedText() == null) {
            jTextArea1.selectAll();

            if (jTextArea1.getSelectedText() == null) {
                return;
            }
        }
        String result = TextUtilities.removeLineBreaks(jTextArea1.getSelectedText());

        undoSupport.beginUpdate();
        int start = jTextArea1.getSelectionStart();
        jTextArea1.replaceSelection(result);
        jTextArea1.setSelectionStart(start);
        jTextArea1.setSelectionEnd(start + result.length());
        undoSupport.endUpdate();
    }
}

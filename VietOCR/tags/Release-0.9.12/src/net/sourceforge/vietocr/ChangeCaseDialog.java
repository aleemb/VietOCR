/**
 * Copyright @ 2009 Quan Nguyen
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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.*;

public class ChangeCaseDialog extends javax.swing.JDialog {
    private String selectedCase;

    /** Creates new form ChangeCaseDialog */
    public ChangeCaseDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        setLocationRelativeTo(getOwner());

        //  Handle escape key to hide the dialog
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction =
            new AbstractAction() {
            @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                }
            };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jRadioButtonSen = new javax.swing.JRadioButton();
        jRadioButtonLower = new javax.swing.JRadioButton();
        jRadioButtonUpper = new javax.swing.JRadioButton();
        jRadioButtonTitle = new javax.swing.JRadioButton();
        jPanel2 = new javax.swing.JPanel();
        jButtonChangeCase = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/sourceforge/vietocr/ChangeCaseDialog"); // NOI18N
        setTitle(bundle.getString("this.Title")); // NOI18N
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 16, 16, 0));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.Y_AXIS));

        buttonGroup1.add(jRadioButtonSen);
        jRadioButtonSen.setText(bundle.getString("jRadioButtonSen.Text")); // NOI18N
        jRadioButtonSen.setActionCommand("Sentence case");
        jPanel1.add(jRadioButtonSen);

        buttonGroup1.add(jRadioButtonLower);
        jRadioButtonLower.setText(bundle.getString("jRadioButtonLower.Text")); // NOI18N
        jRadioButtonLower.setActionCommand("lower case");
        jPanel1.add(jRadioButtonLower);

        buttonGroup1.add(jRadioButtonUpper);
        jRadioButtonUpper.setText(bundle.getString("jRadioButtonUpper.Text")); // NOI18N
        jRadioButtonUpper.setActionCommand("UPPER CASE");
        jPanel1.add(jRadioButtonUpper);

        buttonGroup1.add(jRadioButtonTitle);
        jRadioButtonTitle.setText(bundle.getString("jRadioButtonTitle.Text")); // NOI18N
        jRadioButtonTitle.setActionCommand("Title Case");
        jPanel1.add(jRadioButtonTitle);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(16, 20, 16, 16));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jButtonChangeCase.setText(bundle.getString("jButtonChangeCase.Text")); // NOI18N
        jButtonChangeCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChangeCaseActionPerformed(evt);
            }
        });
        java.awt.Dimension size = jButtonChangeCase.getMaximumSize();
        size.width = Short.MAX_VALUE;
        jButtonChangeCase.setMaximumSize(size);
        jPanel2.add(jButtonChangeCase);
        jPanel2.add(javax.swing.Box.createVerticalStrut(3));
        getRootPane().setDefaultButton(jButtonChangeCase);

        jButtonClose.setText(bundle.getString("jButtonClose.Text")); // NOI18N
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        size = jButtonClose.getMaximumSize();
        size.width = Short.MAX_VALUE;
        jButtonClose.setMaximumSize(size);
        jPanel2.add(jButtonClose);

        getContentPane().add(jPanel2, java.awt.BorderLayout.EAST);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonChangeCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChangeCaseActionPerformed
        for (Enumeration e = buttonGroup1.getElements(); e.hasMoreElements();) {
            JRadioButton bt = (JRadioButton) e.nextElement();
            if (bt.isSelected()) {
                selectedCase = bt.getActionCommand();
                break;
            }
        }

        final JFrame frame = (JFrame) this.getOwner();

        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        frame.getGlassPane().setVisible(true);

        try {
            ((GuiWithFormat)frame).changeCase(selectedCase);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
//                    JOptionPane.showMessageDialog(frame, GuiWithFormat.APP_NAME
//                             + myResources.getString("_has_run_out_of_memory.\nPlease_restart_") + GuiWithFormat.APP_NAME
//                             + myResources.getString("_and_try_again."), myResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
        } finally {
            SwingUtilities.invokeLater(
                    new Runnable() {

                        @Override
                        public void run() {
                            frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            frame.getGlassPane().setVisible(false);
                            getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                            getGlassPane().setVisible(false);
                            getRootPane().setDefaultButton(jButtonClose);
                        }
                    });
        }
}//GEN-LAST:event_jButtonChangeCaseActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButtonCloseActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                ChangeCaseDialog dialog = new ChangeCaseDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    /**
     *  Sets the selected case
     *
     *@param  String selectedCase
     */
    public void setSelectedCase(String selectedCase) {
        this.selectedCase = selectedCase;

        for (Enumeration e = buttonGroup1.getElements(); e.hasMoreElements();) {
            JRadioButton bt = (JRadioButton) e.nextElement();
            if (bt.getActionCommand().equals(selectedCase)) {
                bt.setSelected(true);
                break;
            }
        }
    }

    /**
     *  Gets the selected case
     *
     *@return    String selectedCase
     */
    String getSelectedCase() {
        return selectedCase;
    }
    
    /**
     *  Shows and hides the dialog
     */
    @Override
    public void setVisible(final boolean flag) {
        if (flag) {
            super.setVisible(true);
            // switch default button twice to make it pulse in Mac OS X
            getRootPane().setDefaultButton(jButtonClose);
            getRootPane().setDefaultButton(jButtonChangeCase);
            // send to back and front to get focus in Metal and CDE/Motif
            toBack();
            toFront();
            jPanel1.requestFocus();
        } else {
            // switch default button to make it appear immediately upon re-opening
            getRootPane().setDefaultButton(jButtonChangeCase);
            jPanel2.paintImmediately(jPanel2.getBounds());
            super.setVisible(false);
        }
    }

    public void changeUILanguage(final Locale locale) {
        Locale.setDefault(locale);
        final ResourceBundle bundle = ResourceBundle.getBundle("net.sourceforge.vietocr.ChangeCaseDialog");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FormLocalizer localizer = new FormLocalizer(ChangeCaseDialog.this, ChangeCaseDialog.class);
                localizer.ApplyCulture(bundle);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButtonChangeCase;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton jRadioButtonLower;
    private javax.swing.JRadioButton jRadioButtonSen;
    private javax.swing.JRadioButton jRadioButtonTitle;
    private javax.swing.JRadioButton jRadioButtonUpper;
    // End of variables declaration//GEN-END:variables
}
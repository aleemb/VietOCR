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

import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.IIOImage;
import javax.swing.*;
import javax.swing.Timer;
import net.sourceforge.vietocr.postprocessing.Processor;

/**
 *
 * @author Quan Nguyen
 */
public class GuiWithWatch extends GuiWithFormat {
    private String watchFolder;
    private String outputFolder;
    private boolean watchEnabled;

    private StatusFrame statusFrame;
    private WatchDialog watchDialog;
    private Watcher watcher;

    public GuiWithWatch() {
        watchFolder = prefs.get("WatchFolder", System.getProperty("user.home"));
        outputFolder = prefs.get("OutputFolder", System.getProperty("user.home"));
        watchEnabled = prefs.getBoolean("WatchEnabled", false);

        statusFrame = new StatusFrame();
        statusFrame.setTitle(bundle.getString("statusFrame.Title"));

        // watch for new image files
        final Queue<File> queue = new LinkedList<File>();
        watcher = new Watcher(queue, new File(watchFolder));
        watcher.setEnabled(watchEnabled);

        Thread t = new Thread(watcher);
        t.start();

        // autoOCR if there are files in the queue
        Action autoOcrAction = new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final File imageFile = queue.poll();
                if (imageFile != null) {
                    final ArrayList<IIOImage> iioImageList = ImageIOHelper.getIIOImageList(imageFile);
                    if (!statusFrame.isVisible()) {
                        statusFrame.setVisible(true);
                    }
                    statusFrame.getTextArea().append(imageFile.getPath() + "\n");

                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                OCR ocrEngine = new OCR(tessPath);
                                String result = ocrEngine.recognizeText(iioImageList, -1, curLangCode);

                                // postprocess to correct common OCR errors
                                result = Processor.postProcess(result, curLangCode);

                                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(outputFolder, imageFile.getName() + ".txt")), UTF8));
                                out.write(result);
                                out.close();
                            } catch (Exception e) {
                                statusFrame.getTextArea().append("    **  " + bundle.getString("Cannotprocess") + " " + imageFile.getName() + "  **\n");
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        };

        new Timer(5000, autoOcrAction).start();
    }

    @Override
    protected void openWatchDialog() {
        if (watchDialog == null) {
            watchDialog = new WatchDialog(this);
        }

        watchDialog.setWatchFolder(watchFolder);
        watchDialog.setOutputFolder(outputFolder);
        watchDialog.setWatchEnabled(watchEnabled);

        if (watchDialog.showDialog() == JOptionPane.OK_OPTION) {
            watchFolder = watchDialog.getWatchFolder();
            outputFolder = watchDialog.getOutputFolder();
            watchEnabled = watchDialog.isWatchEnabled();
            watcher.setPath(new File(watchFolder));
            watcher.setEnabled(watchEnabled);
        }
    }
    
    @Override
    void quit() {
        super.quit();

        prefs.put("WatchFolder", watchFolder);
        prefs.put("OutputFolder", outputFolder);
        prefs.putBoolean("WatchEnabled", watchEnabled);

        System.exit(0);
    }

    @Override
    void changeUILanguage(final Locale locale) {
        super.changeUILanguage(locale);
        
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                if (watchDialog != null) {
                    watchDialog.changeUILanguage(locale);
                }

                statusFrame.setTitle(bundle.getString("statusFrame.Title"));
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        selectedUILang = prefs.get("UILanguage", "en");
        Locale.setDefault(selectedUILang.equals("vi") ? VIETNAM : Locale.US);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new GuiWithWatch().setVisible(true);
            }
        });
    }
}

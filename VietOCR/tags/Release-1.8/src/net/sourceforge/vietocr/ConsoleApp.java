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
import java.util.List;
import javax.imageio.IIOImage;
import net.sourceforge.vietocr.postprocessing.Processor;

public class ConsoleApp {

    public static void main(String[] args) {
        new ConsoleApp().performOCR(args);
    }

    private void performOCR(String[] args) {
        List<File> tempTiffFiles = null;

        try {
            if (args[0].equals("-?") || args[0].equals("-help") || args.length == 1 || args.length == 3) {
                System.err.println("Usage: java -jar VietOCR.jar\n"
                        + "       (to launch the program in GUI mode)\n\n"
                        + "   or  java -jar VietOCR.jar imagefile outputfile [-l langcode]\n"
                        + "       (to execute the program in command-line mode)");
                return;
            }

            final File imageFile = new File(args[0]);
            final File outputFile = new File(args[1]);

            if (!imageFile.exists()) {
                System.err.println("Input file does not exist.");
                return;
            }

            String curLangCode;

            if (args.length == 2) {
                curLangCode = "eng"; //default language
            } else {
                curLangCode = args[3];
            }

            String tessPath;

            File baseDir = Utilities.getBaseDir(this);

            if (Gui.WINDOWS) {
                tessPath = new File(baseDir, "tesseract").getPath();
            } else {
                tessPath = Gui.prefs.get("TesseractDirectory", new File(baseDir, "tesseract").getPath());
            }

            OCR ocrEngine = new OCR(tessPath);
            List<IIOImage> iioImageList = ImageIOHelper.getIIOImageList(imageFile);
            tempTiffFiles = ImageIOHelper.createTiffFiles(iioImageList, -1);
            String result = ocrEngine.recognizeText(tempTiffFiles, curLangCode);

            // postprocess to correct common OCR errors
            result = Processor.postProcess(result, curLangCode);

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile.getPath() + ".txt"), Gui.UTF8));
            out.write(result);
            out.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            //clean up working files
            if (tempTiffFiles != null) {
                for (File f : tempTiffFiles) {
                    f.delete();
                }
            }
        }
    }
}

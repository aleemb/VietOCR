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

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class OCR {

    private final String LANG_OPTION = "-l";
    private final String EOL = System.getProperty("line.separator");
    private String tessPath;
    final static String OUTPUT_FILE_NAME = "TessOutput";
    final static String FILE_EXTENSION = ".txt";

    /** Creates a new instance of OCR */
    public OCR(String tessPath) {
        this.tessPath = tessPath;
    }

    /**
     * @param tempImageFiles
     * @param lang
     * @return
     * @throws java.lang.Exception
     */
    String recognizeText(final List<File> tempImageFiles, final String lang) throws Exception {
        File tempTessOutputFile = File.createTempFile(OUTPUT_FILE_NAME, FILE_EXTENSION);
        String outputFileName = tempTessOutputFile.getPath().substring(0, tempTessOutputFile.getPath().length() - FILE_EXTENSION.length()); // chop the .txt extension

        List<String> cmd = new ArrayList<String>();
        cmd.add(tessPath + "/tesseract");
        cmd.add(""); // placeholder for inputfile
        cmd.add(outputFileName);
        cmd.add(LANG_OPTION);
        cmd.add(lang);

        ProcessBuilder pb = new ProcessBuilder();
        pb.directory(new File(System.getProperty("user.home")));
        pb.redirectErrorStream(true);

        StringBuffer result = new StringBuffer();

        for (File tempImageFile : tempImageFiles) {
//            ProcessBuilder pb = new ProcessBuilder(tessPath + "/tesseract", tempImageFile.getPath(), outputFileName, LANG_OPTION, lang);

            cmd.set(1, tempImageFile.getPath());
            pb.command(cmd);
            Process process = pb.start();

            int w = process.waitFor();
            System.out.println("Exit value = " + w);

            if (w == 0) {
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(tempTessOutputFile), "UTF-8"));

                String str;

                while ((str = in.readLine()) != null) {
                    result.append(str).append(EOL);
                }
                in.close();
            } else {
                String msg;
                switch (w) {
                    case 1:
                        msg = "Errors accessing files.";
                        break;
                    case 29:
                        msg = "Cannot recognize the image or its selected region.";
                        break;
                    case 31:
                        msg = "Unsupported image format.";
                        break;
                    default:
                        msg = "Errors occurred.";
                }

                tempTessOutputFile.delete();
                throw new RuntimeException(msg);
            }
        }

        tempTessOutputFile.delete();
        return result.toString();
    }
}
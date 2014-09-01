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

import java.io.*;
import java.net.*;
import net.sf.ghost4j.*;

public class Utilities {

    /**
     * 
     * @return the directory of the running jar
     */
    public static File getBaseDir(Object aType) {
        URL dir = aType.getClass().getResource("/" + aType.getClass().getName().replaceAll("\\.", "/") + ".class");
        File dbDir = new File(System.getProperty("user.dir"));

        try {
            if (dir.toString().startsWith("jar:")) {
                dir = new URL(dir.toString().replaceFirst("^jar:", "").replaceFirst("/[^/]+.jar!.*$", ""));
                dbDir = new File(dir.toURI());
            }
        } catch (MalformedURLException mue) {
            mue.printStackTrace();
        } catch (URISyntaxException use) {
            use.printStackTrace();
        }
        return dbDir;
    }

    /**
     * Convert PDF to TIFF format.
     *
     * @param inputPdfFile
     * @return a multi-page TIFF image
     */
    public static File convertPdf2Tiff(File inputPdfFile) {
        try {
            File[] pngFiles = convertPdf2Png(inputPdfFile);
            File tiffFile = File.createTempFile("multipage", ".tif");
            
            // put PNG images into a single multi-page TIFF image for return
            ImageIOHelper.mergeTiff(pngFiles, tiffFile);
            for (File tempFile : pngFiles) {
                tempFile.delete();
            }
            return tiffFile;
        } catch (IOException ioe) {
            System.err.println("ERROR: " + ioe.getMessage());
            return null;
        }

    }

    /**
     * Convert PDF to PNG format.
     *
     * @param inputPdfFile
     * @return an array of PNG images
     */
    public static File[] convertPdf2Png(File inputPdfFile) {
        File imageDir = inputPdfFile.getParentFile();

        //get Ghostscript instance
        Ghostscript gs = Ghostscript.getInstance();

        //prepare Ghostscript interpreter parameters
        //refer to Ghostscript documentation for parameter usage
        String[] gsArgs = new String[10];
        gsArgs[0] = "-gs";
        gsArgs[1] = "-dNOPAUSE";
        gsArgs[2] = "-dBATCH";
        gsArgs[3] = "-dSAFER";
        gsArgs[4] = "-sDEVICE=pnggray";
        gsArgs[5] = "-r300";
        gsArgs[6] = "-dGraphicsAlphaBits=4";
        gsArgs[7] = "-dTextAlphaBits=4";
        gsArgs[8] = "-sOutputFile=" + imageDir.getPath() + "/workingimage%03d.png";
        gsArgs[9] = inputPdfFile.getPath();

        //execute and exit interpreter
        try {
            gs.initialize(gsArgs);
            gs.exit();
        } catch (GhostscriptException e) {
            System.err.println("ERROR: " + e.getMessage());
        }

        // find working files
        File[] workingFiles = imageDir.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().matches("workingimage\\d{3}\\.png$");
            }
        });

        return workingFiles;
    }
}

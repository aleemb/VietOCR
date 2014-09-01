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
import javax.imageio.*;
import javax.imageio.stream.*;
import javax.imageio.metadata.*;
import com.sun.media.imageio.plugins.tiff.*;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class ImageIOHelper {

    final static String OUTPUT_FILE_NAME = "TessTempFile";
    final static String TIFF_EXT = ".tif";
    final static String TIFF_FORMAT = "tiff";

    public static List<File> createImageFiles(File imageFile, int index) throws Exception {
        List<File> tempImageFiles = new ArrayList<File>();

        String imageFileName = imageFile.getName();
        String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);

        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
        ImageReader reader = readers.next();

        if (reader == null) {
            throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
        }

        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
        reader.setInput(iis);
        //Read the stream metadata
//        IIOMetadata streamMetadata = reader.getStreamMetadata();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get tif writer and set output to file
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = writers.next();

        //Read the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        if (index == -1) {
            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
//                BufferedImage bi = reader.read(i);
//                IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(i));
                IIOImage image = reader.readAll(i, reader.getDefaultReadParam());
                File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
                ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
                writer.setOutput(ios);
                writer.write(streamMetadata, image, tiffWriteParam);
                ios.close();
                tempImageFiles.add(tempFile);
            }
        } else {
//            BufferedImage bi = reader.read(index);
//            IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(index));
            IIOImage image = reader.readAll(index, reader.getDefaultReadParam());
            File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
            ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
            writer.setOutput(ios);
            writer.write(streamMetadata, image, tiffWriteParam);
            ios.close();
            tempImageFiles.add(tempFile);
        }
        writer.dispose();
        reader.dispose();

        return tempImageFiles;
    }

    public static List<File> createImageFiles(List<IIOImage> imageList, int index) throws Exception {
        List<File> tempImageFiles = new ArrayList<File>();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get tif writer and set output to file
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = writers.next();

        if (writer == null) {
            throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
        }

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        if (index == -1) {
            for (IIOImage image : imageList) {
                File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
                ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
                writer.setOutput(ios);
                writer.write(streamMetadata, image, tiffWriteParam);
                ios.close();
                tempImageFiles.add(tempFile);
            }
        } else {
            IIOImage image = imageList.get(index);
            File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
            ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
            writer.setOutput(ios);
            writer.write(streamMetadata, image, tiffWriteParam);
            ios.close();
            tempImageFiles.add(tempFile);
        }
        writer.dispose();

        return tempImageFiles;
    }

    public static List<IIOImage> getIIOImageList(File imageFile) throws Exception {
        ImageReader reader = null;
        ImageInputStream iis = null;

        try {
            List<IIOImage> iioImageList = new ArrayList<IIOImage>();

            String imageFileName = imageFile.getName();
            String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageFormat);
            reader = readers.next();

            if (reader == null) {
                throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
            }

            iis = ImageIO.createImageInputStream(imageFile);
            reader.setInput(iis);

            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
//                IIOImage image = new IIOImage(reader.read(i), null, reader.getImageMetadata(i));
                IIOImage image = reader.readAll(i, reader.getDefaultReadParam());
                iioImageList.add(image);
            }

            return iioImageList;
        } finally {
            try {
                if (iis != null) {
                    iis.close();
                }
                if (reader != null) {
                    reader.dispose();
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static void mergeTiff(File[] inputImages, File outputTiff) throws Exception {
        List<IIOImage> imageList = new ArrayList<IIOImage>();

        for (int i = 0; i < inputImages.length; i++) {
            imageList.addAll(getIIOImageList(inputImages[i]));
        }

        if (imageList.size() == 0) {
            // if no image
            return;
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = writers.next();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff);
        writer.setOutput(ios);

        IIOImage firstIioImage = imageList.remove(0);
        writer.write(streamMetadata, firstIioImage, tiffWriteParam);

        int i = 1;
        for (IIOImage iioImage : imageList) {
            writer.writeInsert(i++, iioImage, tiffWriteParam);
        }
        ios.close();

        writer.dispose();
    }
}

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
import java.awt.image.*;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class ImageIOHelper {

    final static String OUTPUT_FILE_NAME = "TessTempFile";
    final static String TIFF_EXT = ".tif";
    final static String TIFF_FORMAT = "tiff";

    public static ArrayList<File> createImageFiles(File imageFile, int index) throws Exception {
        ArrayList<File> tempImageFiles = new ArrayList<File>();

        String imageFileName = imageFile.getName();
        String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);

        Iterator readers = ImageIO.getImageReadersByFormatName(imageFormat);
        ImageReader reader = (ImageReader) readers.next();

        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
        reader.setInput(iis);
        //Read the stream metadata
//        IIOMetadata streamMetadata = reader.getStreamMetadata();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get tif writer and set output to file
        Iterator writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = (ImageWriter) writers.next();
        
        //Read the stream metadata
        IIOMetadata streamMetadata = writer.getDefaultStreamMetadata(tiffWriteParam);

        if (index == -1) {
            int imageTotal = reader.getNumImages(true);

            for (int i = 0; i < imageTotal; i++) {
                BufferedImage bi = reader.read(i);
                IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(i));
                File tempFile = File.createTempFile(OUTPUT_FILE_NAME, TIFF_EXT);
                ImageOutputStream ios = ImageIO.createImageOutputStream(tempFile);
                writer.setOutput(ios);
                writer.write(streamMetadata, image, tiffWriteParam);
                ios.close();
                tempImageFiles.add(tempFile);
            }
        } else {
            BufferedImage bi = reader.read(index);
            IIOImage image = new IIOImage(bi, null, reader.getImageMetadata(index));
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

    public static ArrayList<File> createImageFiles(ArrayList<IIOImage> imageList, int index) throws Exception {
        ArrayList<File> tempImageFiles = new ArrayList<File>();

        //Set up the writeParam
        TIFFImageWriteParam tiffWriteParam = new TIFFImageWriteParam(Locale.US);
        tiffWriteParam.setCompressionMode(ImageWriteParam.MODE_DISABLED);

        //Get tif writer and set output to file
        Iterator writers = ImageIO.getImageWritersByFormatName(TIFF_FORMAT);
        ImageWriter writer = (ImageWriter) writers.next();

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

    public static ArrayList<IIOImage> getIIOImageList(File imageFile) throws Exception {
        ArrayList<IIOImage> iioImageList = new ArrayList<IIOImage>();

        String imageFileName = imageFile.getName();
        String imageFormat = imageFileName.substring(imageFileName.lastIndexOf('.') + 1);
        Iterator readers = ImageIO.getImageReadersByFormatName(imageFormat);
        ImageReader reader = (ImageReader) readers.next();

        if (reader == null) {
            throw new RuntimeException("Need to install JAI Image I/O package.\nhttps://jai-imageio.dev.java.net");
        }

        ImageInputStream iis = ImageIO.createImageInputStream(imageFile);
        reader.setInput(iis);

        int imageTotal = reader.getNumImages(true);

        for (int i = 0; i < imageTotal; i++) {
            IIOImage image = new IIOImage(reader.read(i), null, reader.getImageMetadata(i));
            iioImageList.add(image);
        }

        reader.dispose();

        return iioImageList;
    }

    public static ArrayList<ImageIconScalable> getImageList(ArrayList<IIOImage> iioImageList) {
         ArrayList<ImageIconScalable> al = new ArrayList<ImageIconScalable>();
         for (IIOImage iioImage : iioImageList) {
             al.add(new ImageIconScalable((BufferedImage)iioImage.getRenderedImage()));
         }

         return al;
    }
}

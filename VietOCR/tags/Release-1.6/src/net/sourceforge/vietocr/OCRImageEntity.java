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

import java.io.File;
import java.util.List;
import javax.imageio.IIOImage;

public class OCRImageEntity {

    private List<IIOImage> originalImages;
    private File originalImageFile;
    private int index;

    public OCRImageEntity(List<IIOImage> originalImages, int index) {
        this.originalImages = originalImages;
        this.index = index;
    }

    public OCRImageEntity(File originalImageFile, int index) {
        this.originalImageFile = originalImageFile;
        this.index = index;
    }

    /**
     * @return the originalImages
     */
    public List<IIOImage> getOriginalImages() {
        return originalImages;
    }

    /**
     * @return the originalImageFile
     */
    public File getOriginalImageFile() {
        return originalImageFile;
    }

    /**
     * @return the ClonedImageFiles
     */
    public List<File> getClonedImageFiles() throws Exception {
        if (originalImages != null) {
            return ImageIOHelper.createImageFiles(originalImages, index);
        } else {
            return ImageIOHelper.createImageFiles(originalImageFile, index);
        }
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }
}

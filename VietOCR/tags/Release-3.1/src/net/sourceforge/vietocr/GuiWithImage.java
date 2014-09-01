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

import javax.swing.JOptionPane;

public class GuiWithImage extends GuiWithPostprocess {

    GuiWithImage() {
        this.jCheckBoxMenuItemScreenshotMode.setSelected(prefs.getBoolean("ScreenshotMode", false));
    }

    @Override
    void readImageMetadata() {
        if (iioImageList == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ImageInfoDialog dialog = new ImageInfoDialog(this, true);
        dialog.setImage(iioImageList.get(imageIndex));
        if (dialog.showDialog() == JOptionPane.OK_OPTION) {
            // Do nothing for now.
            // Initial plan was to implement various image manipulation operations
            // (rotate, flip, sharpen, brighten, threshold, clean up,...) here.
        }
    }

    @Override
    void quit() {
        prefs.putBoolean("ScreenshotMode", this.jCheckBoxMenuItemScreenshotMode.isSelected());

        super.quit();
    }
}

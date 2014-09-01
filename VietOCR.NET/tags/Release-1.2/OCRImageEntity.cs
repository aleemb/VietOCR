/**
 * Copyright @ 2008 Quan Nguyen
 * 
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace VietOCR.NET
{
    class OCRImageEntity
    {
        IList<Image> originalImages;
        
        public IList<Image> OriginalImages
        {
            get { return originalImages; }
            set { originalImages = value; }
        }

        public IList<Image> ClonedImages
        {
            get { return Clone(originalImages); }
        }

        int index;

        public int Index
        {
            get { return index; }
            set { index = value; }
        }

        Rectangle rect;

        public Rectangle Rect
        {
            get { return rect; }
            set { rect = value; }
        }

        String lang;

        public String Lang
        {
            get { return lang; }
            set { lang = value; }
        }

        public OCRImageEntity(IList<Image> originalImages, int index, Rectangle rect, String lang)
        {
            this.originalImages = originalImages;
            this.index = index;
            this.rect = rect;
            this.lang = lang;
        }

        /// <summary>
        /// Clone a list of images.
        /// </summary>
        /// <param name="originalImages">List of original images.</param>
        /// <returns>All or one cloned image.</returns>
        private IList<Image> Clone(IList<Image> originalImages)
        {
            IList<Image> images = new List<Image>();

            if (index == -1)
            {
                foreach (Image image in originalImages)
                {
                    images.Add(new Bitmap(image));
                }
            }
            else
            {
                images.Add(new Bitmap(originalImages[index]));
            }

            return images;
        }
    }
}

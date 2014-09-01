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
        IList<Image> images;

        public IList<Image> Images
        {
            get { return images; }
            set { images = value; }
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

        public OCRImageEntity(IList<Image> images, int index, Rectangle rect, String lang)
        {
            this.images = images;
            this.index = index;
            this.rect = rect;
            this.lang = lang;
        }
    }
}

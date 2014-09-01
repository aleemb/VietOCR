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
                    images.Add(image);
                }
            }
            else
            {
                images.Add(originalImages[index]);
            }

            return images;
        }

        /// <summary>
        /// Not used after all since the Picturebox already uses copies of these images.
        /// </summary>
        /// <param name="source"></param>
        /// <returns></returns>
        //private Image Clone(Image source)
        //{
            //PixelFormat pxf = source.PixelFormat;
            //Bitmap bmp = (Bitmap)source;
            //Rectangle rect = new Rectangle(0, 0, bmp.Width, bmp.Height);

            //BitmapData bData = bmp.LockBits(rect, ImageLockMode.ReadOnly, pxf);
            //// number of bytes in the bitmap
            //int byteCount = bData.Stride * bmp.Height;
            //byte[] bmpBytes = new byte[byteCount];

            //// Copy the locked bytes from memory
            //Marshal.Copy(bData.Scan0, bmpBytes, 0, byteCount);
            //bmp.UnlockBits(bData);

            //Bitmap bmpTarget = new Bitmap(source.Width, source.Height, pxf);
            //bmpTarget.SetResolution(source.HorizontalResolution, source.VerticalResolution);
            //BitmapData bDataTarget = bmpTarget.LockBits(rect, ImageLockMode.WriteOnly, pxf);
            //// Copy the bytes to the bitmap object
            //Marshal.Copy(bmpBytes, 0, bDataTarget.Scan0, bmpBytes.Length);
            //bmpTarget.UnlockBits(bDataTarget);
            //return bmpTarget;


            ////create temporary
            //Image temp = new Bitmap(source.Width, source.Height);
            //((Bitmap)temp).SetResolution(source.HorizontalResolution, source.VerticalResolution);

            ////get graphics
            //Graphics g = Graphics.FromImage(temp);

            ////copy original
            //g.DrawImage(source, 0, 0);
            //g.Dispose();

            ////return temp;
            //using (MemoryStream ms = new MemoryStream())
            //{
            //    bmp.Save(ms, ImageFormat.Png);
            //    //byte[] bmpBytes = ms.GetBuffer();
            //    //using (MemoryStream ms1 = new MemoryStream(bmpBytes))
            //    //{
            //        return Image.FromStream(ms);
            //    //}
            //}
        //}
    }
}

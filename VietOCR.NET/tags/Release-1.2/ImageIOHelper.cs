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
using System;
using System.Collections.Generic;
using System.IO;
using System.Drawing;
using System.Drawing.Imaging;
using System.Drawing.Drawing2D;
using System.Collections;
using System.Windows.Forms;

namespace VietOCR.NET
{
    class ImageIOHelper
    {
        /// <summary>
        /// Get image(s) from file
        /// </summary>
        /// <param name="imageFile">file name</param>
        /// <returns>list of images</returns>
        public static IList<Image> GetImageList(FileInfo imageFile)
        {
            Image image = null;

            try
            {
                // read in the image
                image = Image.FromFile(imageFile.FullName);

                IList<Image> images = new List<Image>();

                int count = image.GetFrameCount(FrameDimension.Page);
                for (int i = 0; i < count; i++)
                {
                    // save each frame to a bytestream
                    using (MemoryStream byteStream = new MemoryStream())
                    {
                        image.SelectActiveFrame(FrameDimension.Page, i);
                        image.Save(byteStream, ImageFormat.Bmp);

                        // and then create a new Image from it
                        images.Add(Image.FromStream(byteStream));
                    }
                }

                return images;
            }
            catch
            {
                return null;
            }
            finally
            {
                if (image != null)
                {
                    image.Dispose();
                }
            }
        }

        public static Image Crop(Image image, Rectangle cropArea)
        {
            try
            {
                Bitmap bmp = new Bitmap(cropArea.Width, cropArea.Height);
                bmp.SetResolution(300, 300);

                Graphics gfx = Graphics.FromImage(bmp);
                gfx.SmoothingMode = SmoothingMode.AntiAlias;
                gfx.InterpolationMode = InterpolationMode.HighQualityBicubic;
                gfx.PixelOffsetMode = PixelOffsetMode.HighQuality;
                gfx.DrawImage(image, 0, 0, cropArea, GraphicsUnit.Pixel);
                gfx.Dispose();

                return bmp;
            }
            catch (Exception exc)
            {
                Console.WriteLine(exc.Message);
                return null;
            }
        }

        public static Image Crop2(Image image, Rectangle cropArea)
        {
            Bitmap bitmap = new Bitmap(image);
            bitmap.SetResolution(image.HorizontalResolution, image.VerticalResolution);
            return bitmap.Clone(cropArea, bitmap.PixelFormat);
        }

        public static void MergeTiff(string[] inputImages, string outputTiff)
        {
            //get the codec for tiff files
            ImageCodecInfo info = null;

            foreach (ImageCodecInfo ice in ImageCodecInfo.GetImageEncoders())
            {
                if (ice.MimeType == "image/tiff")
                {
                    info = ice;
                }
            }

            //use the save encoder
            System.Drawing.Imaging.Encoder enc = System.Drawing.Imaging.Encoder.SaveFlag;
            EncoderParameters ep = new EncoderParameters(2);
            ep.Param[0] = new EncoderParameter(enc, (long)EncoderValue.MultiFrame);
            Encoder enc1 = Encoder.Compression;
            ep.Param[1] = new EncoderParameter(enc1, (long)EncoderValue.CompressionNone);
            Bitmap pages = null;

            int frame = 0;

            foreach (string inputImage in inputImages)
            {
                if (frame == 0)
                {
                    pages = (Bitmap)Image.FromFile(inputImage);
                    //save the first frame
                    pages.Save(outputTiff, info, ep);
                }
                else
                {
                    //save the intermediate frames
                    ep.Param[0] = new EncoderParameter(enc, (long)EncoderValue.FrameDimensionPage);
                    Bitmap bm = (Bitmap)Image.FromFile(inputImage);
                    pages.SaveAdd(bm, ep);
                    bm.Dispose();
                }

                if (frame == inputImages.Length - 1)
                {
                    //flush and close
                    ep.Param[0] = new EncoderParameter(enc, (long)EncoderValue.Flush);
                    pages.SaveAdd(ep);
                }
                frame++;
            }

            if (pages != null)
            {
                pages.Dispose();
            }
        }
    }
}

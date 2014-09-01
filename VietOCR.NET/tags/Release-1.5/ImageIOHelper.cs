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
            string workingTiffFileName = null;

            Image image = null;

            try
            {
                // convert PDF to TIFF
                if (imageFile.Name.ToLower().EndsWith(".pdf"))
                {
                    workingTiffFileName = Utilities.ConvertPdf2Tiff(imageFile.FullName);
                    imageFile = new FileInfo(workingTiffFileName);
                }

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
                        image.Save(byteStream, ImageFormat.Png);

                        // and then create a new Image from it
                        images.Add(Image.FromStream(byteStream));
                    }
                }

                return images;
            }
            catch (OutOfMemoryException e)
            {
                throw new ApplicationException(e.Message, e);
            }
            catch (System.Runtime.InteropServices.ExternalException e)
            {
                throw new ApplicationException(e.Message + "\nIt might have run out of memory due to handling too many images or too large a file.", e);
            }
            finally
            {
                if (image != null)
                {
                    image.Dispose();
                }

                if (workingTiffFileName != null && File.Exists(workingTiffFileName))
                {
                    File.Delete(workingTiffFileName);
                }
            }
        }

        /// <summary>
        /// Crop an image.
        /// </summary>
        /// <param name="image"></param>
        /// <param name="cropArea"></param>
        /// <returns></returns>
        //public static Image Crop(Image image, Rectangle cropArea)
        //{
        //    try
        //    {
        //        Bitmap bmp = new Bitmap(cropArea.Width, cropArea.Height);
        //        bmp.SetResolution(300, 300);

        //        Graphics gfx = Graphics.FromImage(bmp);
        //        gfx.SmoothingMode = SmoothingMode.AntiAlias;
        //        gfx.InterpolationMode = InterpolationMode.HighQualityBicubic;
        //        gfx.PixelOffsetMode = PixelOffsetMode.HighQuality;
        //        gfx.DrawImage(image, 0, 0, cropArea, GraphicsUnit.Pixel);
        //        gfx.Dispose();

        //        return bmp;
        //    }
        //    catch (Exception exc)
        //    {
        //        Console.WriteLine(exc.Message);
        //        return null;
        //    }
        //}

        /// <summary>
        /// Crop an image (another method).
        /// </summary>
        /// <param name="image"></param>
        /// <param name="cropArea"></param>
        /// <returns></returns>
        //public static Image Crop2(Image image, Rectangle cropArea)
        //{
        //    Bitmap bitmap = new Bitmap(image);
        //    bitmap.SetResolution(image.HorizontalResolution, image.VerticalResolution);
        //    return bitmap.Clone(cropArea, bitmap.PixelFormat);
        //}

        /// <summary>
        /// Merge multiple images into one TIFF image.
        /// </summary>
        /// <param name="inputImages"></param>
        /// <param name="outputTiff"></param>
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

            try
            {
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
                        Bitmap bm = null;
                        try
                        {
                            bm = (Bitmap)Image.FromFile(inputImage);
                            pages.SaveAdd(bm, ep);
                        }
                        catch (System.Runtime.InteropServices.ExternalException e)
                        {
                            throw new ApplicationException(e.Message + "\nIt might have run out of memory due to handling too many images or too large a file.", e);
                        }
                        finally
                        {
                            if (bm != null)
                            {
                                bm.Dispose();
                            }
                        }
                    }

                    if (frame == inputImages.Length - 1)
                    {
                        //flush and close
                        ep.Param[0] = new EncoderParameter(enc, (long)EncoderValue.Flush);
                        pages.SaveAdd(ep);
                    }
                    frame++;
                }
            }
            finally
            {
                if (pages != null)
                {
                    pages.Dispose();
                }
            }
        }

        /// <summary>
        /// Get an Image from Clipboard
        /// </summary>
        /// <returns></returns>
        public static Image GetClipboardImage()
        {
            if (Clipboard.ContainsImage())
            {
                return Clipboard.GetImage();
            }
            return null;
        }
    }
}

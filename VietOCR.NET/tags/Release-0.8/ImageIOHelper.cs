using System;
using System.Collections.Generic;
using System.Text;
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
            try
            {
                // read in the image
                Image image = Image.FromFile(imageFile.FullName);

                IList<Image> images = new List<Image>();

                int count = image.GetFrameCount(FrameDimension.Page);
                for (int i = 0; i < count; i++)
                {
                    // save each frame to a bytestream
                    image.SelectActiveFrame(FrameDimension.Page, i);
                    MemoryStream byteStream = new MemoryStream();
                    image.Save(byteStream, ImageFormat.Bmp);

                    // and then create a new Image from it
                    images.Add(Image.FromStream(byteStream));
                }

                return images;
            }
            catch
            {
                return null;
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

    }
}

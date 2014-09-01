using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using ConvertPDF;

namespace VietOCR.NET
{
    class Utilities
    {    
        /// <summary>
        /// Convert PDF to TIFF format.
        /// </summary>
        /// <param name="inputPdfFile"></param>
        /// <returns>a multi-page TIFF image</returns>
        public static string ConvertPdf2Tiff(string inputPdfFile)
        {
            try {
                string[] pngFiles = ConvertPdf2Png(inputPdfFile);
                string tiffFile = Path.GetTempFileName();
                tiffFile = Path.ChangeExtension(tiffFile, ".tif");
                
                // put PNG images into a single multi-page TIFF image for return
                ImageIOHelper.MergeTiff(pngFiles, tiffFile);

                // delete temporary PNG images
                foreach (string tempFile in pngFiles)
                {
                    new FileInfo(tempFile).Delete();
                }

                return tiffFile;
            }
            catch (Exception e)
            {
                Console.WriteLine("ERROR: " + e.Message);
                return null;
            }
        }
 
        /// <summary>
        /// Convert PDF to PNG format.
        /// </summary>
        /// <param name="inputPdfFile"></param>
        /// <returns>an array of PNG images</returns>
        public static string[] ConvertPdf2Png(string inputPdfFile) 
        {
            PDFConvert converter = new PDFConvert();
            converter.GraphicsAlphaBit = 4;
            converter.TextAlphaBit = 4;
            converter.ResolutionX = 300; // -r300
            converter.OutputFormat = "pnggray"; // -sDEVICE

            string sOutputFile = string.Format("{0}\\workingimage%03d.png", Path.GetDirectoryName(inputPdfFile));
            bool success = converter.Convert(inputPdfFile, sOutputFile);

            if (success)
            {
                // find working files
                string[] workingFiles = Directory.GetFiles(Path.GetDirectoryName(inputPdfFile), "workingimage???.png");
                return workingFiles;
            }
            else
            {
                return new string[0];
            }
        }
    }
}

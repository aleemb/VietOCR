﻿using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using System.Drawing;
using VietOCR.NET.Postprocessing;
using VietOCR.NET.Utilities;

namespace VietOCR.NET
{
    class ConsoleApp
    {
        public static void Main(string[] args)
        {
            new ConsoleApp().PerformOCR(args);
        }

        void PerformOCR(string[] args)
        {
            if (args[0] == "-?" || args[0] == "-help" || args.Length == 1 || args.Length >= 8)
            {
                Console.WriteLine("Usage: vietocr imagefile outputfile [-l lang] [-psm pagesegmode] [hocr]");
                return;
            }

            string outputFormat = "txt";
            foreach (string arg in args)
            {
                if ("hocr" == arg)
                {
                    outputFormat = "hocr";
                } 
                else if ("pdf" == arg) 
                {
                    outputFormat = "pdf";
                }
                else if ("txt+" == arg)
                {
                    outputFormat = "txt+";
                }
            }

            FileInfo imageFile = new FileInfo(args[0]);
            FileInfo outputFile = new FileInfo(args[1] + "." + outputFormat.Replace("+", string.Empty));

            if (!imageFile.Exists)
            {
                Console.WriteLine("Input file does not exist.");
                return;
            }

            string curLangCode = "eng"; //default language
            string psm = "3"; // or alternatively, "Auto"; // 3 - Fully automatic page segmentation, but no OSD (default)

            if ((args.Length == 4) || (args.Length == 5))
            {
                if (args[2].Equals("-l"))
                {
                    curLangCode = args[3];
                }
                else if (args[2].Equals("-psm"))
                {
                    psm = args[3];
                }
            }
            else if ((args.Length == 6) || (args.Length == 7))
            {
                curLangCode = args[3];
                psm = args[5];
                try
                {
                    Int16.Parse(psm);
                }
                catch
                {
                    Console.WriteLine("Invalid input value.");
                    return;
                }
            }

            try
            {
                OCRHelper.PerformOCR(imageFile.FullName, outputFile.FullName, curLangCode, psm, outputFormat);
            }
            catch (Exception e)
            {
                Console.WriteLine("Error: " + e.Message);
            }
        }
    }
}

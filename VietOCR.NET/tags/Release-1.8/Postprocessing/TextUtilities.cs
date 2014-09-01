﻿using System;
using System.Collections.Generic;
using System.Text;
using System.Text.RegularExpressions;
using System.IO;

namespace VietOCR.NET.Postprocessing
{
    class TextUtilities
    {
        /// <summary>
        /// Corrects letter cases.
        /// </summary>
        /// <param name="input"></param>
        /// <returns></returns>
        public static string CorrectLetterCases(String input)
        {
            // lower uppercase letters ended by lowercase letters except the first letter
            Regex regex = new Regex("(?<=\\p{L}+)(\\p{Lu}+)(?=\\p{Ll}+)");
            input = regex.Replace(input, new MatchEvaluator(LowerCaseText));

            //// lower uppercase letters begun by lowercase letters
            regex = new Regex("(?<=\\p{Ll}+)(\\p{Lu}+)");
            input = regex.Replace(input, new MatchEvaluator(LowerCaseText));

            return input;
        }

        static string LowerCaseText(Match m)
        {
            // Lowercase the matched string.
            return m.Value.ToLower();
        }

        /// <summary>
        /// Corrects common Tesseract OCR errors.
        /// </summary>
        /// <param name="input"></param>
        /// <returns></returns>
        public static string CorrectOCRErrors(String input)
        {            
            // substitute letters frequently misrecognized by Tesseract 2.03
            return Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(input,
                        "\\b1(?=\\p{L}+\\b)", "l"), // 1 to l
                        "\\b11(?=\\p{L}+\\b)", "n"), // 11 to n
                        "\\bI(?![mn]+\\b)", "l"), // I to l
                        "(?<=\\b\\p{L}*)0(?=\\p{L}*\\b)", "o") // 0 to o
                //Regex.Replace("(?<!\\.) S(?=\\p{L}*\\b)", " s") // S to s
                //Regex.Replace("(?<![cn])h\\b", "n")
            ;
        }

        public static Dictionary<string, string> LoadMap(string dangAmbigsFile)
        {
            Dictionary<string, string> map = new Dictionary<string, string>();

            try
            {
                StreamReader sr = new StreamReader(dangAmbigsFile, Encoding.UTF8);
                string str;

                while ((str = sr.ReadLine()) != null)
                {
                    int index = str.IndexOf('=');
                    if (index == -1)
                    {
                        continue;
                    }

                    string key = str.Substring(0, index);
                    string value = str.Substring(index + 1);
                    map[key] = value;
                }
                sr.Close();   
            }
            catch (Exception e)
            {
                Console.WriteLine(e.StackTrace);
            }

            return map;
        }
    }
}

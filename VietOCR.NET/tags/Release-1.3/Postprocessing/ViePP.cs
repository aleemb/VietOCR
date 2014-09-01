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
namespace VietOCR.NET.Postprocessing
{
    using System;
    using System.Text;
    using System.Text.RegularExpressions;

    /**
     *
     * @author Quan Nguyen (nguyenq@users.sf.net)
     */
    public class ViePP : IPostProcessor
    {
        const string TONE = "[\u0300\u0309\u0303\u0301\u0323]?"; // '`?~.
        const string DOT_BELOW = "\u0323?"; // .
        const string MARK = "[\u0306\u0302\u031B]?"; // (^+
        const string VOWEL = "[aeiouy]";

        public string PostProcess(string text)
        {
            if (text.Trim().Length == 0)
            {
                return text;
            }

            // correct common errors caused by OCR
            text = TextUtilities.CorrectOCRErrors(text);

            // substitute Vietnamese letters frequently misrecognized by Tesseract 2.03
            StringBuilder strB = new StringBuilder(text);
            strB.Replace("êĩ-", "ết")
                .Replace("ug", "ng")
                .Replace("uh", "nh")
                .Replace("rn", "m")
                .Replace("iii", "m")
                .Replace("ll", "u")
                .Replace("II", "u")
                .Replace("ôh", "ốn")
                .Replace("âỳ", "ấy")
                .Replace("u1I", "ưn")
                .Replace("q1I", "qu")
                .Replace("tmg", "úng")
                .Replace("tm", "trư")
                .Replace("Tm", "Trư")
                .Replace("êf", "ết")
                .Replace("rg", "ng")
                .Replace("êh", "ến")
                .Replace("‘â", "ầ")
                .Replace("fâ", "rầ")
                ;

            // correct letter cases
            text = TextUtilities.CorrectLetterCases(strB.ToString());

            // add hook marks
            //                    .ReplaceAll("(?<![qQ])(u)(?=[ơờởỡớợ]\\p{L})", "ư")
            //                    .ReplaceAll("(?<![qQ])(U)(?=[ƠỜỞỠỚỢ]\\p{L})", "Ư")
            //                    .Replace("ưon", "ươn")
            //                    .Replace("ưoi", "ươi");

            string nfdText = text.Normalize(NormalizationForm.FormD);
            nfdText = Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(nfdText,
                        "(?i)(?<![qQ])(u)(?=o\u031B" + TONE + "\\p{L})", "$1\u031B"), // uo+n to u+o+n 
                        "(?i)(?<=u\u031B)(o)(?=" + TONE + "\\p{L})", "$1\u031B"), // u+on to u+o+n
                        "(?i)(i)" + TONE + "(?=[eioy])", "$1"), // remove mark on i followed by certain vowels
                        "(?i)(?<=" + VOWEL + DOT_BELOW + TONE + MARK + ")(i)" + TONE + "\\b", "$1") // // remove mark on i preceeded by vowels
                    ;

            return nfdText.Normalize();
        }
    }
}
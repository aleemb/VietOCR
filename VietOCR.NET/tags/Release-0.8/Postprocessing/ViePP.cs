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
namespace net.sourceforge.vietocr.postprocessing
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

            // substitute letters frequently misrecognized by Tesseract 2.03
            text = Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(
                    Regex.Replace(text,
                        "\\b1(?=\\p{L}+\\b)", "l"), // 1 to l
                        "\\b11(?=\\p{L}+\\b)", "n"), // 11 to n
                        "\\bI(?=\\p{Ll}+\\b)", "l"), // I to l
                        "(?<=\\b\\p{L}*)0(?=\\p{L}*\\b)", "o") // 0 to o

                //                    .ReplaceAll("(?<!\\.) S(?=\\p{L}*\\b)", " s") // S to s
                //                    .ReplaceAll("(?<![cn])h\\b", "n")
            ;

            StringBuilder strB = new StringBuilder(text);

            strB.Replace("êĩ-", "ết")
                .Replace("ug", "ng")
                .Replace("uh", "nh")
                .Replace("rn", "m")
                .Replace("iii", "m")
                .Replace("ll", "u")
                .Replace("II", "u")
                .Replace("ôh", "ốn");

            // lower uppercase letters ended by lowercase letters except the first letter
            Regex regex = new Regex("(?<=\\p{L}+)(\\p{Lu}+)(?=\\p{Ll}+)");
            text = regex.Replace(strB.ToString(), new MatchEvaluator(LowerCaseText));

            //// lower uppercase letters begun by lowercase letters
            regex = new Regex("(?<=\\p{Ll}+)(\\p{Lu}+)");
            text = regex.Replace(text, new MatchEvaluator(LowerCaseText));

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

        string LowerCaseText(Match m)
        {
            // Lowercase the matched string.
            return m.Value.ToLower();
        }

    }
}
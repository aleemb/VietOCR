
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

package net.sourceforge.vietocr.postprocessing;

import java.text.Normalizer; // available in Java 6.0

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class ViePP implements IPostProcessor {
    final String TONE = "[\u0300\u0309\u0303\u0301\u0323]?"; // `?~'.
    final String DOT_BELOW = "\u0323?"; // .
    final String MARK = "[\u0306\u0302\u031B]?"; // (^+
    final String VOWEL = "[aeiouy]";

    @Override
    public String postProcess(String text) {
        if (text.trim().length() == 0) {
            return text;
        }

        // correct common errors caused by OCR
        text = TextUtilities.correctOCRErrors(text);

        // substitute Vietnamese letters frequently misrecognized by Tesseract
        text = text.replace("êĩ-", "ết")
                .replace("ug", "ng")
                .replace("uh", "nh")
                .replace("rn", "m")
                .replace("iii", "m")
                .replace("II", "u")
                .replace("ôh", "ốn")
                .replace("âỳ", "ấy")
                .replace("u1I", "ưn")
                .replace("q1I", "qu")
                .replace("tmg", "úng")
                .replace("tm", "trư")
                .replace("Tm", "Trư")
                .replace("êf", "ết")
                .replace("rg", "ng")
                .replace("êh", "ến")
                .replace("fâ", "rầ")
                ;

        // correct letter cases
        text = TextUtilities.correctLetterCases(text);

        // add hook marks
//        text = text.replaceAll("(?i)(?<![q])(u)(?=[ơờởỡớợ]\\p{L})", "ư")
//                .replace("ưon", "ươn")
//                .replace("ưoi", "ươi");

        String nfdText = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("(?i)(?<![q])(u)(?=o\u031B" + TONE + "\\p{L})", "$1\u031B") // uo+n to u+o+n 
                .replaceAll("(?i)(?<=u\u031B)(o)(?=" + TONE + "\\p{L})", "$1\u031B") // u+on to u+o+n
                .replaceAll("(?i)(i)" + TONE + "(?=[eioy])", "$1") // remove mark on i followed by certain vowels
                .replaceAll("(?i)(?<=[^q]" + VOWEL + "\\p{InCombiningDiacriticalMarks}{0,2})(i)" + TONE + "\\b", "$1") // remove mark on i preceeded by vowels w/ or w/o diacritics
                .replaceAll("(?i)(?<=[aeo]\u0302)\u2019", "\u0301") // ^right-single-quote to ^acute
                .replaceAll("(?i)\u2018([aeo]\u0302)(?!\\p{InCombiningDiacriticalMarks})", "$1\u0300") // left-single-quote+a^ to a^grave
                ;

        return Normalizer.normalize(nfdText, Normalizer.Form.NFC);
    }
}
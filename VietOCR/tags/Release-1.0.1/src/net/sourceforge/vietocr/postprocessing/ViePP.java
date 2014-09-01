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
    final String TONE = "[\u0300\u0309\u0303\u0301\u0323]?"; // '`?~.
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
                .replace("ll", "u")
                .replace("II", "u")
                .replace("ôh", "ốn")
                .replace("âỳ", "ấy")
                .replace("u1I", "ưn")
                .replace("q1I", "qu")
                .replace("tmg", "úng")
                .replace("tm", "trư")
                .replace("Tm", "Trư")
                ;

        // correct letter cases
        text = TextUtilities.correctLetterCases(text);

        // add hook marks
//        text = text.replaceAll("(?<![qQ])(u)(?=[ơờởỡớợ]\\p{L})", "ư")
//                .replaceAll("(?<![qQ])(U)(?=[ƠỜỞỠỚỢ]\\p{L})", "Ư")
//                .replace("ưon", "ươn")
//                .replace("ưoi", "ươi");

        String nfdText = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("(?i)(?<![qQ])(u)(?=o\u031B" + TONE + "\\p{L})", "$1\u031B") // uo+n to u+o+n 
                .replaceAll("(?i)(?<=u\u031B)(o)(?=" + TONE + "\\p{L})", "$1\u031B") // u+on to u+o+n
                .replaceAll("(?i)(i)" + TONE + "(?=[eioy])", "$1") // remove mark on i followed by certain vowels
                .replaceAll("(?i)(?<=" + VOWEL + DOT_BELOW + TONE + MARK + ")(i)" + TONE + "\\b", "$1") // // remove mark on i preceeded by vowels
                ;

        return Normalizer.normalize(nfdText, Normalizer.Form.NFC);
    }
}

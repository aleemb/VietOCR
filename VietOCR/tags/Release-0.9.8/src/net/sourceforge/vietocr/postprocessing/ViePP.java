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

import java.util.regex.*;
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

        // substitute letters frequently misrecognized by Tesseract 2.03
        text = text.replaceAll("\\b1(?=\\p{L}+\\b)", "l") // 1 to l
                .replaceAll("\\b11(?=\\p{L}+\\b)", "n") // 11 to n
                .replaceAll("\\bI(?=\\p{Ll}+\\b)", "l") // I to l
                .replaceAll("(?<=\\b\\p{L}*)0(?=\\p{L}*\\b)", "o") // 0 to o
                //                    .replaceAll("(?<!\\.) S(?=\\p{L}*\\b)", " s") // S to s
                //                    .replaceAll("(?<![cn])h\\b", "n")                    
                .replace("êĩ-", "ết")
                .replace("ug", "ng")
                .replace("uh", "nh")
                .replace("rn", "m")
                .replace("iii", "m")
                .replace("ll", "u")
                .replace("II", "u");

        StringBuffer strB = new StringBuffer();

        // lower uppercase letters ended by lowercase letters except the first letter
        Matcher matcher = Pattern.compile("(?<=\\p{L}+)(\\p{Lu}+)(?=\\p{Ll}+)").matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(strB, matcher.group().toLowerCase());
        }
        matcher.appendTail(strB);

        // lower uppercase letters begun by lowercase letters
        matcher = Pattern.compile("(?<=\\p{Ll}+)(\\p{Lu}+)").matcher(strB.toString());
        strB.setLength(0);
        while (matcher.find()) {
            matcher.appendReplacement(strB, matcher.group().toLowerCase());
        }
        matcher.appendTail(strB);

        // add hook marks
        text = strB.toString();
//                    .replaceAll("(?<![qQ])(u)(?=[ơờởỡớợ]\\p{L})", "ư")
//                    .replaceAll("(?<![qQ])(U)(?=[ƠỜỞỠỚỢ]\\p{L})", "Ư")
//                    .replace("ưon", "ươn")
//                    .replace("ưoi", "ươi");

        String nfdText = Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("(?i)(?<![qQ])(u)(?=o\u031B" + TONE + "\\p{L})", "$1\u031B") // uo+n to u+o+n 
                .replaceAll("(?i)(?<=u\u031B)(o)(?=" + TONE + "\\p{L})", "$1\u031B") // u+on to u+o+n
                .replaceAll("(?i)(i)" + TONE + "(?=[eioy])", "$1") // remove mark on i followed by certain vowels
                .replaceAll("(?i)(?<=" + VOWEL + DOT_BELOW + TONE + MARK + ")(i)" + TONE + "\\b", "$1") // // remove mark on i preceeded by vowels
                ;

        return Normalizer.normalize(nfdText, Normalizer.Form.NFC);
    }
}

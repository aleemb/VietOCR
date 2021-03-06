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

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class TextUtilities {
    /**
     * Corrects letter cases.
     *
     * @param input
     * @return
     */
    public static String correctLetterCases(String input) {
        StringBuffer strB = new StringBuffer();

        // lower uppercase letters ended by lowercase letters except the first letter
        Matcher matcher = Pattern.compile("(?<=\\p{L}+)(\\p{Lu}+)(?=\\p{Ll}+)").matcher(input);
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

        return strB.toString();
    }

    /**
     * Corrects common Tesseract OCR errors.
     *
     * @param input
     * @return
     */
     public static String correctOCRErrors(String input) {
        // substitute letters frequently misrecognized by Tesseract 2.03
        return input.replaceAll("\\b1(?=\\p{L}+\\b)", "l") // 1 to l
                .replaceAll("\\b11(?=\\p{L}+\\b)", "n") // 11 to n
                .replaceAll("\\bI(?=\\p{Ll}+\\b)", "l") // I to l
                .replaceAll("(?<=\\b\\p{L}*)0(?=\\p{L}*\\b)", "o") // 0 to o
//                .replaceAll("(?<!\\.) S(?=\\p{L}*\\b)", " s") // S to s
//                .replaceAll("(?<![cn])h\\b", "n")
                ;
     }
}

/**
 * Copyright @ 2011 Quan Nguyen
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
using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace VietOCR.NET
{
    class OCRImages : OCR<Image>
    {
        /// <summary>
        /// Recognize text
        /// </summary>
        /// <param name="images"></param>
        /// <param name="index"></param>
        /// <param name="lang"></param>
        /// <returns></returns>
        [System.Diagnostics.DebuggerNonUserCodeAttribute()]
        public override string RecognizeText(IList<Image> images, string lang)
        {
            // To be implemented when a working DLL becomes available.

            //tessnet3.Tesseract ocr = new tessnet3.Tesseract();

            //ocr.Init(null, lang, 3);

            //StringBuilder strB = new StringBuilder();

            //foreach (Image image in images)
            //{
            //    string result = ocr.DoOCR(image, rect);

            //    if (result == null) return String.Empty;
            //    strB.Append(result);

            //}
            //return strB.ToString();
            return null;
        }
    }
}

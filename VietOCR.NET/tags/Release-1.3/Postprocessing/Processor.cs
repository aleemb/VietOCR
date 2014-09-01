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
    using System.IO;
    using System.Collections.Generic;

    /**
     *
     * @author Quan Nguyen (nguyenq@users.sf.net)
     */
    public class Processor
    {

        public static string PostProcess(string text, string langCode)
        {
            try
            {
                IPostProcessor processor = ProcessorFactory.createProcessor((ISO639)Enum.Parse(typeof(ISO639), langCode));
                return processor.PostProcess(text);
            }
            catch
            {
                return text;
            }
        }

        public static string PostProcess(string text, string langCode, string dangAmbigsPath, bool dangAmbigsOn)
        {
            // postprocessor
            StringBuilder strB = new StringBuilder(PostProcess(text, langCode));

            if (!dangAmbigsOn)
            {
                return strB.ToString();
            }

            // replace text based on entries read from a DangAmbigs.txt file
            Dictionary<string, string> replaceRules = TextUtilities.LoadMap(Path.Combine(dangAmbigsPath, langCode + ".DangAmbigs.txt"));
            Dictionary<string, string>.KeyCollection.Enumerator enumer = replaceRules.Keys.GetEnumerator();

            while (enumer.MoveNext())
            {
                string key = enumer.Current;
                string value = replaceRules[key];
                strB = strB.Replace(key, value);
            }
            return strB.ToString();
        }
    }
}
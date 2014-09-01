namespace net.sourceforge.vietocr.postprocessing
{
    using System;

    /**
     *
     * @author Quan Nguyen (nguyenq@users.sf.net)
     */
    public class Processor
    {
        
        public static string PostProcess(string text, string langCode)
        {
            IPostProcessor processor = ProcessorFactory.createProcessor((ISO639) Enum.Parse(typeof(ISO639), langCode));
            return processor.PostProcess(text);
        }
    }
}
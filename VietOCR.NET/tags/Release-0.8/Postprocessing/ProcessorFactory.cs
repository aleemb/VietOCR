namespace net.sourceforge.vietocr.postprocessing
{
    using System;

    public enum ISO639
    {
        eng, deu, fra, ita, nld, por, spa, vie
    }    
    
    /**
     *
     * @author Quan Nguyen (nguyenq@users.sf.net)
     */

    public class ProcessorFactory
    {
        // ISO369-3 codes: http://www.sil.org/iso639-3/codes.asp


        public static IPostProcessor createProcessor(ISO639 code)
        {
            IPostProcessor processor;

            switch (code)
            {
                //            case eng:
                //                processor = new EngPP();
                //                break;
                case ISO639.vie:
                    processor = new ViePP();
                    break;
                default:
                    throw new Exception(code.ToString());
            }

            return processor;
        }
    }
}

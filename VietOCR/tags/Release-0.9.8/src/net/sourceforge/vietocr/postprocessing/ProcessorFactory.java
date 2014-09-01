/*
 * ProcessorFactory.java
 *
 * Created on April 10, 2008, 5:35 PM
 */

package net.sourceforge.vietocr.postprocessing;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */

public class ProcessorFactory {
    
     public static IPostProcessor createProcessor(ISO639 code) {
        IPostProcessor processor;
        
        switch (code) {
//            case eng:
//                processor = new EngPP();
//                break;
            case vie:
                processor = new ViePP();
                break;
            default:
                throw new UnsupportedOperationException(code.toString());
        }
        
        return processor;
    }
}

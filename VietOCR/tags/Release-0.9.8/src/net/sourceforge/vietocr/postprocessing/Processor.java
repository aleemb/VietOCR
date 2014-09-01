/*
 * Processor.java
 *
 * Created on April 10, 2008, 5:58 PM
 */

package net.sourceforge.vietocr.postprocessing;

/**
 *
 * @author Quan Nguyen (nguyenq@users.sf.net)
 */
public class Processor {
    public static String postProcess(String text, String langCode)
    {
        IPostProcessor processor = ProcessorFactory.createProcessor(ISO639.valueOf(langCode));
        return processor.postProcess(text);
    }
}

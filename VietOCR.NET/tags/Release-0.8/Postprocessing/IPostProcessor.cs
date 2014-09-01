/*
 * IPostProcessor.java
 *
 * Created on April 10, 2008, 5:31 PM
 */

namespace net.sourceforge.vietocr.postprocessing
{

    /**
     *
     * @author Quan Nguyen (nguyenq@users.sf.net)
     */
    public interface IPostProcessor
    {
        string PostProcess(string text);
    }
}
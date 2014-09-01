System requirements:

- Java Runtime Environment 6.0 - http://java.sun.com/javase/downloads/index.jsp

If you encounter out-of-memory exception, run ocr.bat instead of using the .jar.

Nếu bạn gặp out-of-memory exception, hãy chạy ocr.bat thay vì sử dụng .jar.

Check http://vietunicode.sourceforge.net/howto/tesseract-ocr.html for more information.

If there are problems setting Tesseract path on Linux, please consult this post: http://groups.google.com/group/tesseract-ocr/browse_thread/thread/58fcfab8ef3ae7c1#36d3c5e059332666

On Linux, make install Tesseract will place the resultant executable binary and the language data in different directories, /usr/local/bin and /usr/local/share/tessdata, respectively. VietOCR has assumed that they were in the same directory; therefore, for it to work properly, you'll need to make a soft link to tessdata in /usr/local/bin, the same directory as the tesseract executable is in, as follows:

ln -s /usr/local/share/tessdata /usr/local/bin/tessdata

In addition, you can also let VietOCR know the location of tessdata via the environment variable TESSDATA_PREFIX.

export TESSDATA_PREFIX=/usr/local/share/

The Windows Image Acquisition Library v2.0 require Windows XP Service Pack 1 (SP1) or later. To install the WIA Library, copy the wiaaut.dll file to your System32 directory (usually located at C:\Windows\System32) and run from the command line:

regsvr32 C:\Windows\System32\wiaaut.dll
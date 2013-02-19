package geoscript.style.io

import geoscript.style.Style
import org.geotools.styling.SLDTransformer

/**
 * Write a Style to an SLD document
 * <p><blockquote><pre>
 * def sym = new Fill("#ff00FF") + new Stroke("#ffff00", 0.25")
 * def writer = new SLDWriter()
 * writer.write(sym)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class SLDWriter implements Writer {

    /**
     * Whether to format the SLD or not
     */
    boolean format = true
   
    /**
     * Write the Style to the OutputStream
     * @param The Style
     * @param The OutputStream
     */
    void write(Style style, OutputStream out) {
        def transformer = new SLDTransformer()
        if (format) {
            transformer.indentation = 2
        }
        transformer.transform(style.gtStyle, out)
    }

    /**
     * Write the Style to the File
     * @param style The Style
     * @param file The File
     */
    void write(Style style, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(style, out)
        out.close()
    }

    /**
     * Write the Style to a String
     * @param The Style
     * @return A String
     */
    String write(Style style) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(style, out);
        out.close()
        return out.toString()
    }
}
package geoscript.style.io

import geoscript.style.Style
import org.geotools.ysld.Ysld

/**
 * Write a Style to an SLD document
 * <p><blockquote><pre>
 * def sym = new Fill("#ff00FF") + new Stroke("#ffff00", 0.25")
 * def writer = new YSLDWriter()
 * writer.write(sym)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class YSLDWriter implements Writer {

    /**
     * Write the Style to the OutputStream
     * @param style The Style
     * @param out The OutputStream
     */
    @Override
    void write(Style style, OutputStream out) {
        Ysld.encode(style.gtStyle, out)
    }

    /**
     * Write the Style to the File
     * @param style The Style
     * @param file The File
     */
    @Override
    void write(Style style, File file) {
        Ysld.encode(style.gtStyle, file)
    }

    /**
     * Write the Style to a String
     * @param style The Style
     * @return A String
     */
    @Override
    String write(Style style) {
        StringWriter out = new StringWriter()
        Ysld.encode(style.gtStyle, out)
        out.toString()
    }
}

package geoscript.style.io

import geoscript.style.Style

/**
 * Write a GeoScript Style to an InputStream, File, or String.
 * @author Jared Erickson
 */
interface Writer {
    
    /**
     * Write the Style to the OutputStream
     * @param style The Style
     * @param out The OutputStream
     */
    void write(Style style, OutputStream out)

    /**
     * Write the Style to the File
     * @param style The Style
     * @param file The File
     */
    void write(Style style, File file)

    /**
     * Write the Style to a String
     * @param style The Style
     * @return A String
     */
    String write(Style style)
    
}


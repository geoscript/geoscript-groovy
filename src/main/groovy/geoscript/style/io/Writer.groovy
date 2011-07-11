package geoscript.style.io

import geoscript.style.Style

/**
 * A Style Writer
 * @author Jared Erickson
 */
interface Writer {
    
    /**
     * Write the Style to the OutputStream
     * @param The Style
     * @param The OutputStream
     */
    void write(Style style, OutputStream out);
    
    /**
     * Write the Style to a String
     * @param The Style
     * @return A String
     */
    String write(Style style);
    
}


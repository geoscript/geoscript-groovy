package geoscript.style.io

import geoscript.style.Style

/**
 * Read a GeoScript Style from an InputStream, File, or String 
 * @author Jared Erickson
 */
interface Reader {
	
    /**
     * Read a GeoScript Style from an InputStream
     * @param input An InputStream
     * @return A GeoScript Style
     */
    Style read(InputStream input)

    /**
     * Read a GeoScript Style from a File
     * @param file A File
     * @return A GeoScript Style
     */
    Style read(File file)
	
    /**
     * Read a GeoScript Style from a String
     * @param str A String
     * @return A GeoScript Style
     */
    Style read(String str)
    
}


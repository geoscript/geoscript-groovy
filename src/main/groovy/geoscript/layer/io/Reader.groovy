package geoscript.layer.io

import geoscript.layer.Layer

/**
 * Read a GeoScript Layer from an InputStream, File, or String.
 * @author Jared Erickson
 */
interface Reader {

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(InputStream input)

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(File file)

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(String str)

}

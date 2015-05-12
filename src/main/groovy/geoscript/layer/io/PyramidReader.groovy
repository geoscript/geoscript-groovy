package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * Read a Pyramid from a String
 * @author Jared Erickson
 */
interface PyramidReader {

    /**
     * Read a Pyramid from a String
     * @param str A String
     * @return A Pyramid
     */
    Pyramid read(String str)

}

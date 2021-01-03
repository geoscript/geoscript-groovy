package geoscript.carto.io

import geoscript.carto.CartoBuilder

/**
 * Read a CartoBuilder from a String
 * @author Jared Erickson
 */
interface CartoReader {

    /**
     * Get the name of the CartoReader
     * @return The name
     */
    String getName()

    /**
     * Read a CartoBuilder from a String
     * @param str A String
     * @return A CartoBuilder
     */
    CartoBuilder read(String str)

}
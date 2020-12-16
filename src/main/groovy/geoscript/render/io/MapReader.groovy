package geoscript.render.io

import geoscript.render.Map as GMap

/**
 * Read a Map from a configuration string.
 * @author Jared Erickson
 */
interface MapReader {

    /**
     * Read a Map from a configuration string.
     * @param str The string
     * @return A Map
     */
    GMap read(String str)
}
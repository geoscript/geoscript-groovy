package geoscript.feature.io

import geoscript.feature.Feature

/**
 * Read a Feature from a String.
 * @author Jared Erickson
 */
interface Reader {

    /**
     * Read a Feature from a String.
     * @param str The String
     * @return A Feature
     */
    Feature read(String str)

}

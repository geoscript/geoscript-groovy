package geoscript.feature.io

import geoscript.feature.Schema

/**
 * Read a Schema from a String
 * @author Jared Erickson
 */
interface SchemaReader {

    /**
     * Read a Schema from a String
     * @param str The String
     * @return A Schema
     */
    Schema read(String str)

}

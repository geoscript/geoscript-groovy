package geoscript.feature.io

import geoscript.feature.Schema

/**
 * Write a Schema to a String
 * @author Jared Erickson
 */
interface SchemaWriter {

    /**
     * Write a Schema to a String
     * @param schema A Schema
     * @return A String
     */
    String write(Schema schema)

}

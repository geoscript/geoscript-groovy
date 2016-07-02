package geoscript.feature.io

import geoscript.feature.Schema

/**
 * Write a Schema to a simple String.  This implementation uses the GeoTools
 * DataUtilities.encodeType method.
 * @author Jared Erickson
 */
class StringSchemaWriter implements SchemaWriter {

    @Override
    String write(Schema schema) {
        schema.spec
    }

}

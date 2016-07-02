package geoscript.feature.io

import geoscript.feature.Field
import geoscript.feature.Schema
import groovy.json.JsonSlurper

/**
 * Read a Schema from a JSON String.
 * @author Jared Erickson
 */
class JsonSchemaReader implements SchemaReader {

    @Override
    Schema read(String str) {
        JsonSlurper json = new JsonSlurper()
        Object obj = json.parseText(str)
        String name = obj.name
        List fields = obj.fields.collect { Map fld ->
            String fieldName = fld.name
            String fieldType = fld.type
            if (fld.containsKey('geometry')) {
                String fieldProjection = fld.projection
                new Field(fieldName, fieldType, fieldProjection)
            } else {
                new Field(fieldName, fieldType)
            }
        }
        new Schema(name, fields)
    }

}

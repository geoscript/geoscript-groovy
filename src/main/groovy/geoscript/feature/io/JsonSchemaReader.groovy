package geoscript.feature.io

import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.proj.Projection
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
            Field field = new Field(fieldName, fieldType)
            if (fld.containsKey('projection')) {
                String fieldProjection = fld.projection
                field.proj = new Projection(fieldProjection)
            }
            field
        }
        new Schema(name, fields)
    }

}

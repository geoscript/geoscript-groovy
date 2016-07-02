package geoscript.feature.io

import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.layer.Layer
import groovy.json.JsonBuilder
import groovy.json.JsonOutput

/**
 * Write a Schema to a JSON String.
 * @author Jared Erickson
 */
class JsonSchemaWriter implements SchemaWriter {

    @Override
    String write(Schema schema) {
        Map json = [
            name: schema.name,
            projection: schema.proj.id,
            geometry: schema.geom.name,
            fields: schema.fields.collect { Field fld ->
                Map fldMap = [
                    name: fld.name,
                    type: fld.typ
                ]
                if (fld.isGeometry()) {
                    fldMap.geometry = true
                    fldMap.projection = fld.proj.id
                }
                fldMap
            }
        ]
        JsonOutput.prettyPrint(JsonOutput.toJson(json))
    }

}

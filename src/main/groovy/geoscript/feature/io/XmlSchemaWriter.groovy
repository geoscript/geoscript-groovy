package geoscript.feature.io

import geoscript.feature.Field
import geoscript.feature.Schema
import groovy.xml.MarkupBuilder

/**
 * Write a Schema to an XML String.
 * @author Jared Erickson
 */
class XmlSchemaWriter implements SchemaWriter {

    @Override
    String write(Schema schema) {
        StringWriter writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.schema {
            name(schema.name)
            projection(schema.proj.id)
            geometry(schema.geom.name)
            fields {
                schema.fields.each { Field f ->
                    field {
                        name(f.name)
                        type(f.typ)
                        if (f.isGeometry()) {
                            projection(f.proj.id)
                        }
                    }
                }
            }
        }
        writer.toString()
    }

}

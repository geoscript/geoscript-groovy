package geoscript.feature.io

import geoscript.feature.Field
import geoscript.feature.Schema
import groovy.xml.XmlSlurper

/**
 * Read a Schema from an XML String.
 * @author Jared Erickson
 */
class XmlSchemaReader implements SchemaReader {

    @Override
    Schema read(String str) {
        XmlSlurper xml = new XmlSlurper()
        Object root = xml.parseText(str)
        String name = root.name.text()
        List fields = root.fields.children().collect { Object fld ->
            String fieldName = fld.name.text()
            String fieldType = fld.type.text()
            String fieldProjection = fld.projection?.text().trim() ?: null
            new Field(fieldName, fieldType, fieldProjection)
        }
        new Schema(name, fields)
    }

}

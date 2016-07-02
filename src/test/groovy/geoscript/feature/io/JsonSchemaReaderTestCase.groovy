package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.Test

import static org.junit.Assert.*

/**
 * The JsonSchemaReader Unit Test
 * @author Jared Erickson
 */
class JsonSchemaReaderTestCase {

    @Test void read() {
        String str = """{
    "name": "points",
    "projection": "EPSG:4326",
    "geometry": "geom",
    "fields": [
        {
            "name": "geom",
            "type": "Point",
            "geometry": true,
            "projection": "EPSG:4326"
        },
        {
            "name": "name",
            "type": "String"
        },
        {
            "name": "price",
            "type": "Float"
        }
    ]
}"""
        SchemaReader reader = new JsonSchemaReader()
        Schema schema = reader.read(str)
        assertEquals "points", schema.name
        assertEquals 3, schema.fields.size()
        assertEquals "geom", schema.fields[0].name
        assertEquals "Point", schema.fields[0].typ
        assertEquals "EPSG:4326", schema.fields[0].proj.id
        assertEquals "name", schema.fields[1].name
        assertEquals "String", schema.fields[1].typ
        assertNull schema.fields[1].proj
        assertEquals "price", schema.fields[2].name
        assertEquals "Float", schema.fields[2].typ
        assertNull schema.fields[2].proj
    }

}

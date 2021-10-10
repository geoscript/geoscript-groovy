package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * The JsonSchemaWriter Unit Test
 * @author Jared Erickson
 */
class JsonSchemaWriterTest {

    @Test void write() {
        Schema schema = new Schema("points", "geom:Point:srid=4326,name:String,price:float")
        SchemaWriter writer = new JsonSchemaWriter()
        String str = writer.write(schema)
        assertEquals """{
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
}""", str
    }

}

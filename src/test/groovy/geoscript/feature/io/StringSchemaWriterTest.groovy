package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The StringSchemaWriter Unit Test
 * @author Jared Erickson
 */
class StringSchemaWriterTest {

    @Test void write() {
        Schema schema = new Schema("points", "geom:Point:srid=4326,name:String,price:float")
        SchemaWriter writer = new StringSchemaWriter()
        String str = writer.write(schema)
        assertEquals "geom:Point:srid=4326,name:String,price:Float", str
    }

}

package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.Test
import static org.junit.Assert.*

/**
 * The StringSchemaWriter Unit Test
 * @author Jared Erickson
 */
class StringSchemaWriterTestCase {

    @Test void write() {
        Schema schema = new Schema("points", "geom:Point:srid=4326,name:String,price:float")
        SchemaWriter writer = new StringSchemaWriter()
        String str = writer.write(schema)
        assertEquals "geom:Point:srid=4326,name:String,price:Float", str
    }

}

package geoscript.feature.io

import geoscript.feature.Schema
import org.junit.Test
import static org.junit.Assert.*

/**
 * The StringSchemaReader Unit Test
 * @author Jared Erickson
 */
class StringSchemaReaderTestCase {

    @Test void readWithDefaults() {
        String str = "geom:Point:srid=4326,name:String,price:float"
        SchemaReader reader = new StringSchemaReader()
        Schema schema = reader.read(str)
        assertEquals "layer", schema.name
        assertEquals "http://geoscript.org/feature", schema.uri
        assertEquals schema.field("geom").typ, 'Point'
        assertEquals schema.field("geom").proj.id, 'EPSG:4326'
        assertEquals schema.field("name").typ, 'String'
        assertEquals schema.field("price").typ, 'Float'
    }

    @Test void readWithOptions() {
        String str = "geom:Point:srid=4326,name:String,price:float"
        SchemaReader reader = new StringSchemaReader()
        Schema schema = reader.read(name: 'points', uri: 'https://data.org', str)
        assertEquals "points", schema.name
        assertEquals "https://data.org", schema.uri
        assertEquals schema.field("geom").typ, 'Point'
        assertEquals schema.field("geom").proj.id, 'EPSG:4326'
        assertEquals schema.field("name").typ, 'String'
        assertEquals schema.field("price").typ, 'Float'
    }
}

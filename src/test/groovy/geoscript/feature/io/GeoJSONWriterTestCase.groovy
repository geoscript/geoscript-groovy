package geoscript.feature.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The GeoJSONWriter UnitTest
 */
class GeoJSONWriterTestCase {


    @Test void write() {

        // The Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Feature from a List of values
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)

        // Write the Feature to a GeoJSON String
        GeoJSONWriter writer = new GeoJSONWriter()
        String expected = """{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"house1"}"""
        String actual = writer.write(feature)
        assertEquals expected, actual
    }

}

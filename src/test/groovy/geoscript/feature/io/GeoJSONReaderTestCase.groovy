package geoscript.feature.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The GeoJSONReader UnitTest
 */
class GeoJSONReaderTestCase {

    @Test void read() {

        // The GeoJSON String
        String geojson = "{\"type\":\"Feature\",\"geometry\":{\"type\":\"Point\",\"coordinates\":[111,-47]},\"properties\":{\"name\":\"House\",\"price\":12.5},\"id\":\"house1\"}"

        // The Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Feature from a List of values
        Feature expected = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)

        // Read a Feature from the GeoJSON String
        GeoJSONReader reader = new GeoJSONReader()
        Feature actual = reader.read(geojson)

        // Make sure the expected and actual Features are equal
        assertEquals expected.geom.wkt, actual.geom.wkt
        assertEquals expected.get("name"), actual.get("name")
        assertEquals expected.get("price"), actual.get("price"), 0.1
    }

}

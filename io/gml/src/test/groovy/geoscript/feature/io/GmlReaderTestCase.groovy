package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The GmlReader UnitTest
 * @author Jared Erickson
 */
class GmlReaderTestCase {

    @Test void read() {

        // The GML String
        String gml = """<gml:houses fid="house1" xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink">
    <gml:name>House</gml:name>
    <gml:geom>
        <gml:Point>
            <gml:coord>
                <gml:X>111.0</gml:X>
                <gml:Y>-47.0</gml:Y>
            </gml:coord>
        </gml:Point>
    </gml:geom>
    <gml:price>12.5</gml:price>
</gml:houses>
"""

        // The Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Feature from a List of values
        Feature expected = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)

        // Read a Feature from the GML String
        GmlReader reader = new GmlReader()
        Feature actual = reader.read(gml)

        // Make sure the expected and actual Features are equal
        assertEquals expected.geom.wkt, actual.geom.wkt
        assertEquals expected.get("name"), actual.get("name")
        assertEquals expected.get("price"), actual.get("price") as float, 0.1
    }

}

package geoscript.feature.io

import geoscript.feature.Feature
import org.junit.Test

import static org.junit.Assert.*

/**
 * The KmlReader Unit Test
 * @author Jared Erickson
 */
class KmlReaderTestCase {

    @Test
    void write() {
        String kml = """<kml:Placemark xmlns:kml="http://earth.google.com/kml/2.1" id="house1">
<kml:name>House</kml:name>
<kml:Point>
<kml:coordinates>111.0,-47.0</kml:coordinates>
</kml:Point>
</kml:Placemark>"""
        KmlReader reader = new KmlReader()
        Feature f = reader.read(kml)
        assertNotNull f
        assertEquals("House", f["name"])
        assertEquals("POINT (111 -47)", f.geom.wkt)
        assertNull f["description"]
    }

}

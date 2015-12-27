package geoscript.feature

import geoscript.AssertUtil
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class FeatureTestCase {

    @Test void getKml() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        AssertUtil.assertStringsEqual """<kml:Placemark xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:kml="http://earth.google.com/kml/2.1" id="house1">
<kml:name>House</kml:name>
<kml:Point>
<kml:coordinates>111.0,-47.0</kml:coordinates>
</kml:Point>
</kml:Placemark>
""", f1.kml, removeXmlNS: true, trim: true
    }

    @Test void fromKml() {
        Feature f = Feature.fromKml("""<kml:Placemark xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:kml="http://earth.google.com/kml/2.1" id="house1">
<kml:name>House</kml:name>
<kml:Point>
<kml:coordinates>111.0,-47.0</kml:coordinates>
</kml:Point>
</kml:Placemark>
""")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("House", f["name"])
    }
}

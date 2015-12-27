package geoscript.feature

import geoscript.filter.Property
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*
import geoscript.AssertUtil

class FeatureTestCase {

    @Test void getGpx() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        AssertUtil.assertStringsEqual "<wpt lat='-47.0' lon='111.0' xmlns='http://www.topografix.com/GPX/1/1'>" +
                "<name>House</name><desc>House costs \$12.5</desc></wpt>",
                f1.getGpx(name: new Property("name"), description: {Feature f -> "${f['name']} costs \$${f['price']}"})
    }

    @Test void fromGpx() {
        Feature f = Feature.fromGpx("<wpt lat='-47.0' lon='111.0' xmlns='http://www.topografix.com/GPX/1/1'>" +
                "<name>House</name><desc>House costs \$12.5</desc></wpt>")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("House", f["name"])
        assertEquals("House costs \$12.5", f["desc"])
    }
}

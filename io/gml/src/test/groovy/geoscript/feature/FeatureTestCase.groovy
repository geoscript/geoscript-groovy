package geoscript.feature

import geoscript.AssertUtil
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class FeatureTestCase {

    @Test void getGml() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        AssertUtil.assertStringsEqual """<gsf:houses xmlns:gsf="http://geoscript.org/feature" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" fid="house1">
<gml:name>House</gml:name>
<gsf:geom>
<gml:Point>
<gml:coord>
<gml:X>111.0</gml:X>
<gml:Y>-47.0</gml:Y>
</gml:coord>
</gml:Point>
</gsf:geom>
<gsf:price>12.5</gsf:price>
</gsf:houses>
""", f1.gml, removeXmlNS: true, trim: true
    }

    @Test void fromGml() {
        Feature f = Feature.fromGml("""<gsf:houses xmlns:gsf="http://geoscript.org/feature" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:gml="http://www.opengis.net/gml" fid="house1">
<gml:name>House</gml:name>
<gsf:geom>
<gml:Point>
<gml:coord>
<gml:X>111.0</gml:X>
<gml:Y>-47.0</gml:Y>
</gml:coord>
</gml:Point>
</gsf:geom>
<gsf:price>12.5</gsf:price>
</gsf:houses>
""")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("House", f["name"])
        assertEquals(12.5, f["price"] as double, 0.1)
    }

}
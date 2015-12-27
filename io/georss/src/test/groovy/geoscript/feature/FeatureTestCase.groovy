package geoscript.feature

import geoscript.AssertUtil
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*

class FeatureTestCase {

    @Test void getGeoRSS() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        AssertUtil.assertStringsEqual "<entry xmlns:georss='http://www.georss.org/georss' xmlns='http://www.w3.org/2005/Atom'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<georss:point>-47.0 111.0</georss:point>" +
                "</entry>", f1.getGeoRSS(feedType: "atom", geometryType: "simple", itemDate: "12/7/2013"), removeXmlNS: true
    }

    @Test void fromGeoRSS() {
        Feature f = Feature.fromGeoRSS("<entry xmlns:georss='http://www.georss.org/georss' xmlns='http://www.w3.org/2005/Atom'>" +
                "<title>house1</title>" +
                "<summary>[geom:POINT (111 -47), name:House, price:12.5]</summary>" +
                "<updated>12/7/2013</updated>" +
                "<georss:point>-47.0 111.0</georss:point>" +
                "</entry>")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("[geom:POINT (111 -47), name:House, price:12.5]", f["summary"])
        assertEquals("12/7/2013", f["updated"])
        assertEquals("house1", f["title"])
    }

}

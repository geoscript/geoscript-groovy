package geoscript.feature

import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*

class FeatureTestCase {

    @Test void getGeoJSON() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals """{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"house1"}""", f1.geoJSON
    }

    @Test void fromGeoJSON() {
        Feature f = Feature.fromGeoJSON("""{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"house1"}""")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("House", f["name"])
        assertEquals(12.5, f["price"], 0.1)
    }

}

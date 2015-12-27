package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class GeometryTestCase {

    @Test void getKml() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<Point><coordinates>111.0,-47.0</coordinates></Point>", g.kml
    }

    @Test void fromKml() {
        Geometry g = Geometry.fromKml("<Point><coordinates>111.0,-47.0</coordinates></Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void fromString() {
        Geometry g = Geometry.fromString("<Point><coordinates>1,1</coordinates></Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
    }

}

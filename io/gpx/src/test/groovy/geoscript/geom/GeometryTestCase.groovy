package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class GeometryTestCase {

    @Test void fromGpx() {
        Geometry g = Geometry.fromGpx("<wpt lat='2.0' lon='1.0'/>")
        assertEquals "POINT (1 2)", g.toString()
    }

    @Test void getGpx() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<wpt lat='-47.0' lon='111.0'/>", g.gpx
    }

    @Test void fromString() {
        Geometry g = Geometry.fromString("<wpt lat='2.0' lon='1.0'/>")
        assertNotNull g
        assertEquals "POINT (1 2)", g.wkt
    }
}

package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertArrayEquals
import static org.junit.Assert.assertEquals

class GeometryTestCase {

    @Test void getGeobuf() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "10021806320c08001a0880e7ed69ffa6e92c", g.geobuf
        assertArrayEquals([16, 2, 24, 6, 50, 12, 8, 0, 26, 8, -128, -25, -19, 105, -1, -90, -23, 44] as byte[], g.geobufBytes)
    }

    @Test void fromGeobuf() {
        Geometry g = Geometry.fromGeobuf("10021806320c08001a0880e7ed69ffa6e92c")
        assertEquals "POINT (111 -47)", g.wkt
        g = Geometry.fromGeobuf([16, 2, 24, 6, 50, 12, 8, 0, 26, 8, -128, -25, -19, 105, -1, -90, -23, 44] as byte[])
        assertEquals "POINT (111 -47)", g.wkt
    }

    @Test void fromString() {
        Geometry g = Geometry.fromString("10021806320c08001a0880e7ed69ffa6e92c")
        assertEquals "POINT (111 -47)", g.wkt
    }
}

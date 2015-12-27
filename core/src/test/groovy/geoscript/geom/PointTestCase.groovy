package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class PointTestCase {
	
    @Test void constructors() {
        def p1 = new Point(111,-47)
        assertEquals "POINT (111 -47)", p1.wkt

        def p2 = [111,-47] as Point
        assertEquals "POINT (111 -47)", p2.wkt
    }

    @Test void plus() {
        def p1 = new Point(1,2)
        def m1 = p1 + new Point(3,4)
        assertEquals "MULTIPOINT ((1 2), (3 4))", m1.wkt
    }

    @Test void getXY() {
        def p1 = new Point(111,-47)
        assertEquals(111, p1.x, 0.0)
        assertEquals(-47, p1.y, 0.0)
    }

    @Test void getXYZ() {
        def p = new Point(111, -47, 12.34)
        assertEquals(111, p.x, 0.01)
        assertEquals(-47, p.y, 0.01)
        assertEquals(12.34, p.z, 0.01)
    }

    @Test void getAngle() {
        // Default is degrees
        assertEquals(45, new Point(0,0).getAngle(new Point(10,10)), 0.1)
        assertEquals(-45, new Point(0,0).getAngle(new Point(10,-10)), 0.1)
        assertEquals(-135, new Point(0,0).getAngle(new Point(-10,-10)), 0.1)
        assertEquals(135, new Point(0,0).getAngle(new Point(-10,10)), 0.1)
        // Degrees
        assertEquals(45, new Point(0,0).getAngle(new Point(10,10), "degrees"), 0.1)
        assertEquals(-45, new Point(0,0).getAngle(new Point(10,-10), "degrees"), 0.1)
        assertEquals(-135, new Point(0,0).getAngle(new Point(-10,-10), "degrees"), 0.1)
        assertEquals(135, new Point(0,0).getAngle(new Point(-10,10), "degrees"), 0.1)
        // Radians
        assertEquals(0.7853, new Point(0,0).getAngle(new Point(10,10), "radians"), 0.001)
        assertEquals(-0.7853, new Point(0,0).getAngle(new Point(10,-10), "radians"), 0.001)
        assertEquals(-2.3561, new Point(0,0).getAngle(new Point(-10,-10), "radians"), 0.001)
        assertEquals(2.3561, new Point(0,0).getAngle(new Point(-10,10), "radians"), 0.001)
    }

    @Test void getAzimuth() {
        assertEquals(44.75, new Point(0,0).getAzimuth(new Point(10,10)), 0.01)
        assertEquals(135.24, new Point(0,0).getAzimuth(new Point(10,-10)), 0.1)
        assertEquals(-135.24, new Point(0,0).getAzimuth(new Point(-10,-10)), 0.1)
        assertEquals(-44.75, new Point(0,0).getAzimuth(new Point(-10,10)), 0.1)
    }
}
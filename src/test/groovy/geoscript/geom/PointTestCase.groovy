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

}
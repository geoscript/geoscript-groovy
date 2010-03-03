package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class PointTestCase {
	
    @Test void constructors() {
        def p1 = new Point(111,-47)
        assertEquals "POINT (111 -47)", p1.wkt
    }

    @Test void plus() {
        def p1 = new Point(1,2)
        def m1 = p1 + new Point(3,4)
        assertEquals "MULTIPOINT (1 2, 3 4)", m1.wkt
    }

}
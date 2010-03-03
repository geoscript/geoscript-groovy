package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiPointTestCase {
	
    @Test void constructors() {
        def m1 = new MultiPoint([1,2],[3,4])
        assertEquals "MULTIPOINT (1 2, 3 4)", m1.wkt
		
        def m2 = new MultiPoint(new Point(1,2),new Point(3,4))
        assertEquals "MULTIPOINT (1 2, 3 4)", m2.wkt
    }

    @Test void plus() {
        def m1 = new MultiPoint([1,2],[3,4])
        assertEquals "MULTIPOINT (1 2, 3 4)", m1.wkt
        def m2 = m1 + new Point(5,6)
        assertEquals "MULTIPOINT (1 2, 3 4)", m1.wkt
        assertEquals "MULTIPOINT (1 2, 3 4, 5 6)", m2.wkt

        def p1 = new Point(1,2)
        def m3 = p1 + new Point(3,4)
        assertEquals "MULTIPOINT (1 2, 3 4)", m3.wkt
    }
	
}
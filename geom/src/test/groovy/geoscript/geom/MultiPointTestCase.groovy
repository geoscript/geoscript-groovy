package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertEquals

class MultiPointTestCase {
	
    @Test void constructors() {
        def m1 = new MultiPoint([1,2],[3,4])
        assertEquals "MULTIPOINT ((1 2), (3 4))", m1.wkt
		
        def m2 = new MultiPoint(new Point(1,2),new Point(3,4))
        assertEquals "MULTIPOINT ((1 2), (3 4))", m2.wkt

        def pts = [new Point(1,2),new Point(3,4)]
        def m3 = new MultiPoint(pts)
        assertEquals "MULTIPOINT ((1 2), (3 4))", m3.wkt

        def doubles = [[1,2],[3,4]]
        def m4 = new MultiPoint(doubles)
        assertEquals "MULTIPOINT ((1 2), (3 4))", m4.wkt

    }

    @Test void plus() {
        def m1 = new MultiPoint([1,2],[3,4])
        assertEquals "MULTIPOINT ((1 2), (3 4))", m1.wkt
        def m2 = m1 + new Point(5,6)
        assertEquals "MULTIPOINT ((1 2), (3 4))", m1.wkt
        assertEquals "MULTIPOINT ((1 2), (3 4), (5 6))", m2.wkt

        def p1 = new Point(1,2)
        def m3 = p1 + new Point(3,4)
        assertEquals "MULTIPOINT ((1 2), (3 4))", m3.wkt

        def m4 = new MultiPoint([])
        assertEquals "MULTIPOINT EMPTY", m4.wkt
        def m5 = m4 + new Point(1,1)
        assertEquals "MULTIPOINT ((1 1))", m5.wkt
    }
	
}

package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*

/**
 * The LineString unit test
 */
class LineStringTestCase {
	
    @Test void constructors() {
        def l1 = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l1.wkt
		
        def l2 = new LineString([111.0, -47],[123.0, -48],[110.0, -47])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l2.wkt

        def l3 = new LineString([ new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l3.wkt

        def l4 = new LineString(new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47))
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l4.wkt
    }

    @Test void plus() {
        def line = new LineString([1,2],[3,4],[5,6])
        println("Line: ${line.wkt}")
        def multi = line + new LineString([7,8],[9,10],[11,12])
        println("Multi: ${multi.wkt}")
        assertEquals "MULTILINESTRING ((1 2, 3 4, 5 6), (7 8, 9 10, 11 12))", multi.wkt
    }

    @Test void startEndPoint() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertEquals new Point(1,2).wkt, line.startPoint.wkt
        assertEquals new Point(5,6).wkt, line.endPoint.wkt
    }

    @Test void reverse() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertEquals "LINESTRING (1 2, 3 4, 5 6)", line.wkt
        def line2 = line.reverse()
        assertEquals "LINESTRING (5 6, 3 4, 1 2)", line2.wkt
    }

    @Test void isClosed() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertFalse(line.isClosed())
    }

    @Test void isRing() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertFalse(line.isRing())
    }
}
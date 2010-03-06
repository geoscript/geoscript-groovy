package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

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
	
}
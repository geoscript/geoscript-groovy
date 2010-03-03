package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class LineStringTestCase {
	
	@Test void constructors() {
		def l1 = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
		assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l1.wkt()
		
		def l2 = new LineString([111.0, -47],[123.0, -48],[110.0, -47])
		assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l2.wkt()
	}
	
}
package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiPolygonTestCase {

    @Test void constructors() {
        MultiPolygon p1 = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p1.wkt


        MultiPolygon p2 = new MultiPolygon([[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p2.wkt
    }
	
}

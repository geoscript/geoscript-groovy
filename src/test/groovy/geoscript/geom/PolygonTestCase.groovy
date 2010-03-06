package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals
import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing

class PolygonTestCase {
	
    @Test void constructors() {
		
        Polygon p1 = new Polygon(Geometry.factory.createPolygon(new LinearRing([[1,2],[3,4],[5,6],[1,2]]).g,
                [] as JtsLinearRing[]
            ))
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p1.wkt
		
        def p2 = new Polygon([[[1,2],[3,4],[5,6],[1,2]]])
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p2.wkt
		
        def p3 = new Polygon(new LinearRing([[1,2],[3,4],[5,6],[1,2]]))
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p3.wkt
		
        def p4 = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        assertEquals "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", p4.wkt
		
        def p5 = new Polygon([
                [[1,1], [10,1], [10,10], [1,10], [1,1]],
                [[2,2], [4,2], [4,4], [2,4], [2,2]],
                [[5,5], [6,5], [6,6], [5,6], [5,5]]
            ])
        assertEquals "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", p5.wkt
		
        def p6 = new Polygon([1,2],[3,4],[5,6],[1,2])
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p6.wkt
    }
	
}
package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiPolygonTestCase {

    @Test void constructors() {
        MultiPolygon p1 = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p1.wkt

        MultiPolygon p2 = new MultiPolygon([[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p2.wkt

        MultiPolygon p3 = new MultiPolygon([new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8])])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p3.wkt

        MultiPolygon p4 = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p4.wkt
    }

    @Test void plus() {
        def mp1 = new MultiPolygon(new Polygon([1,2],[3,4],[5,6],[1,2]), new Polygon([7,8],[9,10],[11,12],[7,8]))
        def mp2 = mp1 + new Polygon([11,12],[13,14],[15,16],[11,12])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)), ((11 12, 13 14, 15 16, 11 12)))", mp2.wkt
    }

}

package geoscript.geom

import com.vividsolutions.jts.geom.LinearRing as JtsLinearRing
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class PolygonTestCase {
	
    @Test void constructors() {

        // Create a Polygon from a JTS Polygon
        Polygon p1 = new Polygon(Geometry.factory.createPolygon(new LinearRing([[1,2],[3,4],[5,6],[1,2]]).g,
                [] as JtsLinearRing[]
            ))
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p1.wkt

        // Create a Polygon from a List of List of List of Doubles
        def p2 = new Polygon([[[1,2],[3,4],[5,6],[1,2]]])
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p2.wkt

        // Create a Polygon from a LinearRing (no holes)
        def p3 = new Polygon(new LinearRing([[1,2],[3,4],[5,6],[1,2]]))
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p3.wkt

        // Create a Polygon from a LinearRing and a List of LinearRing holes
        def p4 = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        assertEquals "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", p4.wkt

        // Create a Polygon from a List of List of List of Doubles
        def p5 = new Polygon([
                [[1,1], [10,1], [10,10], [1,10], [1,1]],
                [[2,2], [4,2], [4,4], [2,4], [2,2]],
                [[5,5], [6,5], [6,6], [5,6], [5,5]]
            ])
        assertEquals "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", p5.wkt

        // Create a Polygon from a repeated List of Doubles with no holes
        def p6 = new Polygon([1,2],[3,4],[5,6],[1,2])
        assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p6.wkt

        // Create a Polygon from a List of LinearRings
        def p7 = new Polygon(
            [
                new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        assertEquals "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", p4.wkt


    }

    @Test void plus() {
        def p = new Polygon([[[1,2],[3,4],[5,6],[1,2]]])
        def m = p + new Polygon([[[11,12],[13,14],[15,16],[11,12]]])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((11 12, 13 14, 15 16, 11 12)))", m.wkt
    }

    @Test void split() {
        Geometry g = new Bounds(0,0,10,10).geometry
        Geometry split1 = g.split(new LineString([[0,0],[10,10]]))
        assertTrue split1 instanceof MultiPolygon
        assertEquals 2, split1.numGeometries
        Geometry split2 = split1.split(new LineString([[0,10], [10,0]]))
        assertTrue split2 instanceof MultiPolygon
        assertEquals 4, split2.numGeometries
    }
}
package geoscript.geom

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class MultiPolygonTest {

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
        def mp3 = new MultiPolygon([])
        assertEquals "MULTIPOLYGON EMPTY", mp3.wkt
        def mp4 = mp3 + new Polygon([1,2],[3,4],[4,5],[1,2])
        assertEquals "MULTIPOLYGON (((1 2, 3 4, 4 5, 1 2)))", mp4.wkt
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

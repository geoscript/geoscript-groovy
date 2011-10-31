package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The WktReader UnitTest
 * @author Jared Erickson
 */
class WktReaderTestCase {

    @Test void readPoint() {
        WktReader reader = new WktReader()
        String wkt = "POINT (111 -47)"
        Point expected = new Point(111,-47)
        Point actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        WktReader reader = new WktReader()
        String wkt = "LINESTRING (111 -47, 123 -48, 110 -47)"
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLinearRing() {
        WktReader reader = new WktReader()
        String wkt = "LINEARRING (111 -47, 123 -48, 110 -47, 111 -47)"
        LineString expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        LineString actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygon() {
        WktReader reader = new WktReader()
        String wkt ="POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        WktReader reader = new WktReader()
        String wkt = "MULTIPOINT ((111 -47), (110 -46.5))"
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        WktReader reader = new WktReader()
        String wkt = "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))"
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        WktReader reader = new WktReader()
        String wkt = "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))"
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        WktReader reader = new WktReader()
        String wkt = "GEOMETRYCOLLECTION (POINT (100 0), LINESTRING (101 0, 102 1))"
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }
}

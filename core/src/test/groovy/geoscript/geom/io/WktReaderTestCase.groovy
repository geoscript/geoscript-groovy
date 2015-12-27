package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.*
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

    @Test void readMultiPoint2() {
        WktReader reader = new WktReader()
        String wkt = "MULTIPOINT (111 -47, 110 -46.5)"
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

    @Test void readCircularString() {
        WktReader reader = new WktReader()
        String wkt = "CIRCULARSTRING(6.12 10.0, 7.07 7.07, 10.0 0.0)"
        CircularString expected = new CircularString([6.12, 10.0],[7.07, 7.07],[10.0, 0.0])
        CircularString actual = reader.read(wkt)
        assertEquals expected.curvedWkt, actual.curvedWkt
        assertEquals expected.wkt, actual.wkt

        [
                "CIRCULARSTRING (4 1, 7 4, 4 7)",
                "CIRCULARSTRING(1 1, 2 0, 2 0, 1 1, 0 1)"
        ].each {
            assertTrue reader.read(it) instanceof CircularString
        }

    }

    @Test void readCircularRing() {
        WktReader reader = new WktReader()
        String wkt = "CIRCULARSTRING(2.0 1.0, 1.0 2.0, 0.0 1.0, 1.0 0.0, 2.0 1.0)"
        CircularRing expected = new CircularRing([2, 1],[1, 2],[0, 1],[1, 0],[2, 1])
        CircularRing actual = reader.read(wkt)
        assertEquals expected.curvedWkt, actual.curvedWkt
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readCompoundCurve() {
        WktReader reader = new WktReader()
        String wkt = "COMPOUNDCURVE(CIRCULARSTRING(10.0 10.0, 0.0 20.0, -10.0 10.0), (-10.0 10.0, -10.0 0.0, 10.0 0.0, 5.0 5.0))"
        CompoundCurve expected = new CompoundCurve(
                new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]),
                new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [5.0, 5.0])
        )
        CompoundCurve actual = reader.read(wkt)
        assertEquals expected.curvedWkt, actual.curvedWkt
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readCompoundRing() {
        WktReader reader = new WktReader()
        String wkt = "COMPOUNDCURVE(CIRCULARSTRING(10.0 10.0, 0.0 20.0, -10.0 10.0), (-10.0 10.0, -10.0 0.0, 10.0 0.0, 10.0 10.0))"
        CompoundRing expected = new CompoundRing(
                new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]),
                new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [10.0, 10.0])
        )
        CompoundRing actual = reader.read(wkt)
        assertEquals expected.curvedWkt, actual.curvedWkt
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPointWithSrid() {
        WktReader reader = new WktReader()
        String wkt = "SRID=4326;POINT (111 -47)"
        Point expected = new Point(111,-47)
        Point actual = reader.read(wkt)
        assertEquals expected.wkt, actual.wkt
    }
}

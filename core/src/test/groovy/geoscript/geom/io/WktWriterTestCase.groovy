package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The WktReader UnitTest
 * @author Jared Erickson
 */
class WktWriterTestCase {

    @Test void writePoint() {
        WktWriter writer = new WktWriter()
        Point p = new Point(111,-47)
        String expected = "POINT (111 -47)"
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeLineString() {
        WktWriter writer = new WktWriter()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        String expected = "LINESTRING (111 -47, 123 -48, 110 -47)"
        String actual = writer.write(l)
        assertEquals expected, actual
    }

    @Test void writeLinearRing() {
        WktWriter writer = new WktWriter()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        String expected = "LINEARRING (111 -47, 123 -48, 110 -47, 111 -47)"
        String actual = writer.write(l)
        assertEquals expected, actual
    }

    @Test void writePolygon() {
        WktWriter writer = new WktWriter()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = "POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))"
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiPoint() {
        WktWriter writer = new WktWriter()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = "MULTIPOINT ((111 -47), (110 -46.5))"
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        WktWriter writer = new WktWriter()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))"
        String actual = writer.write(m)
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        WktWriter writer = new WktWriter()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected =  "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))"
        String actual = writer.write(mp)
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        WktWriter writer = new WktWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = "GEOMETRYCOLLECTION (POINT (100 0), LINESTRING (101 0, 102 1))"
        String actual = writer.write(gc)
        assertEquals expected, actual
    }
}

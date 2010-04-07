package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The GeoJSONWriter Unit Test
 * @author Jared Erickson
 */
class GeoJSONWriterTestCase {

    @Test void writePoint() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Point p = new Point(111,-47)
        assertEquals """{ "type": "Point", "coordinates": [111.0, -47.0] }""", writer.write(p)
    }

    @Test void writeLineString() {
        GeoJSONWriter writer = new GeoJSONWriter()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals """{ "type": "LineString", "coordinates": [[111.0, -47.0], [123.0, -48.0], [110.0, -47.0]] }""", writer.write(l)
    }

    @Test void writeLinearRing() {
        GeoJSONWriter writer = new GeoJSONWriter()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        assertEquals """{ "type": "Polygon", "coordinates": [[111.0, -47.0], [123.0, -48.0], [110.0, -47.0], [111.0, -47.0]] }""", writer.write(l)
    }

    @Test void writePolygon() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = """{ "type": "Polygon", "coordinates": [[[1.0, 1.0], [10.0, 1.0], [10.0, 10.0], [1.0, 10.0], [1.0, 1.0]], [[2.0, 2.0], [4.0, 2.0], [4.0, 4.0], [2.0, 4.0], [2.0, 2.0]], [[5.0, 5.0], [6.0, 5.0], [6.0, 6.0], [5.0, 6.0], [5.0, 5.0]]] }"""
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiPoint() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = """{ "type": "MultiPoint", "coordinates": [[111.0, -47.0], [110.0, -46.5]] }"""
        String actual = writer.write(p)
        println("Expected: ${expected}")
        println("Actual  : ${actual}")
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = """{ "type": "MultiLineString", "coordinates": [[[1.0, 2.0], [3.0, 4.0]], [[5.0, 6.0], [7.0, 8.0]]] }"""
        String actual = writer.write(m)
        println("Expected: ${expected}")
        println("Actual  : ${actual}")
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected = """{ "type": "MultiPolygon", "coordinates": [[[[1.0, 2.0], [3.0, 4.0], [5.0, 6.0], [1.0, 2.0]]], [[[7.0, 8.0], [9.0, 10.0], [11.0, 12.0], [7.0, 8.0]]]] }"""
        String actual = writer.write(mp)
        println("Expected: ${expected}")
        println("Actual  : ${actual}")
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        GeoJSONWriter writer = new GeoJSONWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = """{ "type": "GeometryCollection", "geometries": [{ "type": "Point", "coordinates": [100.0, 0.0] }, { "type": "LineString", "coordinates": [[101.0, 0.0], [102.0, 1.0]] }] }"""
        String actual = writer.write(gc)
        println("Expected: ${expected}")
        println("Actual  : ${actual}")
        assertEquals expected, actual
    }

}


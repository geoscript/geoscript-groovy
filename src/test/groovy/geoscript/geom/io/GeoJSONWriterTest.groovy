package geoscript.geom.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals
import geoscript.geom.*

/**
 * The GeoJSONWriter Unit Test
 * @author Jared Erickson
 */
class GeoJSONWriterTest {

    @Test void writePointPrettyPrint() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Point p = new Point(111.1,-47.2)
        assertEquals """{
    "type": "Point",
    "coordinates": [
        111.1,
        -47.2
    ]
}""", writer.write(p, prettyPrint: true)
        assertEquals """{"type":"Point","coordinates":[111.1,-47.2]}""", writer.write(p, prettyPrint: false)
    }

    @Test void writePoint() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Point p = new Point(111.1,-47.2)
        assertEquals """{"type":"Point","coordinates":[111.1,-47.2]}""", writer.write(p)
    }

    @Test void writePointWithDecimals() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Point p = new Point(111.123456,-47.234567)
        assertEquals """{"type":"Point","coordinates":[111.123456,-47.234567]}""", writer.write(p, decimals: 6)
    }

    @Test void writeLineString() {
        GeoJSONWriter writer = new GeoJSONWriter()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals """{"type":"LineString","coordinates":[[111,-47],[123,-48],[110,-47]]}""", writer.write(l)
    }

    @Test void writeLinearRing() {
        GeoJSONWriter writer = new GeoJSONWriter()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        assertEquals """{"type":"LineString","coordinates":[[111,-47],[123,-48],[110,-47],[111,-47]]}""", writer.write(l)
    }

    @Test void writePolygon() {
        GeoJSONWriter writer = new GeoJSONWriter()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = """{"type":"Polygon","coordinates":[[[1,1],[10,1],[10,10],[1,10],[1,1]],[[2,2],[4,2],[4,4],[2,4],[2,2]],[[5,5],[6,5],[6,6],[5,6],[5,5]]]}"""
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiPoint() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = """{"type":"MultiPoint","coordinates":[[111,-47],[110,-46.5]]}"""
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = """{"type":"MultiLineString","coordinates":[[[1,2],[3,4]],[[5,6],[7,8]]]}"""
        String actual = writer.write(m)
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        GeoJSONWriter writer = new GeoJSONWriter()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected = """{"type":"MultiPolygon","coordinates":[[[[1,2],[3,4],[5,6],[1,2]]],[[[7,8],[9,10],[11,12],[7,8]]]]}"""
        String actual = writer.write(mp)
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        GeoJSONWriter writer = new GeoJSONWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = """{"type":"GeometryCollection","geometries":[{"type":"Point","coordinates":[100,0.0]},{"type":"LineString","coordinates":[[101,0.0],[102,1]]}]}"""
        String actual = writer.write(gc)
        assertEquals expected, actual
    }

}


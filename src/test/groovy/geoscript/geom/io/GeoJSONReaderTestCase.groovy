package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The GeoJSONReader Unit Test
 * @author Jared Erickson
 */
class GeoJSONReaderTestCase {

    @Test void readPoint() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "Point", "coordinates": [111.0, -47.0] }"""
        Point expected = new Point(111,-47)
        Point actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "LineString", "coordinates": [[111.0, -47.0], [123.0, -48.0], [110.0, -47.0]] }"""
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygonWithHoles() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "Polygon", "coordinates": [[[1.0, 1.0], [10.0, 1.0], [10.0, 10.0], [1.0, 10.0], [1.0, 1.0]], [[2.0, 2.0], [4.0, 2.0], [4.0, 4.0], [2.0, 4.0], [2.0, 2.0]], [[5.0, 5.0], [6.0, 5.0], [6.0, 6.0], [5.0, 6.0], [5.0, 5.0]]] }"""
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygonWithOutHoles() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "Polygon", "coordinates": [[[1.0, 1.0], [10.0, 1.0], [10.0, 10.0], [1.0, 10.0], [1.0, 1.0]]] }"""
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]))
        Polygon actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "MultiPoint", "coordinates": [[111.0, -47.0], [110.0, -46.5]] }"""
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "MultiLineString", "coordinates": [[[1.0, 2.0], [3.0, 4.0]], [[5.0, 6.0], [7.0, 8.0]]] }"""
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "MultiPolygon", "coordinates": [[[[1.0, 2.0], [3.0, 4.0], [5.0, 6.0], [1.0, 2.0]]], [[[7.0, 8.0], [9.0, 10.0], [11.0, 12.0], [7.0, 8.0]]]] }"""
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        GeoJSONReader reader = new GeoJSONReader()
        String geoJson = """{ "type": "GeometryCollection", "geometries": [{ "type": "Point", "coordinates": [100.0, 0.0] }, { "type": "LineString", "coordinates": [[101.0, 0.0], [102.0, 1.0]] }] }"""
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(geoJson)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }
}



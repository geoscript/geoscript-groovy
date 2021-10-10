package geoscript.geom.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals
import geoscript.geom.*

import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue


/**
 * The KmlReader UnitTest
 * @author Jared Erickson
 */
class KmlReaderTest {

    @Test void readPoint() {
        KmlReader reader = new KmlReader()
        String kml = "<Point><coordinates>111.0,-47.0</coordinates></Point>"
        Point expected = new Point(111,-47)
        Point actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        KmlReader reader = new KmlReader()
        String kml = "<LineString><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</coordinates></LineString>"
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLinearRing() {
        KmlReader reader = new KmlReader()
        String kml = "<LinearRing><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</coordinates></LinearRing>"
        LinearRing expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        LinearRing actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygon() {
        KmlReader reader = new KmlReader()
        String kml = "<Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</coordinates></LinearRing></outerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</coordinates></LinearRing></innerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</coordinates></LinearRing></innerBoundaryIs></Polygon>"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygonNoHoles() {
        KmlReader reader = new KmlReader()
        String kml = "<Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</coordinates></LinearRing></outerBoundaryIs></Polygon>"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]))
        Polygon actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        KmlReader reader = new KmlReader()
        String kml = "<MultiGeometry><Point><coordinates>111.0,-47.0</coordinates></Point><Point><coordinates>110.0,-46.5</coordinates></Point></MultiGeometry>"
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        KmlReader reader = new KmlReader()
        String kml = "<MultiGeometry><LineString><coordinates>1.0,2.0 3.0,4.0</coordinates></LineString><LineString><coordinates>5.0,6.0 7.0,8.0</coordinates></LineString></MultiGeometry>"
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        KmlReader reader = new KmlReader()
        String kml = "<MultiGeometry><Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,2.0 3.0,4.0 5.0,6.0 1.0,2.0</coordinates></LinearRing></outerBoundaryIs></Polygon><Polygon><outerBoundaryIs><LinearRing><coordinates>7.0,8.0 9.0,10.0 11.0,12.0 7.0,8.0</coordinates></LinearRing></outerBoundaryIs></Polygon></MultiGeometry>"
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        KmlReader reader = new KmlReader()
        String kml = "<MultiGeometry><Point><coordinates>100.0,0.0</coordinates></Point><LineString><coordinates>101.0,0.0 102.0,1.0</coordinates></LineString></MultiGeometry>"
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygonWithNamespace() {
        KmlReader reader = new KmlReader()
        String kml = '<kml:Polygon xmlns:kml="http://earth.google.com/kml/2.1"><kml:outerBoundaryIs><kml:LinearRing><kml:coordinates>0.0,0.0 2.0,0.0 2.0,2.0 0.0,2.0 0.0,0.0</kml:coordinates></kml:LinearRing></kml:outerBoundaryIs></kml:Polygon>'
        Polygon expected = new Polygon([0, 0], [2, 0], [2, 2], [0, 2], [0, 0])
        Polygon actual = reader.read(kml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readNode() {
        KmlReader reader = new KmlReader()
        XmlParser parser = new XmlParser(false, false)
        // Point
        String str = "<Point><coordinates>111.0,-47.0</coordinates></Point>"
        Geometry geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("POINT (111 -47)", geom.wkt)
        // Point with namespace
        str = "<kml:Point><kml:coordinates>111.0,-47.0</kml:coordinates></kml:Point>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("POINT (111 -47)", geom.wkt)
        // LineString
        str = "<LineString><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</coordinates></LineString>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("LINESTRING (111 -47, 123 -48, 110 -47)", geom.wkt)
        // LinearRing
        str = "<LinearRing><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</coordinates></LinearRing>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("LINEARRING (111 -47, 123 -48, 110 -47, 111 -47)", geom.wkt)
        // Polygon
        str = "<Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</coordinates></LinearRing></outerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</coordinates></LinearRing></innerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</coordinates></LinearRing></innerBoundaryIs></Polygon>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", geom.wkt)
        // Polygon no wholes
        str = "<Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</coordinates></LinearRing></outerBoundaryIs></Polygon>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1))", geom.wkt)
        // Polygon with namespace
        str = '<kml:Polygon xmlns:kml="http://earth.google.com/kml/2.1"><kml:outerBoundaryIs><kml:LinearRing><kml:coordinates>0.0,0.0 2.0,0.0 2.0,2.0 0.0,2.0 0.0,0.0</kml:coordinates></kml:LinearRing></kml:outerBoundaryIs></kml:Polygon>'
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("POLYGON ((0 0, 2 0, 2 2, 0 2, 0 0))", geom.wkt)
        // MultiPoint
        str = "<MultiGeometry><Point><coordinates>111.0,-47.0</coordinates></Point><Point><coordinates>110.0,-46.5</coordinates></Point></MultiGeometry>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("MULTIPOINT ((111 -47), (110 -46.5))", geom.wkt)
        // MultiPoint with namespace
        str = "<kml:MultiGeometry><kml:Point><kml:coordinates>111.0,-47.0</kml:coordinates></kml:Point><kml:Point><kml:coordinates>110.0,-46.5</kml:coordinates></kml:Point></kml:MultiGeometry>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("MULTIPOINT ((111 -47), (110 -46.5))", geom.wkt)
        // MultiLineString
        str = "<MultiGeometry><LineString><coordinates>1.0,2.0 3.0,4.0</coordinates></LineString><LineString><coordinates>5.0,6.0 7.0,8.0</coordinates></LineString></MultiGeometry>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", geom.wkt)
        // GeometryCollection
        str = "<MultiGeometry><Point><coordinates>100.0,0.0</coordinates></Point><LineString><coordinates>101.0,0.0 102.0,1.0</coordinates></LineString></MultiGeometry>"
        geom = reader.read(parser.parse(new StringReader(str)))
        assertEquals("GEOMETRYCOLLECTION (POINT (100 0), LINESTRING (101 0, 102 1))", geom.wkt)
    }
}
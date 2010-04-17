package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*

/**
 * The GML2Reader UnitTest
 * @author Jared Erickson
 */
class Gml2ReaderTestCase {

    @Test void readPoint() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>"
        Point expected = new Point(111,-47)
        Point actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>"
        expected = new Point(111,-47)
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:LineString><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</gml:coordinates></gml:LineString>"
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</gml:coordinates></gml:LineString>"
        expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLinearRing() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:LinearRing><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</gml:coordinates></gml:LinearRing>"
        LinearRing expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        LinearRing actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:LinearRing xmlns:gml=\"http://www.opengis.net/gml\"><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</gml:coordinates></gml:LinearRing>"
        expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygon() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon>"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\"><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon>"
        expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:MultiPoint><gml:pointMember><gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:coordinates>110.0,-46.5</gml:coordinates></gml:Point></gml:pointMember></gml:MultiPoint>"
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml\"><gml:pointMember><gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:coordinates>110.0,-46.5</gml:coordinates></gml:Point></gml:pointMember></gml:MultiPoint>"
        expected = new MultiPoint([111,-47],[110,-46.5])
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = """<gml:MultiLineString><gml:lineStringMember><gml:LineString><gml:coordinates>1.0,2.0 3.0,4.0</gml:coordinates></gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:coordinates>5.0,6.0 7.0,8.0</gml:coordinates></gml:LineString></gml:lineStringMember></gml:MultiLineString>"""
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = """<gml:MultiLineString xmlns:gml=\"http://www.opengis.net/gml\"><gml:lineStringMember><gml:LineString><gml:coordinates>1.0,2.0 3.0,4.0</gml:coordinates></gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:coordinates>5.0,6.0 7.0,8.0</gml:coordinates></gml:LineString></gml:lineStringMember></gml:MultiLineString>"""
        expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:MultiPolygon><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,2.0 3.0,4.0 5.0,6.0 1.0,2.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>7.0,8.0 9.0,10.0 11.0,12.0 7.0,8.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember></gml:MultiPolygon>"
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:MultiPolygon xmlns:gml=\"http://www.opengis.net/gml\"><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,2.0 3.0,4.0 5.0,6.0 1.0,2.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>7.0,8.0 9.0,10.0 11.0,12.0 7.0,8.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember></gml:MultiPolygon>"
        expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        Gml2Reader reader = new Gml2Reader()

        // Without XML Namespace
        String gml = "<gml:GeometryCollection><gml:geometryMember><gml:Point><gml:coordinates>100.0,0.0</gml:coordinates></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:coordinates>101.0,0.0 102.0,1.0</gml:coordinates></gml:LineString></gml:geometryMember></gml:GeometryCollection>"
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:GeometryCollection xmlns:gml=\"http://www.opengis.net/gml\"><gml:geometryMember><gml:Point><gml:coordinates>100.0,0.0</gml:coordinates></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:coordinates>101.0,0.0 102.0,1.0</gml:coordinates></gml:LineString></gml:geometryMember></gml:GeometryCollection>"
        expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        actual = reader.read(gml)
        println("Expected: ${expected.wkt}")
        println("Actual  : ${actual.wkt}")
        assertEquals expected.wkt, actual.wkt
    }
}


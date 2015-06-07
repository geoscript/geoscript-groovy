package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*

/**
 * The GML3Reader UnitTest
 * @author Jared Erickson
 */
class Gml3ReaderTestCase {

    @Test void readPoint() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>"
        Point expected = new Point(111,-47)
        Point actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:Point xmlns:gml=\"http://www.opengis.net/gml\"><gml:pos>111.0 -47.0</gml:pos></gml:Point>"
        expected = new Point(111,-47)
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:LineString><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0</gml:posList></gml:LineString>"
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:LineString xmlns:gml=\"http://www.opengis.net/gml\"><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0</gml:posList></gml:LineString>"
        expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLinearRing() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:LinearRing><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0 111.0 -47.0</gml:posList></gml:LinearRing>"
        LinearRing expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        LinearRing actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:LinearRing xmlns:gml=\"http://www.opengis.net/gml\"><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0 111.0 -47.0</gml:posList></gml:LinearRing>"
        expected = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygon() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>1.0 1.0 10.0 1.0 10.0 10.0 1.0 10.0 1.0 1.0</gml:posList></gml:LinearRing></gml:exterior><gml:interior><gml:LinearRing><gml:posList>2.0 2.0 4.0 2.0 4.0 4.0 2.0 4.0 2.0 2.0</gml:posList></gml:LinearRing></gml:interior><gml:interior><gml:LinearRing><gml:posList>5.0 5.0 6.0 5.0 6.0 6.0 5.0 6.0 5.0 5.0</gml:posList></gml:LinearRing></gml:interior></gml:Polygon>"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:Polygon xmlns:gml=\"http://www.opengis.net/gml\"><gml:exterior><gml:LinearRing><gml:posList>1.0 1.0 10.0 1.0 10.0 10.0 1.0 10.0 1.0 1.0</gml:posList></gml:LinearRing></gml:exterior><gml:interior><gml:LinearRing><gml:posList>2.0 2.0 4.0 2.0 4.0 4.0 2.0 4.0 2.0 2.0</gml:posList></gml:LinearRing></gml:interior><gml:interior><gml:LinearRing><gml:posList>5.0 5.0 6.0 5.0 6.0 6.0 5.0 6.0 5.0 5.0</gml:posList></gml:LinearRing></gml:interior></gml:Polygon>"
        expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:MultiPoint><gml:pointMember><gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:pos>110.0 -46.5</gml:pos></gml:Point></gml:pointMember></gml:MultiPoint>"
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:MultiPoint xmlns:gml=\"http://www.opengis.net/gml\"><gml:pointMember><gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:pos>110.0 -46.5</gml:pos></gml:Point></gml:pointMember></gml:MultiPoint>"
        expected = new MultiPoint([111,-47],[110,-46.5])
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace as Curve
        String gml = """<gml:Curve><gml:segments><gml:LineStringSegment interpolation="linear"><gml:posList>1.0 2.0 3.0 4.0</gml:posList></gml:LineStringSegment><gml:LineStringSegment interpolation="linear"><gml:posList>5.0 6.0 7.0 8.0</gml:posList></gml:LineStringSegment></gml:segments></gml:Curve>"""
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace as Curve
        gml = """<gml:Curve xmlns:gml=\"http://www.opengis.net/gml\"><gml:segments><gml:LineStringSegment interpolation="linear"><gml:posList>1.0 2.0 3.0 4.0</gml:posList></gml:LineStringSegment><gml:LineStringSegment interpolation="linear"><gml:posList>5.0 6.0 7.0 8.0</gml:posList></gml:LineStringSegment></gml:segments></gml:Curve>"""
        expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // Without XML Namespace as MultiCurve
        gml = """<gml:MultiCurve><gml:curveMember><gml:LineString><gml:posList>1.0 2.0 3.0 4.0</gml:posList></gml:LineString></gml:curveMember><gml:curveMember><gml:LineString><gml:posList>5.0 6.0 7.0 8.0</gml:posList></gml:LineString></gml:curveMember></gml:MultiCurve>"""
        expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // Without XML Namespace as MultiCurve
        gml = """<gml:MultiCurve xmlns:gml=\"http://www.opengis.net/gml\"><gml:curveMember><gml:LineString><gml:posList>1.0 2.0 3.0 4.0</gml:posList></gml:LineString></gml:curveMember><gml:curveMember><gml:LineString><gml:posList>5.0 6.0 7.0 8.0</gml:posList></gml:LineString></gml:curveMember></gml:MultiCurve>"""
        expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:MultiSurface><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>1.0 2.0 3.0 4.0 5.0 6.0 1.0 2.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>7.0 8.0 9.0 10.0 11.0 12.0 7.0 8.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember></gml:MultiSurface>"
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:MultiSurface xmlns:gml=\"http://www.opengis.net/gml\"><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>1.0 2.0 3.0 4.0 5.0 6.0 1.0 2.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>7.0 8.0 9.0 10.0 11.0 12.0 7.0 8.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember></gml:MultiSurface>"
        expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        Gml3Reader reader = new Gml3Reader()

        // Without XML Namespace
        String gml = "<gml:MultiGeometry><gml:geometryMember><gml:Point><gml:pos>100.0 0.0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101.0 0.0 102.0 1.0</gml:posList></gml:LineString></gml:geometryMember></gml:MultiGeometry>"
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:MultiGeometry xmlns:gml=\"http://www.opengis.net/gml\"><gml:geometryMember><gml:Point><gml:pos>100.0 0.0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101.0 0.0 102.0 1.0</gml:posList></gml:LineString></gml:geometryMember></gml:MultiGeometry>"
        expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // Without XML Namespace
        gml = "<gml:GeometryCollection><gml:geometryMember><gml:Point><gml:pos>100.0 0.0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101.0 0.0 102.0 1.0</gml:posList></gml:LineString></gml:geometryMember></gml:GeometryCollection>"
        expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt

        // With XML Namespace
        gml = "<gml:GeometryCollection xmlns:gml=\"http://www.opengis.net/gml\"><gml:geometryMember><gml:Point><gml:pos>100.0 0.0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101.0 0.0 102.0 1.0</gml:posList></gml:LineString></gml:geometryMember></gml:GeometryCollection>"
        expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        actual = reader.read(gml)
        assertEquals expected.wkt, actual.wkt
    }
}


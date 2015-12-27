package geoscript.geom.io

import geoscript.geom.*
import org.junit.Test

import static org.junit.Assert.assertEquals

/**
 * The Gml3Writer Unit Test
 * @author Jared Erickson
 */
class Gml3WriterTestCase {

    @Test void writePoint() {
        Gml3Writer writer = new Gml3Writer()
        Point p = new Point(111,-47)
        assertEquals "<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>", writer.write(p)
    }

    @Test void writeLineString() {
        Gml3Writer writer = new Gml3Writer()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals "<gml:LineString><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0</gml:posList></gml:LineString>", writer.write(l)
    }

    @Test void writeLinearRing() {
        Gml3Writer writer = new Gml3Writer()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        assertEquals "<gml:LinearRing><gml:posList>111.0 -47.0 123.0 -48.0 110.0 -47.0 111.0 -47.0</gml:posList></gml:LinearRing>", writer.write(l)
    }

    @Test void writePolygon() {
        Gml3Writer writer = new Gml3Writer()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = "<gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>1.0 1.0 10.0 1.0 10.0 10.0 1.0 10.0 1.0 1.0</gml:posList></gml:LinearRing></gml:exterior><gml:interior><gml:LinearRing><gml:posList>2.0 2.0 4.0 2.0 4.0 4.0 2.0 4.0 2.0 2.0</gml:posList></gml:LinearRing></gml:interior><gml:interior><gml:LinearRing><gml:posList>5.0 5.0 6.0 5.0 6.0 6.0 5.0 6.0 5.0 5.0</gml:posList></gml:LinearRing></gml:interior></gml:Polygon>"
        String actual = writer.write(p)
        assertEquals expected, writer.write(p)
    }

    @Test void writeMultiPoint() {
        Gml3Writer writer = new Gml3Writer()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = "<gml:MultiPoint><gml:pointMember><gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:pos>110.0 -46.5</gml:pos></gml:Point></gml:pointMember></gml:MultiPoint>"
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        Gml3Writer writer = new Gml3Writer()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = """<gml:Curve><gml:segments><gml:LineStringSegment interpolation="linear"><gml:posList>1.0 2.0 3.0 4.0</gml:posList></gml:LineStringSegment><gml:LineStringSegment interpolation="linear"><gml:posList>5.0 6.0 7.0 8.0</gml:posList></gml:LineStringSegment></gml:segments></gml:Curve>"""
        String actual = writer.write(m)
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        Gml3Writer writer = new Gml3Writer()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected = "<gml:MultiSurface><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>1.0 2.0 3.0 4.0 5.0 6.0 1.0 2.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember><gml:surfaceMember><gml:Polygon><gml:exterior><gml:LinearRing><gml:posList>7.0 8.0 9.0 10.0 11.0 12.0 7.0 8.0</gml:posList></gml:LinearRing></gml:exterior></gml:Polygon></gml:surfaceMember></gml:MultiSurface>"
        String actual = writer.write(mp)
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        Gml3Writer writer = new Gml3Writer()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = "<gml:MultiGeometry><gml:geometryMember><gml:Point><gml:pos>100.0 0.0</gml:pos></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:posList>101.0 0.0 102.0 1.0</gml:posList></gml:LineString></gml:geometryMember></gml:MultiGeometry>"
        String actual = writer.write(gc)
        assertEquals expected, actual
    }

    /**
     * Compare GML against GeoTool's awesome GML support
     * @param g A GeoScript Geometry
     */
    private void print(Geometry g) {
        geoscript.layer.Layer layer = new geoscript.layer.Layer()
        layer.add(layer.schema.feature([g]))
        layer.toGML()
    }
}


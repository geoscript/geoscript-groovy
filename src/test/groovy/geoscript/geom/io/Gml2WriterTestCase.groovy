package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The Gml2Writer Unit Test
 * @author Jared Erickson
 */
class Gml2WriterTestCase {

    @Test void writePoint() {
        Gml2Writer writer = new Gml2Writer()
        Point p = new Point(111,-47)
        assertEquals "<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>", writer.write(p)
    }

    @Test void writeLineString() {
        Gml2Writer writer = new Gml2Writer()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals "<gml:LineString><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</gml:coordinates></gml:LineString>", writer.write(l)
    }

    @Test void writeLinearRing() {
        Gml2Writer writer = new Gml2Writer()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        assertEquals "<gml:LinearRing><gml:coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</gml:coordinates></gml:LinearRing>", writer.write(l)
    }

    @Test void writePolygon() {
        Gml2Writer writer = new Gml2Writer()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = "<gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs><gml:innerBoundaryIs><gml:LinearRing><gml:coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</gml:coordinates></gml:LinearRing></gml:innerBoundaryIs></gml:Polygon>"
        String actual = writer.write(p)
        assertEquals expected, writer.write(p)
    }

    @Test void writeMultiPoint() {
        Gml2Writer writer = new Gml2Writer()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = "<gml:MultiPoint><gml:pointMember><gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point></gml:pointMember><gml:pointMember><gml:Point><gml:coordinates>110.0,-46.5</gml:coordinates></gml:Point></gml:pointMember></gml:MultiPoint>"
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        Gml2Writer writer = new Gml2Writer()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = """<gml:MultiLineString><gml:lineStringMember><gml:LineString><gml:coordinates>1.0,2.0 3.0,4.0</gml:coordinates></gml:LineString></gml:lineStringMember><gml:lineStringMember><gml:LineString><gml:coordinates>5.0,6.0 7.0,8.0</gml:coordinates></gml:LineString></gml:lineStringMember></gml:MultiLineString>"""
        String actual = writer.write(m)
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        Gml2Writer writer = new Gml2Writer()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected = "<gml:MultiPolygon><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>1.0,2.0 3.0,4.0 5.0,6.0 1.0,2.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember><gml:polygonMember><gml:Polygon><gml:outerBoundaryIs><gml:LinearRing><gml:coordinates>7.0,8.0 9.0,10.0 11.0,12.0 7.0,8.0</gml:coordinates></gml:LinearRing></gml:outerBoundaryIs></gml:Polygon></gml:polygonMember></gml:MultiPolygon>"
        String actual = writer.write(mp)
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        Gml2Writer writer = new Gml2Writer()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = "<gml:GeometryCollection><gml:geometryMember><gml:Point><gml:coordinates>100.0,0.0</gml:coordinates></gml:Point></gml:geometryMember><gml:geometryMember><gml:LineString><gml:coordinates>101.0,0.0 102.0,1.0</gml:coordinates></gml:LineString></gml:geometryMember></gml:GeometryCollection>"
        String actual = writer.write(gc)
        assertEquals expected, actual
    }

}


package geoscript.geom.io

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*

/**
 * The KmlWriter Unit Test
 * @author Jared Erickson
 */
class KmlWriterTestCase {

    @Test void writePoint() {
        KmlWriter writer = new KmlWriter()
        Point p = new Point(111,-47)
        assertEquals "<Point><coordinates>111.0,-47.0</coordinates></Point>", writer.write(p)
    }

    @Test void writeLineString() {
        KmlWriter writer = new KmlWriter()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals "<LineString><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0</coordinates></LineString>", writer.write(l)
    }

    @Test void writeLinearRing() {
        KmlWriter writer = new KmlWriter()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        assertEquals "<LinearRing><coordinates>111.0,-47.0 123.0,-48.0 110.0,-47.0 111.0,-47.0</coordinates></LinearRing>", writer.write(l)
    }

    @Test void writePolygon() {
        KmlWriter writer = new KmlWriter()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = "<Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,1.0 10.0,1.0 10.0,10.0 1.0,10.0 1.0,1.0</coordinates></LinearRing></outerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>2.0,2.0 4.0,2.0 4.0,4.0 2.0,4.0 2.0,2.0</coordinates></LinearRing></innerBoundaryIs><innerBoundaryIs><LinearRing><coordinates>5.0,5.0 6.0,5.0 6.0,6.0 5.0,6.0 5.0,5.0</coordinates></LinearRing></innerBoundaryIs></Polygon>"
        String actual = writer.write(p)
        assertEquals expected, writer.write(p)
    }

    @Test void writeMultiPoint() {
        KmlWriter writer = new KmlWriter()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        assertEquals "<MultiGeometry><Point><coordinates>111.0,-47.0</coordinates></Point><Point><coordinates>110.0,-46.5</coordinates></Point></MultiGeometry>", writer.write(p)
    }

    @Test void writeMultiLineString() {
        KmlWriter writer = new KmlWriter()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        assertEquals "<MultiGeometry><LineString><coordinates>1.0,2.0 3.0,4.0</coordinates></LineString><LineString><coordinates>5.0,6.0 7.0,8.0</coordinates></LineString></MultiGeometry>", writer.write(m)
    }

    @Test void writeMultiPolygon() {
        KmlWriter writer = new KmlWriter()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        assertEquals "<MultiGeometry><Polygon><outerBoundaryIs><LinearRing><coordinates>1.0,2.0 3.0,4.0 5.0,6.0 1.0,2.0</coordinates></LinearRing></outerBoundaryIs></Polygon><Polygon><outerBoundaryIs><LinearRing><coordinates>7.0,8.0 9.0,10.0 11.0,12.0 7.0,8.0</coordinates></LinearRing></outerBoundaryIs></Polygon></MultiGeometry>", writer.write(mp)
    }

    @Test void writeGeometryCollection() {
        KmlWriter writer = new KmlWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = "<MultiGeometry><Point><coordinates>100.0,0.0</coordinates></Point><LineString><coordinates>101.0,0.0 102.0,1.0</coordinates></LineString></MultiGeometry>"
        String actual = writer.write(gc)
        println("Expected: ${expected}")
        println("Actual  : ${actual}")
        assertEquals expected, actual
    }

}


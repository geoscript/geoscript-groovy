package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*

/**
 * The CircularRing Unit Test
 * @author Jared Erickson
 */
class CircularRingTestCase {

    @Test
    void createWithPoints() {
        CircularRing cr = new CircularRing(
                new Point(2, 1),
                new Point(1, 2),
                new Point(0, 1),
                new Point(1, 0),
                new Point(2, 1)
        )
        assertTrue cr.isClosed()
        assertTrue cr.isCurved()
        assertEquals "CircularRing", cr.geometryType
        assertEquals "CIRCULARSTRING (2.0 1.0, 1.0 2.0, 0.0 1.0, 1.0 0.0, 2.0 1.0)", cr.curvedWkt
        assertTrue cr.wkt.startsWith("LINEARRING")
        List pts = cr.controlPoints
        assertEquals 5, pts.size()
        assertEquals new Point(2, 1), pts[0]
        assertEquals new Point(1, 2), pts[1]
        assertEquals new Point(0, 1), pts[2]
        assertEquals new Point(1, 0), pts[3]
        assertEquals new Point(2, 1), pts[4]
        LinearRing ring = cr.linear
        assertTrue ring.numPoints > 5
    }

    @Test
    void createWithDoubleLists() {
        CircularRing cr = new CircularRing(
                [2, 1],
                [1, 2],
                [0, 1],
                [1, 0],
                [2, 1]
        )
        assertTrue cr.isClosed()
        assertTrue cr.isCurved()
        assertEquals "CircularRing", cr.geometryType
        assertEquals "CIRCULARSTRING (2.0 1.0, 1.0 2.0, 0.0 1.0, 1.0 0.0, 2.0 1.0)", cr.curvedWkt
        assertTrue cr.wkt.startsWith("LINEARRING")
        List pts = cr.controlPoints
        assertEquals 5, pts.size()
        assertEquals new Point(2, 1), pts[0]
        assertEquals new Point(1, 2), pts[1]
        assertEquals new Point(0, 1), pts[2]
        assertEquals new Point(1, 0), pts[3]
        assertEquals new Point(2, 1), pts[4]
        LinearRing ring = cr.linear
        assertTrue ring.numPoints > 5
    }

    @Test
    void createWithListOfDoubleLists() {
        CircularRing cr = new CircularRing([
                [2, 1],
                [1, 2],
                [0, 1],
                [1, 0],
                [2, 1]
        ])
        assertTrue cr.isClosed()
        assertTrue cr.isCurved()
        assertEquals "CircularRing", cr.geometryType
        assertEquals "CIRCULARSTRING (2.0 1.0, 1.0 2.0, 0.0 1.0, 1.0 0.0, 2.0 1.0)", cr.curvedWkt
        assertTrue cr.wkt.startsWith("LINEARRING")
        List pts = cr.controlPoints
        assertEquals 5, pts.size()
        assertEquals new Point(2, 1), pts[0]
        assertEquals new Point(1, 2), pts[1]
        assertEquals new Point(0, 1), pts[2]
        assertEquals new Point(1, 0), pts[3]
        assertEquals new Point(2, 1), pts[4]
        LinearRing ring = cr.linear
        assertTrue ring.numPoints > 5
    }

    @Test
    void createWithListOfPoints() {
        CircularRing cr = new CircularRing([
                new Point(2, 1),
                new Point(1, 2),
                new Point(0, 1),
                new Point(1, 0),
                new Point(2, 1)
        ])
        assertTrue cr.isClosed()
        assertTrue cr.isCurved()
        assertEquals "CircularRing", cr.geometryType
        assertEquals "CIRCULARSTRING (2.0 1.0, 1.0 2.0, 0.0 1.0, 1.0 0.0, 2.0 1.0)", cr.curvedWkt
        assertTrue cr.wkt.startsWith("LINEARRING")
        List pts = cr.controlPoints
        assertEquals 5, pts.size()
        assertEquals new Point(2, 1), pts[0]
        assertEquals new Point(1, 2), pts[1]
        assertEquals new Point(0, 1), pts[2]
        assertEquals new Point(1, 0), pts[3]
        assertEquals new Point(2, 1), pts[4]
        LinearRing ring = cr.linear
        assertTrue ring.numPoints > 5
    }
}

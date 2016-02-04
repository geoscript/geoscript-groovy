package geoscript.geom

import org.junit.Test

import static org.junit.Assert.*

/**
 * The CircularString Unit Test
 * @author Jared Erickson
 */
class CircularStringTestCase {

    @Test
    void createWithPoints() {
        CircularString cs = new CircularString(
                new Point(6.12, 10.0),
                new Point(7.07, 7.07),
                new Point(10.0, 0.0)
        )
        assertFalse cs.isClosed()
        assertTrue cs.isCurved()
        assertEquals "CircularString", cs.geometryType
        assertEquals "CIRCULARSTRING (6.12 10.0, 7.07 7.07, 10.0 0.0)", cs.curvedWkt
        assertTrue cs.wkt.startsWith("LINESTRING")
        List pts = cs.controlPoints
        assertEquals 3, pts.size()
        assertEquals new Point(6.12, 10.0), pts[0]
        assertEquals new Point(7.07, 7.07), pts[1]
        assertEquals new Point(10.0, 0.0), pts[2]
        LineString line = cs.linear
        assertTrue line.numPoints > 3
    }

    @Test
    void createWithDoubleLists() {
        CircularString cs = new CircularString(
                [6.12, 10.0],
                [7.07, 7.07],
                [10.0, 0.0]
        )
        assertFalse cs.isClosed()
        assertTrue cs.isCurved()
        assertEquals "CircularString", cs.geometryType
        assertEquals "CIRCULARSTRING (6.12 10.0, 7.07 7.07, 10.0 0.0)", cs.curvedWkt
        assertTrue cs.wkt.startsWith("LINESTRING")
        List pts = cs.controlPoints
        assertEquals 3, pts.size()
        assertEquals new Point(6.12, 10.0), pts[0]
        assertEquals new Point(7.07, 7.07), pts[1]
        assertEquals new Point(10.0, 0.0), pts[2]
        LineString line = cs.linear
        assertTrue line.numPoints > 3
    }

    @Test
    void createWithListOfDoubleLists() {
        CircularString cs = new CircularString([
                [6.12, 10.0],
                [7.07, 7.07],
                [10.0, 0.0]
        ])
        assertFalse cs.isClosed()
        assertTrue cs.isCurved()
        assertEquals "CircularString", cs.geometryType
        assertEquals "CIRCULARSTRING (6.12 10.0, 7.07 7.07, 10.0 0.0)", cs.curvedWkt
        assertTrue cs.wkt.startsWith("LINESTRING")
        List pts = cs.controlPoints
        assertEquals 3, pts.size()
        assertEquals new Point(6.12, 10.0), pts[0]
        assertEquals new Point(7.07, 7.07), pts[1]
        assertEquals new Point(10.0, 0.0), pts[2]
        LineString line = cs.linear
        assertTrue line.numPoints > 3
    }

    @Test
    void createWithListOfPoints() {
        CircularString cs = new CircularString([
                new Point(6.12, 10.0),
                new Point(7.07, 7.07),
                new Point(10.0, 0.0)
        ])
        assertFalse cs.isClosed()
        assertTrue cs.isCurved()
        assertEquals "CircularString", cs.geometryType
        assertEquals "CIRCULARSTRING (6.12 10.0, 7.07 7.07, 10.0 0.0)", cs.curvedWkt
        assertTrue cs.wkt.startsWith("LINESTRING")
        List pts = cs.controlPoints
        assertEquals 3, pts.size()
        assertEquals new Point(6.12, 10.0), pts[0]
        assertEquals new Point(7.07, 7.07), pts[1]
        assertEquals new Point(10.0, 0.0), pts[2]
        cs = Geometry.fromWKT("CIRCULARSTRING (4 1, 7 4, 4 7)")
        LineString line = cs.linear
        assertTrue line.numPoints > 3
    }
}

package geoscript.geom

import org.junit.Test

import static org.junit.Assert.*

/**
 * The CompoundCurve Unit Test
 * @author Jared Erickson
 */
class CompoundCurveTestCase {

    @Test void createWithRepeatedLineStrings() {
        CompoundCurve cc = new CompoundCurve(
                new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]),
                new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [5.0, 5.0])
        )
        assertFalse cc.isClosed()
        assertTrue cc.isCurved()
        assertEquals "CompoundCurve", cc.geometryType
        assertEquals "COMPOUNDCURVE (CIRCULARSTRING (10.0 10.0, 0.0 20.0, -10.0 10.0), (-10.0 10.0, -10.0 0.0, 10.0 0.0, 5.0 5.0))", cc.curvedWkt
        assertTrue cc.wkt.startsWith("LINESTRING")
        List lines = cc.components
        assertEquals 2, lines.size()
        assertEquals new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]), lines[0]
        assertEquals new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [5.0, 5.0]), lines[1]
        LineString line = cc.linear
        assertTrue line.numPoints > 7
    }

    @Test void createWithListOfLineStrings() {
        CompoundCurve cc = new CompoundCurve([
                new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]),
                new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [5.0, 5.0])
        ])
        assertFalse cc.isClosed()
        assertTrue cc.isCurved()
        assertEquals "CompoundCurve", cc.geometryType
        assertEquals "COMPOUNDCURVE (CIRCULARSTRING (10.0 10.0, 0.0 20.0, -10.0 10.0), (-10.0 10.0, -10.0 0.0, 10.0 0.0, 5.0 5.0))", cc.curvedWkt
        assertTrue cc.wkt.startsWith("LINESTRING")
        List lines = cc.components
        assertEquals 2, lines.size()
        assertEquals new CircularString([10.0, 10.0], [0.0, 20.0], [-10.0, 10.0]), lines[0]
        assertEquals new LineString([-10.0, 10.0], [-10.0, 0.0], [10.0, 0.0], [5.0, 5.0]), lines[1]
        LineString line = cc.linear
        assertTrue line.numPoints > 7
    }

}

package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*

/**
 * The LineString unit test
 */
class LineStringTestCase {
	
    @Test void constructors() {
        def l1 = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l1.wkt
		
        def l2 = new LineString([111.0, -47],[123.0, -48],[110.0, -47])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l2.wkt

        def l3 = new LineString([ new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47)])
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l3.wkt

        def l4 = new LineString(new Point(111.0, -47), new Point(123.0, -48), new Point(110.0, -47))
        assertEquals "LINESTRING (111 -47, 123 -48, 110 -47)", l4.wkt
    }

    @Test void plus() {
        def line = new LineString([1,2],[3,4],[5,6])
        def multi = line + new LineString([7,8],[9,10],[11,12])
        assertEquals "MULTILINESTRING ((1 2, 3 4, 5 6), (7 8, 9 10, 11 12))", multi.wkt

        def m2 = new MultiLineString([])
        assertEquals "MULTILINESTRING EMPTY", m2.wkt
        def m3 = m2 + new LineString([1,2],[3,4])
        assertEquals "MULTILINESTRING ((1 2, 3 4))", m3.wkt
    }

    @Test void startEndPoint() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertEquals new Point(1,2).wkt, line.startPoint.wkt
        assertEquals new Point(5,6).wkt, line.endPoint.wkt
    }

    @Test void reverse() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertEquals "LINESTRING (1 2, 3 4, 5 6)", line.wkt
        def line2 = line.reverse()
        assertEquals "LINESTRING (5 6, 3 4, 1 2)", line2.wkt
    }

    @Test void isClosed() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertFalse(line.isClosed())
        assertTrue(new LineString([1,1],[1,4],[4,4],[4,1],[1,1]).isClosed())
    }

    @Test void isRing() {
        def line = new LineString([1,2],[3,4],[5,6])
        assertFalse(line.isRing())
        assertTrue(new LineString([1,1],[1,4],[4,4],[4,1],[1,1]).isRing())
    }

    @Test void close() {
        // Not closed
        LineString lineString = new LineString([1,1],[1,4],[4,4],[4,1])
        assertFalse lineString.isClosed()
        LinearRing linearRing = lineString.close()
        assertTrue linearRing.isClosed()
        assertEquals "LINEARRING (1 1, 1 4, 4 4, 4 1, 1 1)", linearRing.wkt
        // Already closed
        lineString = new LineString([1,1],[1,4],[4,4],[4,1],[1,1])
        linearRing = lineString.close()
        assertTrue linearRing.isClosed()
        assertEquals "LINEARRING (1 1, 1 4, 4 4, 4 1, 1 1)", linearRing.wkt

    }

    @Test(expected = IllegalArgumentException) void closeWithError() {
        LineString lineString = new LineString([1,1],[1,4])
        lineString.close()
    }

    @Test void interpolatePoint() {
        def line = new LineString([
            [1137466.548141059, 650434.9943107369],
            [1175272.4129268457, 648011.541439853],
            [1185935.6055587344, 632986.1336403737]
        ])
        def point = new Point(1153461.34, 649950.30)

        // Interpolate Point Start
        Point pt1 = line.interpolatePoint(0)
        assertEquals line.startPoint.wkt, pt1.wkt

        // Interpolate Point Middle
        Point pt2 = line.interpolatePoint(0.5)
        assertEquals "POINT (1165562.9204493894 648633.9448037925)", pt2.wkt

        // Interpolate Point End
        Point pt3 = line.interpolatePoint(1.0)
        assertEquals line.endPoint.wkt, pt3.wkt
    }

    @Test void locatePoint() {
        def line = new LineString([
            [1137466.548141059, 650434.9943107369],
            [1175272.4129268457, 648011.541439853],
            [1185935.6055587344, 632986.1336403737]
        ])
        def point = new Point(1153461.34, 649950.30)

        double position = line.locatePoint(point)
        assertEquals 0.284, position.round(3), 0.001
    }
    
    @Test void placePoint() {
        def line = new LineString([
            [1137466.548141059, 650434.9943107369],
            [1175272.4129268457, 648011.541439853],
            [1185935.6055587344, 632986.1336403737]
        ])
        def point = new Point(1153461.34, 649950.30)

        Point pt = line.placePoint(point)
        assertEquals "POINT (1153426.8271476042 649411.899502625)", pt.wkt
    }

    @Test void subLine() {
        def line = new LineString([
            [1137466.548141059, 650434.9943107369],
            [1175272.4129268457, 648011.541439853],
            [1185935.6055587344, 632986.1336403737]
        ])
        def point = new Point(1153461.34, 649950.30)

        LineString subLine = line.subLine(0.33, 0.67)
        assertEquals "LINESTRING (1156010.153864557 649246.3016361536, 1175115.6870342216 648021.5879714314)", subLine.wkt
    }

    @Test void addPoint() {
        def line = new LineString([new Point(0,0), new Point(5,5)])
        def newLine = line.addPoint(1, new Point(3,3))
        assertEquals 3, newLine.numPoints
        assertEquals "LINESTRING (0 0, 3 3, 5 5)", newLine.wkt
        newLine = newLine + new Point(10,10)
        assertEquals 4, newLine.numPoints
        assertEquals "LINESTRING (0 0, 3 3, 5 5, 10 10)", newLine.wkt
    }

    @Test void removePoint() {
        def line = new LineString([1,1],[2,2],[3,3],[4,4],[5,5])
        line = line.removePoint(1)
        assertEquals "LINESTRING (1 1, 3 3, 4 4, 5 5)", line.wkt
        line = -line
        assertEquals "LINESTRING (1 1, 3 3, 4 4)", line.wkt
        line = line.removePoint(0)
        assertEquals "LINESTRING (3 3, 4 4)", line.wkt
    }

    @Test void setPoint() {
        def line = new LineString([1,1],[2,2],[3,3],[4,4],[5,5])
        line = line.setPoint(0, new Point(0,0))
        assertEquals "LINESTRING (0 0, 2 2, 3 3, 4 4, 5 5)", line.wkt
        line = line.setPoint(4, new Point(10,10))
        assertEquals "LINESTRING (0 0, 2 2, 3 3, 4 4, 10 10)", line.wkt
    }
}

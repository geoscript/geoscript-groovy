package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*
import com.vividsolutions.jts.geom.Point as JtsPoint
import com.vividsolutions.jts.geom.Coordinate

/**
 * The Geometry unit test
 */
class GeometryTestCase {

    @Test void constructor() {
        JtsPoint pt = Geometry.factory.createPoint(new Coordinate(111,-47))
        Geometry g = new Geometry(pt)
        assertNotNull(g)
        assertEquals "POINT (111 -47)", g.toString()
    }
	
    @Test void buffer() {
        JtsPoint pt = Geometry.factory.createPoint(new Coordinate(111,-47))
        Geometry g = new Geometry(pt)
        Geometry p = g.buffer(5.0)
        assertNotNull(p)
        assertEquals pt.buffer(5.0).toString(), p.toString()
    }
	
    @Test void getWkt() {
        Geometry g = new Geometry(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.wkt
    }
	
    @Test void string() {
        Geometry g = new Geometry(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.toString()
    }
	
    @Test void wrap() {
        Geometry g = Geometry.wrap(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.toString()
    }
	
    @Test void fromWkt() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getCoordinates() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        def coordinates = g.coordinates
        assertEquals 1, coordinates.length
        assertEquals(111, coordinates[0].x, 0.0)
        assertEquals(-47, coordinates[0].y, 0.0)
    }
}
package geoscript.geom

import org.junit.Test

import static org.junit.Assert.*

/**
 * The PreparedGeometry UnitTest
 */
class PreparedGeometryTestCase {

    @Test void constructors() {
        def p1 = new PreparedGeometry(new Point(1,4))
        assertEquals "POINT (1 4)", p1.toString()
    }

    @Test void intersects() {
        def poly1 = Geometry.fromWKT('POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))')
        def prep = Geometry.prepare(poly1)
        def poly2 = Geometry.fromWKT('POLYGON ((2 2, 8 2, 8 8, 2 8, 2 2))')
        assertTrue(prep.intersects(poly2))
    }

    @Test void contains() {
        def poly1 = Geometry.fromWKT('POLYGON ((0 0, 5 0, 5 5, 0 5, 0 0))')
        def prep = Geometry.prepare(poly1)
        def poly2 = Geometry.fromWKT('POLYGON ((2 2, 8 2, 8 8, 2 8, 2 2))')
        assertFalse(prep.contains(poly2))
    }

}


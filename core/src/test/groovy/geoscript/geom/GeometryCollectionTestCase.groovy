package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class GeometryCollectionTestCase {
	
    @Test void constructors() {
        def gc1 = new GeometryCollection(new Point(1,2),new Point(3,4))
        assertEquals "GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4))", gc1.wkt

        def pts = [new Point(1,2),new Point(3,4)]
        def gc2 = new GeometryCollection(pts)
        assertEquals "GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4))", gc2.wkt
    }

    @Test void plus() {
        def gc1 = new GeometryCollection(new Point(1,2),new Point(3,4))
        assertEquals "GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4))", gc1.wkt

        def gc2 = gc1 + new Point(5,6)
        assertEquals "GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4), POINT (5 6))", gc2.wkt

        def gc3 = gc1 + new Point(7,8)
        assertEquals "GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4), POINT (7 8))", gc3.wkt

        def gc4 = new GeometryCollection([])
        assertEquals "GEOMETRYCOLLECTION EMPTY", gc4.wkt
        def gc5 = gc4 + new Point(8,9)
        assertEquals "GEOMETRYCOLLECTION (POINT (8 9))", gc5.wkt
    }

    @Test void slice() {
        GeometryCollection g = Geometry.fromWKT("MULTIPOINT ((1 1), (2 2), (3 3), (4 4), (5 5))")
        assertEquals "MULTIPOINT ((2 2), (3 3))", g.slice(1,3).wkt
        assertEquals "POINT (2 2)", g.slice(1,2).wkt
        assertEquals "MULTIPOINT ((3 3), (4 4), (5 5))", g.slice(2).wkt
        assertEquals "MULTIPOINT ((3 3), (4 4), (5 5))", g.slice(-3).wkt
        assertEquals "MULTIPOINT ((2 2), (3 3))", g.slice(-4, -2).wkt
        assertEquals "MULTIPOINT ((1 1), (2 2), (3 3))", g.slice(0, -2).wkt
    }

    @Test void narrow() {
        GeometryCollection gc = Geometry.fromWKT("GEOMETRYCOLLECTION (POINT (1 2), POINT (3 4), POINT (7 8))")
        assertEquals "MULTIPOINT ((1 2), (3 4), (7 8))", gc.narrow().wkt
        gc = Geometry.fromWKT("GEOMETRYCOLLECTION (POINT (8 9))")
        assertEquals "POINT (8 9)", gc.narrow().wkt
        gc = Geometry.fromWKT("GEOMETRYCOLLECTION (POINT (8 9), LINESTRING (1 1, 5 5))")
        assertEquals "GEOMETRYCOLLECTION (POINT (8 9), LINESTRING (1 1, 5 5))", gc.narrow().wkt
    }

}

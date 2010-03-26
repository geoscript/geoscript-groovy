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
    }
	
}
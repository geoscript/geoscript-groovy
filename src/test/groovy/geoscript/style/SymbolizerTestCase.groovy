package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Symbolizer UnitTest
 * @author Jared Erickson
 */
class SymbolizerTestCase {

    @Test void getDefaultForGeometryType() {

        def sym1 = Symbolizer.getDefaultForGeometryType("Point", "wheat")
        assertTrue(sym1 instanceof PointSymbolizer)

        def sym2 = Symbolizer.getDefaultForGeometryType("LineString", "#fff000")
        assertTrue(sym2 instanceof LineSymbolizer)

        def sym3 = Symbolizer.getDefaultForGeometryType("Polygon", java.awt.Color.BLACK)
        assertTrue(sym3 instanceof PolygonSymbolizer)

    }

    @Test void getSetGeometry() {
        def sym1 = new PointSymbolizer(geometry: "the_geom")
        assertNotNull sym1
        assertEquals "the_geom", sym1.geometry

        def sym2 = new PointSymbolizer(geometry: new geoscript.filter.Function("buffer(the_geom,100)"))
        assertNotNull sym2
        assertTrue sym2.geometry instanceof geoscript.filter.Function
    }

}



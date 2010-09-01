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

}



package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The PolygonSymbolizer UnitTest
 * @author Jared Erickson
 */
class PolygonSymbolizerTestCase {

    @Test void simpleFill() {
        def sym = new PolygonSymbolizer(
            fillColor: "#000080",
            strokeOpacity: 0
        )
        assertNotNull(sym)
        assertEquals("#000080", sym.fillColor)
        assertEquals(0, sym.strokeOpacity, 0.1)
    }

    @Test void simpleFillAndStroke() {
        def sym = new PolygonSymbolizer(
            fillColor: "#000080",
            strokeColor: "#FFFFFF",
            strokeWidth: 2
        )
        assertNotNull(sym)
        assertEquals("#000080", sym.fillColor)
        assertEquals("#ffffff", sym.strokeColor)
        assertEquals(1, sym.strokeOpacity, 0.1)
        assertEquals(2, sym.strokeWidth, 0.1)
    }

    @Test void transparentFill() {
        def sym = new PolygonSymbolizer(
            fillColor: "#000080",
            fillOpacity: 0.5,
            strokeColor: "#FFFFFF",
            strokeWidth: 2
        )
        assertNotNull(sym)
        assertEquals("#000080", sym.fillColor)
        assertEquals(0.5, sym.fillOpacity, 0.1)
        assertEquals("#ffffff", sym.strokeColor)
        assertEquals(1, sym.strokeOpacity, 0.1)
        assertEquals(2, sym.strokeWidth, 0.1)
    }

    @Test void graphicFill() {
        def sym = new PolygonSymbolizer(
            graphic: "colorblocks.png",
            strokeOpacity: 0
        )
        assertNotNull(sym)
        assertEquals("colorblocks.png", sym.graphic)
        assertEquals(0, sym.strokeOpacity, 0.1)
    }

    @Test void hatchingFill() {
        def sym = new PolygonSymbolizer(
            markName: "shape://times",
            markStrokeColor: "#990099",
            markStrokeWidth: 1,
            strokeOpacity: 0
        )
        assertNotNull(sym)
        assertEquals("shape://times", sym.markName)
        assertEquals("#990099", sym.markStrokeColor)
        assertEquals(1, sym.markStrokeWidth, 0.1)
        assertEquals(0, sym.strokeOpacity, 0.1)
    }

}


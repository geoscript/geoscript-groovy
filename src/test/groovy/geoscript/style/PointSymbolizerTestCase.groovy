package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The PointSymbolizer UnitTest
 * @author Jared Erickson
 */
class PointSymbolizerTestCase {

    @Test void simpleFill() {
        def sym = new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeOpacity: 0
        )
        assertNotNull(sym)
        assertEquals("circle", sym.shape)
        assertEquals("#FF0000", sym.fillColor)
        assertEquals(6, sym.size, 0.0)
        assertEquals(0, sym.strokeOpacity, 0.0)
    }

    
    @Test void simpleFillWithStroke() {
        def sym = new PointSymbolizer(
            shape: "circle",
            fillColor: "#FF0000",
            size: 6,
            strokeColor: "#000000",
            strokeWidth: 2
        )
        assertNotNull(sym)
        assertEquals("circle", sym.shape)
        assertEquals("#FF0000", sym.fillColor)
        assertEquals(6, sym.size, 0.0)
        assertEquals("#000000", sym.strokeColor)
        assertEquals(2, sym.strokeWidth, 0.0)
    }

    @Test void simpleRotatedSquare() {
        def sym = new PointSymbolizer(
            shape: "square",
            fillColor: "#009900",
            size: 12,
            rotation: 45,
            strokeOpacity: 0
        )
        assertNotNull(sym)
        assertEquals("square", sym.shape)
        assertEquals("#009900", sym.fillColor)
        assertEquals(12, sym.size, 0.0)
        assertEquals(45, sym.rotation, 0.0)
        assertEquals(0, sym.strokeOpacity, 0.0)
    }

    @Test void transparentTriangle() {
        def sym = new PointSymbolizer(
            shape: "triangle",
            fillColor: "#009900",
            fillOpacity: 0.2,
            size: 12,
            strokeColor: "#000000",
            strokeWidth: 2
        )
        assertNotNull(sym)
        assertEquals("triangle", sym.shape)
        assertEquals("#009900", sym.fillColor)
        assertEquals(0.2, sym.fillOpacity, 0.01)
        assertEquals(12, sym.size, 0.0)
        assertEquals("#000000", sym.strokeColor)
        assertEquals(2, sym.strokeWidth, 0.0)
        assertNull(sym.graphic)
    }

    @Test void graphicMarker() {
        def sym = new PointSymbolizer(graphic: 'smileyface.png')
        assertNotNull(sym)
        println(sym.graphic)
        assertEquals('smileyface.png', sym.graphic)
    }

}


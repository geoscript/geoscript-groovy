package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The LineSymbolizer UnitTest
 * @author Jared Erickson
 */
class LineSymbolizerTestCase {

    @Test void simpleLine() {
        def sym = new LineSymbolizer(
            strokeColor: "#000000",
            strokeWidth: 3,
            strokeOpacity: 0.5
        )
        assertNotNull(sym)
        assertEquals("#000000", sym.strokeColor)
        assertEquals(3, sym.strokeWidth, 0.1)
        assertEquals(0.5, sym.strokeOpacity, 0.1)
    }

    @Test void dashedLine() {
        def sym = new LineSymbolizer(
            strokeColor: "#0000FF",
            strokeWidth: 3,
            strokeDashArray: "5 2"
        )
        assertNotNull(sym)
        assertEquals("#0000ff", sym.strokeColor)
        assertEquals(3, sym.strokeWidth, 0.1)
        assertEquals("5.0 2.0", sym.strokeDashArray)
    }

    @Test void graphicStrokeLine() {
        def sym = new LineSymbolizer(
            graphicStrokeMarkName: "shape://vertline",
            graphicStrokeMarkStrokeColor: "#333333",
            graphicStrokeMarkStrokeWidth: 1,
            graphicStrokeMarkSize: 12
        )
        assertNotNull(sym)
        assertEquals("shape://vertline", sym.graphicStrokeMarkName)
        assertEquals("#333333", sym.graphicStrokeMarkStrokeColor)
        assertEquals(1, sym.graphicStrokeMarkStrokeWidth, 0.1)
        assertEquals(12, sym.graphicStrokeMarkSize, 0.1)
    }

}


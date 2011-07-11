package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Stroke Unit Test
 * @author Jared Erickson
 */
class StrokeTestCase {

    @Test void constructors() {

        // Create a simple Stroke
        Stroke stroke = new Stroke("navy")
        assertEquals "#000080", stroke.color
        assertEquals 1.0, stroke.width, 0.1
        assertEquals "Stroke(color = #000080, width = 1.0)", stroke.toString()

        def gtStroke = stroke.createStroke()
        assertEquals "#000080", gtStroke.color.value
        assertEquals 1.0, gtStroke.width.value, 0.1

        // Create a complicated Stroke
        stroke = new Stroke("wheat", 1.2, [5,2], "square", "bevel")
        assertEquals "#f5deb3", stroke.color
        assertEquals 1.2, stroke.width, 0.1
        assertEquals 5, stroke.dash[0]
        assertEquals 2, stroke.dash[1]
        assertEquals "square", stroke.cap
        assertEquals "bevel", stroke.join
        assertEquals "Stroke(color = #f5deb3, width = 1.2)", stroke.toString()

        gtStroke = stroke.createStroke()
        assertEquals "#f5deb3", gtStroke.color.value
        assertEquals 1.2, gtStroke.width.value, 0.1
        assertEquals 5, gtStroke.dashArray[0], 0.1
        assertEquals 2, gtStroke.dashArray[1], 0.1
        assertEquals "square", gtStroke.lineCap.value
        assertEquals "bevel", gtStroke.lineJoin.value

        // Add a Hatch
        assertNull stroke.hatch
        stroke.hatch("horline")
        assertNotNull stroke.hatch
        assertEquals "horline", stroke.hatch.name
        assertEquals "#000000", stroke.hatch.stroke.color
        assertEquals 1.0, stroke.hatch.stroke.width, 0.1
        assertEquals 8, stroke.hatch.size, 0.1

        gtStroke = stroke.createStroke()
        assertEquals "shape://horline", gtStroke.graphicStroke.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#000000", gtStroke.graphicStroke.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.0, gtStroke.graphicStroke.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 8, gtStroke.graphicStroke.size.value, 0.1

        // Create a Stroke with named parameters
        stroke = new Stroke(opacity: 0.75, color: "wheat", dash: [2,3], width: 1.2)
        assertEquals 0.75, stroke.opacity, 0.1
        assertEquals "#f5deb3", stroke.color
        assertEquals 1.2, stroke.width, 0.1
        assertEquals 2, stroke.dash[0]
        assertEquals 3, stroke.dash[1]
        assertEquals "Stroke(color = #f5deb3, width = 1.2)", stroke.toString()
    }

    @Test void apply() {
        Stroke stroke = new Stroke("navy")
        def sym = Symbolizer.styleFactory.createLineSymbolizer()
        stroke.apply(sym)
        assertEquals "#000080", sym.stroke.color.value
        assertEquals 1.0, sym.stroke.width.value, 0.1
    }

    @Test void prepare() {
        Stroke stroke = new Stroke("navy")
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createLineSymbolizer())
        stroke.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals "#000080", sym.stroke.color.value
        assertEquals 1.0, sym.stroke.width.value, 0.1
    }
}

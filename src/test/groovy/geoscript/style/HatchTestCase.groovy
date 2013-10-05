package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Expression
import geoscript.filter.Color

/**
 * The Hatch Unit Test
 * @author Jared Erickson
 */
class HatchTestCase {

    @Test void constructors() {

        Hatch hatch = new Hatch("horline")
        assertEquals "shape://horline", hatch.name.value
        assertEquals "#000000", hatch.stroke.color.value
        assertEquals 1.0, hatch.stroke.width.value, 0.1
        assertEquals 8, hatch.size.value, 0.1
        assertEquals "Hatch(name = shape://horline, fill = null, stroke = Stroke(color = #000000, width = 1), size = 8)", hatch.toString()

        def graphic = hatch.createHatch()
        assertEquals "shape://horline", graphic.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#000000", graphic.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.0, graphic.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 8, graphic.size.value, 0.1

        hatch = new Hatch("times", new Stroke("wheat", 1.2, [5,2], "square", "bevel"), 12.2)
        assertEquals "shape://times", hatch.name.value
        assertEquals "#f5deb3", hatch.stroke.color.value
        assertEquals 1.2, hatch.stroke.width.value, 0.1
        assertEquals 5, hatch.stroke.dash[0]
        assertEquals 2, hatch.stroke.dash[1]
        assertEquals "square", hatch.stroke.cap.value
        assertEquals "bevel", hatch.stroke.join.value
        assertEquals 12.2, hatch.size.value, 0.1
        assertEquals "Hatch(name = shape://times, fill = null, stroke = Stroke(color = #f5deb3, width = 1.2), size = 12.2)", hatch.toString()

        graphic = hatch.createHatch()
        assertEquals "shape://times", graphic.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#f5deb3", graphic.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.2, graphic.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 5, graphic.graphicalSymbols()[0].stroke.dashArray[0], 0.1
        assertEquals 2, graphic.graphicalSymbols()[0].stroke.dashArray[1], 0.1
        assertEquals "square", graphic.graphicalSymbols()[0].stroke.lineCap.value
        assertEquals "bevel", graphic.graphicalSymbols()[0].stroke.lineJoin.value
        assertEquals "bevel", graphic.graphicalSymbols()[0].stroke.lineJoin.value
        assertEquals 12.2, graphic.size.value, 0.1

        // Create a Hatch with Expressions
        hatch = new Hatch(new Expression("times"), new Stroke(new Color("wheat"), new Expression(1.2), [5,2], new Expression("square"), new Expression("bevel")), new Expression(12.2))
        assertEquals "shape://times", hatch.name.value
        assertEquals "#f5deb3", hatch.stroke.color.value
        assertEquals 1.2, hatch.stroke.width.value, 0.1
        assertEquals 5, hatch.stroke.dash[0]
        assertEquals 2, hatch.stroke.dash[1]
        assertEquals "square", hatch.stroke.cap.value
        assertEquals "bevel", hatch.stroke.join.value
        assertEquals 12.2, hatch.size.value, 0.1
        assertEquals "Hatch(name = shape://times, fill = null, stroke = Stroke(color = #f5deb3, width = 1.2), size = 12.2)", hatch.toString()

        graphic = hatch.createHatch()
        assertEquals "shape://times", graphic.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#f5deb3", graphic.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.2, graphic.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 5, graphic.graphicalSymbols()[0].stroke.dashArray[0], 0.1
        assertEquals 2, graphic.graphicalSymbols()[0].stroke.dashArray[1], 0.1
        assertEquals "square", graphic.graphicalSymbols()[0].stroke.lineCap.value
        assertEquals "bevel", graphic.graphicalSymbols()[0].stroke.lineJoin.value
        assertEquals "bevel", graphic.graphicalSymbols()[0].stroke.lineJoin.value
        assertEquals 12.2, graphic.size.value, 0.1

        // Named parameters
        hatch = new Hatch(size: 12, name: "slash", stroke: new Stroke("red",1.2))
        assertEquals "shape://slash", hatch.name.value
        assertEquals "#ff0000", hatch.stroke.color.value
        assertEquals 1.2, hatch.stroke.width.value, 0.1
        assertEquals 12, hatch.size.value, 0.1
        assertEquals "Hatch(name = shape://slash, fill = null, stroke = Stroke(color = #ff0000, width = 1.2), size = 12)", hatch.toString()

        // Hatch with Fill but no stroke
        hatch = new Hatch("circle", new Fill("red"), 8)
        assertEquals "circle", hatch.name.value
        assertEquals "#ff0000", hatch.fill.color.value
        assertNull hatch.stroke
        assertEquals 8, hatch.size.value, 0.1
        assertEquals "Hatch(name = circle, fill = Fill(color = #ff0000, opacity = 1.0), stroke = null, size = 8)", hatch.toString()

        // Hatch with Fill and stroke
        hatch = new Hatch("circle", new Fill("red"), new Stroke("wheat",0.1), 10)
        assertEquals "circle", hatch.name.value
        assertEquals "#ff0000", hatch.fill.color.value
        assertEquals "#f5deb3", hatch.stroke.color.value
        assertEquals 0.1, hatch.stroke.width.value, 0.1
        assertEquals 10, hatch.size.value, 0.1
        assertEquals "Hatch(name = circle, fill = Fill(color = #ff0000, opacity = 1.0), stroke = Stroke(color = #f5deb3, width = 0.1), size = 10)", hatch.toString()
    }
}

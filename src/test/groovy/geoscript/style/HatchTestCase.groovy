package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Hatch Unit Test
 * @author Jared Erickson
 */
class HatchTestCase {

    @Test void constructors() {

        Hatch hatch = new Hatch("horline")
        assertEquals "horline", hatch.name
        assertEquals "#000000", hatch.stroke.color
        assertEquals 1.0, hatch.stroke.width, 0.1
        assertEquals 8, hatch.size, 0.1
        assertEquals "Hatch(name = horline, stroke = Stroke(color = #000000, width = 1.0), size = 8.0)", hatch.toString()

        def graphic = hatch.createHatch()
        assertEquals "shape://horline", graphic.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#000000", graphic.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.0, graphic.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 8, graphic.size.value, 0.1

        hatch = new Hatch("times", new Stroke("wheat", 1.2, [5,2], "square", "bevel"), 12.2)
        assertEquals "times", hatch.name
        assertEquals "#f5deb3", hatch.stroke.color
        assertEquals 1.2, hatch.stroke.width, 0.1
        assertEquals 5, hatch.stroke.dash[0]
        assertEquals 2, hatch.stroke.dash[1]
        assertEquals "square", hatch.stroke.cap
        assertEquals "bevel", hatch.stroke.join
        assertEquals 12.2, hatch.size, 0.1
        assertEquals "Hatch(name = times, stroke = Stroke(color = #f5deb3, width = 1.2), size = 12.2)", hatch.toString()

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
        assertEquals "slash", hatch.name
        assertEquals "#ff0000", hatch.stroke.color
        assertEquals 1.2, hatch.stroke.width, 0.1
        assertEquals 12, hatch.size, 0.1
        assertEquals "Hatch(name = slash, stroke = Stroke(color = #ff0000, width = 1.2), size = 12.0)", hatch.toString()
    }
}

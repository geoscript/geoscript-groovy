package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The ColorMap UnitTest
 * @author Jared Erickson
 */
class ColorMapTestCase {

    @Test void colorMap() {
        def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        assertEquals "ramp", colorMap.type
        assertEquals 2, colorMap.values.size()
        assertFalse colorMap.extended

        colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]], "interval")
        assertEquals "interval", colorMap.type
        assertEquals 2, colorMap.values.size()
        assertFalse colorMap.extended

        colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]], "interval", true)
        assertEquals "interval", colorMap.type
        assertEquals 2, colorMap.values.size()
        assertTrue colorMap.extended
    }

    @Test void apply() {
        def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        colorMap.apply(sym)

        sym.colorMap.type = org.geotools.styling.ColorMap.TYPE_RAMP
        assertEquals colorMap.values.size(), sym.colorMap.colorMapEntries.length
        assertEquals colorMap.values[0].color, sym.colorMap.colorMapEntries[0].color.value
        assertEquals colorMap.values[0].quantity, sym.colorMap.colorMapEntries[0].quantity.value
        assertEquals colorMap.values[1].color, sym.colorMap.colorMapEntries[1].color.value
        assertEquals colorMap.values[1].quantity, sym.colorMap.colorMapEntries[1].quantity.value
    }

    @Test void prepare() {
        def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        colorMap.prepare(rule)
        def sym = rule.symbolizers[0]

        sym.colorMap.type = org.geotools.styling.ColorMap.TYPE_RAMP
        assertEquals colorMap.values.size(), sym.colorMap.colorMapEntries.length
        assertEquals colorMap.values[0].color, sym.colorMap.colorMapEntries[0].color.value
        assertEquals colorMap.values[0].quantity, sym.colorMap.colorMapEntries[0].quantity.value
        assertEquals colorMap.values[1].color, sym.colorMap.colorMapEntries[1].color.value
        assertEquals colorMap.values[1].quantity, sym.colorMap.colorMapEntries[1].quantity.value
    }

    @Test void string() {
        def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256]])
        assertEquals "ColorMap(values = [[color:#008000, quantity:70], [color:#663333, quantity:256]], type = ramp, extended = false)", colorMap.toString()
    }

}

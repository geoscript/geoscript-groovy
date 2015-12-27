package geoscript.style

import geoscript.filter.Color
import geoscript.layer.Format
import geoscript.layer.Raster
import org.junit.Test
import static org.junit.Assert.*

/**
 * The ColorMap UnitTest
 * @author Jared Erickson
 */
class ColorMapTestCase {

    @Test void colorMap() {

        // Create a ColorMap from a List of Maps
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

        // Load a Raster
        File file = new File(getClass().getClassLoader().getResource("raster.tif").toURI())
        Format format = Format.getFormat(file)
        Raster raster = format.read()
        Map extrema = raster.extrema
        double min = extrema.min[0]
        double max = extrema.max[0]

        // Create a ColorMap for a Raster and a color palette
        colorMap = new ColorMap(raster,"Greens", 5)
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 5, colorMap.values.size()
        assertEquals min, colorMap.values[0].quantity, 0.1
        assertEquals "#edf8e9", colorMap.values[0].color.hex
        assertEquals max, colorMap.values[4].quantity, 0.1
        assertEquals "#006d2c", colorMap.values[4].color.hex

        // Create a ColorMap for min and max values and a color palette
        colorMap = new ColorMap(min, max, "Greens", 5)
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 5, colorMap.values.size()
        assertEquals min, colorMap.values[0].quantity, 0.1
        assertEquals "#edf8e9", colorMap.values[0].color.hex
        assertEquals max, colorMap.values[4].quantity, 0.1
        assertEquals "#006d2c", colorMap.values[4].color.hex

        // Create a ColorMap for a Raster and a List of Colors
        colorMap = new ColorMap(raster, Color.getPaletteColors("Greens", 5))
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 5, colorMap.values.size()
        assertEquals min, colorMap.values[0].quantity, 0.1
        assertEquals "#edf8e9", colorMap.values[0].color.hex
        assertEquals max, colorMap.values[4].quantity, 0.1
        assertEquals "#006d2c", colorMap.values[4].color.hex

        // Create a ColorMap for a min and max value and a List of Colors
        colorMap = new ColorMap(min, max, Color.getPaletteColors("Greens", 5))
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 5, colorMap.values.size()
        assertEquals min, colorMap.values[0].quantity, 0.1
        assertEquals "#edf8e9", colorMap.values[0].color.hex
        assertEquals max, colorMap.values[4].quantity, 0.1
        assertEquals "#006d2c", colorMap.values[4].color.hex
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

package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Expression
import geoscript.filter.Color
import geoscript.filter.Function

/**
 * The Fill Unit Test
 * @author Jared Erickson
 */
class FillTestCase {

    @Test void constructors() {

        // Create a simple Fill
        Fill fill = new Fill("#ffffff", 0.75)
        assertEquals "#ffffff", fill.color.value
        assertEquals 0.75, fill.opacity.value, 0.01
        assertEquals "Fill(color = #ffffff, opacity = 0.75)", fill.toString()

        def gtFill = fill.createFill()
        assertEquals "#ffffff", gtFill.color.value
        assertEquals 0.75, gtFill.opacity.value, 0.01

        // Create a simple Fill with Expressions
        fill = new Fill(new Color("#ffffff"), new Expression(0.75))
        assertEquals "#ffffff", fill.color.value
        assertEquals 0.75, fill.opacity.value, 0.01
        assertEquals "Fill(color = #ffffff, opacity = 0.75)", fill.toString()

        gtFill = fill.createFill()
        assertEquals "#ffffff", gtFill.color.value
        assertEquals 0.75, gtFill.opacity.value, 0.01

        // Add Hatch
        fill.hatch("slash", new Stroke("wheat"), 4)
        assertEquals "shape://slash", fill.hatch.name.value
        assertEquals "#f5deb3", fill.hatch.stroke.color.value
        assertEquals 1.0, fill.hatch.stroke.width.value, 0.1
        assertEquals 4.0, fill.hatch.size.value, 0.1

        gtFill = fill.createFill();
        assertEquals "#ffffff", gtFill.color.value
        assertEquals 0.75, gtFill.opacity.value, 0.01
        assertEquals "shape://slash", gtFill.graphicFill.graphicalSymbols()[0].wellKnownName.value
        assertEquals "#f5deb3", gtFill.graphicFill.graphicalSymbols()[0].stroke.color.value
        assertEquals 1.0, gtFill.graphicFill.graphicalSymbols()[0].stroke.width.value, 0.1
        assertEquals 4.0, gtFill.graphicFill.size.value, 0.1

        // Add Icon
        fill.icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), fill.icon.url
        assertTrue fill.icon.url instanceof URL
        assertEquals "image/png", fill.icon.format

        // Named parameters
        fill = new Fill(color: "red", opacity:0.22)
        assertEquals "#ff0000", fill.color.value
        assertEquals 0.22, fill.opacity.value, 0.1
    }

    @Test void apply() {
        def sym = Symbolizer.styleFactory.createPolygonSymbolizer()
        Fill fill = new Fill("#ffffff", 0.75).icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        fill.apply(sym)
        assertEquals "#ffffff", sym.fill.color.value
        assertEquals 0.75, sym.fill.opacity.value, 0.01
        assertNotNull(sym.fill.graphicFill)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), sym.fill.graphicFill.graphicalSymbols()[0].location
        assertEquals "image/png", sym.fill.graphicFill.graphicalSymbols()[0].format
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPolygonSymbolizer())
        Fill fill = new Fill("#ffffff", 0.75).icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        fill.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals "#ffffff", sym.fill.color.value
        assertEquals 0.75, sym.fill.opacity.value, 0.01
        assertNotNull(sym.fill.graphicFill)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), sym.fill.graphicFill.graphicalSymbols()[0].location
        assertEquals "image/png", sym.fill.graphicFill.graphicalSymbols()[0].format
    }

    @Test void recode() {
        Fill fill = new Fill(new Function("Recode(SUB_REGION,'N Eng','#6495ED')"))
        println fill
    }

}

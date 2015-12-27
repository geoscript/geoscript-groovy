package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Expression

/**
 * The Icon Unit Test
 * @author Jared Erickson
 */
class IconTestCase {

    @Test void constructor() {
        Icon icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals(-1, icon.size.value)

        icon = new Icon(url: "http://www.geotools.org/_static/img/geotools-logo.png", format: "image/png", size: 32)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals 32, icon.size.value

        // No format
        icon = new Icon(url: "http://www.geotools.org/_static/img/geotools-logo.png", size: 32)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals 32, icon.size.value

        // Short hand format with named parameters
        icon = new Icon(url: "http://www.geotools.org/_static/img/geotools-logo.png", format: "png", size: 32)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals 32, icon.size.value

        // Short hand format with full constructor
        icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.png", "png", 32)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals 32, icon.size.value

        icon = new Icon(url: "http://www.geotools.org/_static/img/geotools-logo.png", format: "image/png", size: new Expression(32))
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals 32, icon.size.value

        icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.png")
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
        assertEquals(-1, icon.size.value)

        icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.jpeg")
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.jpeg"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/jpeg", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.jpeg, format = image/jpeg)", icon.toString()
        assertEquals(-1, icon.size.value)
    }

    @Test void apply() {
        Icon icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        def pointSym = Symbolizer.styleFactory.createPointSymbolizer()
        icon.apply(pointSym)
        assertNotNull(pointSym.graphic)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), pointSym.graphic.graphicalSymbols()[0].location
        assertEquals "image/png", pointSym.graphic.graphicalSymbols()[0].format
    }

    @Test void prepare() {
        Icon icon = new Icon("http://www.geotools.org/_static/img/geotools-logo.png", "image/png")
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPointSymbolizer())
        rule.symbolizers().add(Symbolizer.styleFactory.createPolygonSymbolizer())
        icon.prepare(rule)

        def pointSym = rule.symbolizers[0]
        assertNotNull(pointSym.graphic)
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), pointSym.graphic.graphicalSymbols()[0].location
        assertEquals "image/png", pointSym.graphic.graphicalSymbols()[0].format
    }

}

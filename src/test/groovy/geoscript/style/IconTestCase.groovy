package geoscript.style

import org.junit.Test
import static org.junit.Assert.*


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

        icon = new Icon(url: "http://www.geotools.org/_static/img/geotools-logo.png", format: "image/png")
        assertEquals new URL("http://www.geotools.org/_static/img/geotools-logo.png"), icon.url
        assertTrue icon.url instanceof URL
        assertEquals "image/png", icon.format
        assertEquals "Icon(url = http://www.geotools.org/_static/img/geotools-logo.png, format = image/png)", icon.toString()
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

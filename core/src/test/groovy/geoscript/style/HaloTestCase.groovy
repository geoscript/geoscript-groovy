package geoscript.style

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.filter.Color
import geoscript.filter.Expression

/**
 * The Halo Unit Test
 * @author Jared Erickson
 */
class HaloTestCase {

    @Test void constructors() {
        Halo halo = new Halo(new Fill("#ffffff"), 1)
        assertEquals "#ffffff", halo.fill.color.value
        assertEquals 1, halo.radius.value, 0.1
        assertEquals "Halo(fill = Fill(color = #ffffff, opacity = 1.0), radius = 1)", halo.toString()

        halo = new Halo(new Fill(new Color("#ffffff")), new Expression(1))
        assertEquals "#ffffff", halo.fill.color.value
        assertEquals 1, halo.radius.value, 0.1
        assertEquals "Halo(fill = Fill(color = #ffffff, opacity = 1.0), radius = 1)", halo.toString()

        halo = new Halo(fill: new Fill("navy"), radius: 2.5)
        assertEquals "#000080", halo.fill.color.value
        assertEquals 2.5, halo.radius.value, 0.1
        assertEquals "Halo(fill = Fill(color = #000080, opacity = 1.0), radius = 2.5)", halo.toString()
    }

    @Test void apply() {
        def text = Symbolizer.styleFactory.createTextSymbolizer()
        Halo halo = new Halo(new Fill("#ffffff"), 1)
        halo.apply(text)
        assertEquals "#ffffff", text.halo.fill.color.value
        assertEquals 1, text.halo.radius.value, 0.1
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())
        Halo halo = new Halo(new Fill("#ffffff"), 1)
        halo.prepare(rule)
        def text = rule.symbolizers()[0]
        assertEquals "#ffffff", text.halo.fill.color.value
        assertEquals 1, text.halo.radius.value, 0.1
    }

}

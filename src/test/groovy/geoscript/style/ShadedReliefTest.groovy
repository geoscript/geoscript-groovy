package geoscript.style

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The ShadedRelief UnitTest
 * @author Jared Erickson
 */
class ShadedReliefTest {

    @Test void constructors() {
        def shadedRelief = new ShadedRelief()
        assertEquals 55, shadedRelief.reliefFactor.value
        assertFalse shadedRelief.brightnessOnly

        shadedRelief = new ShadedRelief(35, true)
        assertEquals 35, shadedRelief.reliefFactor.value
        assertTrue shadedRelief.brightnessOnly
    }

    @Test void apply() {
        def shadedRelief = new ShadedRelief()
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        shadedRelief.apply(sym)
        assertEquals 55, sym.shadedRelief.reliefFactor.value
        assertFalse sym.shadedRelief.brightnessOnly

        shadedRelief = new ShadedRelief(35, true)
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        shadedRelief.apply(sym)
        assertEquals 35, sym.shadedRelief.reliefFactor.value
        assertTrue sym.shadedRelief.brightnessOnly
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        def shadedRelief = new ShadedRelief()
        shadedRelief.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals 55, sym.shadedRelief.reliefFactor.value
        assertFalse sym.shadedRelief.brightnessOnly

        rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        shadedRelief = new ShadedRelief(35, true)
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        shadedRelief.prepare(rule)
        sym = rule.symbolizers[0]
        assertEquals 35, sym.shadedRelief.reliefFactor.value
        assertTrue sym.shadedRelief.brightnessOnly
    }

    @Test void string() {
        def shadedRelief = new ShadedRelief()
        assertEquals "ShadedRelief(reliefFactor = 55, brightnessOnly = false)", shadedRelief.toString()

        shadedRelief = new ShadedRelief(35, true)
        assertEquals "ShadedRelief(reliefFactor = 35, brightnessOnly = true)", shadedRelief.toString()
    }
}

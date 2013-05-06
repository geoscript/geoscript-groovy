package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 *
 */
class ContrastEnhancementTestCase {

    @Test void constructors() {
        def c = new ContrastEnhancement("normalize")
        assertEquals "normalize", c.method
        assertNull c.gammaValue

        c = new ContrastEnhancement("histogram", 0.5)
        assertEquals "histogram", c.method
        assertEquals 0.5, c.gammaValue.value, 0.1
    }

    @Test void apply() {
        def c = new ContrastEnhancement("normalize")
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        c.apply(sym)
        assertEquals "NORMALIZE", sym.contrastEnhancement.method.name()
        assertNull sym.contrastEnhancement.gammaValue

        c = new ContrastEnhancement("histogram", 0.5)
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        c.apply(sym)
        assertEquals "HISTOGRAM", sym.contrastEnhancement.method.name()
        assertEquals 0.5, sym.contrastEnhancement.gammaValue.value, 0.1
    }

    @Test void prepare() {
        def c = new ContrastEnhancement("normalize")
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        c.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals "NORMALIZE", sym.contrastEnhancement.method.name()
        assertNull sym.contrastEnhancement.gammaValue

        c = new ContrastEnhancement("histogram", 0.5)
        rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        c.prepare(rule)
        sym = rule.symbolizers[0]
        assertEquals "HISTOGRAM", sym.contrastEnhancement.method.name()
        assertEquals 0.5, sym.contrastEnhancement.gammaValue.value, 0.1
    }

    @Test void string() {
        def c = new ContrastEnhancement("normalize")
        assertEquals "ContrastEnhancement(method = normalize)", c.toString()

        c = new ContrastEnhancement("histogram", 0.5)
        assertEquals "ContrastEnhancement(method = histogram, gammaValue = 0.5)", c.toString()
    }

}

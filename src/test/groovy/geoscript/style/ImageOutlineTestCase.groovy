package geoscript.style

import org.geotools.styling.PolygonSymbolizer
import org.junit.Test
import static org.junit.Assert.*

/**
 * The ImageOutline UnitTest
 * @author Jared Erickson
 */
class ImageOutlineTestCase {

    @Test
    void constructors() {
        def outline = new ImageOutline(new Fill("black", 0.75))
        assertNotNull outline.fill
        assertNull outline.stroke

        outline = new ImageOutline(new Stroke("black", 0.35))
        assertNull outline.fill
        assertNotNull outline.stroke
    }

    @Test
    void apply() {
        def outline = new ImageOutline(new Fill("black", 0.75))
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        outline.apply(sym)
        assertNotNull sym.imageOutline
        assertTrue sym.imageOutline instanceof PolygonSymbolizer
        assertNotNull sym.imageOutline.fill
        assertNull sym.imageOutline.stroke

        outline = new ImageOutline(new Stroke("black", 0.75))
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        outline.apply(sym)
        assertNotNull sym.imageOutline
        assertTrue sym.imageOutline instanceof PolygonSymbolizer
        assertNull sym.imageOutline.fill
        assertNotNull sym.imageOutline.stroke
    }

    @Test
    void prepare() {
        def outline = new ImageOutline(new Fill("black", 0.75))
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        outline.prepare(rule)
        def sym = rule.symbolizers[0]
        assertNotNull sym.imageOutline
        assertTrue sym.imageOutline instanceof PolygonSymbolizer
        assertNotNull sym.imageOutline.fill
        assertNull sym.imageOutline.stroke

        outline = new ImageOutline(new Stroke("black", 0.75))
        rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        outline.prepare(rule)
        sym = rule.symbolizers[0]
        assertNotNull sym.imageOutline
        assertTrue sym.imageOutline instanceof PolygonSymbolizer
        assertNull sym.imageOutline.fill
        assertNotNull sym.imageOutline.stroke
    }

    @Test
    void string() {
        def outline = new ImageOutline(new Fill("black", 0.75))
        assertEquals "ImageOutline(fill = Fill(color = #000000, opacity = 0.75))", outline.toString()

        outline = new ImageOutline(new Stroke("black", 0.35))
        assertEquals "ImageOutline(stroke = Stroke(color = #000000, width = 0.35))", outline.toString()
    }
}

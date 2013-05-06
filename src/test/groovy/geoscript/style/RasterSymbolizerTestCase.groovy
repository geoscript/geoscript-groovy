package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Raster UnitTest
 * @author Jared Erickson
 */
class RasterSymbolizerTestCase {

    @Test void raster() {
        RasterSymbolizer raster = new RasterSymbolizer(0.5)
        assertEquals 0.5, raster.opacity.value, 0.1
        assertEquals "RANDOM", raster.overlap.value
        assertEquals "grid", raster.geometry.value

        raster = new RasterSymbolizer(0.75, "AVERAGE", "the_geom")
        assertEquals 0.75, raster.opacity.value, 0.1
        assertEquals "AVERAGE", raster.overlap.value
        assertEquals "the_geom", raster.geometry.value
    }

    @Test void apply() {
        RasterSymbolizer raster = new RasterSymbolizer(0.5)
        def sym = Symbolizer.styleFactory.createRasterSymbolizer()
        raster.apply(sym)
        assertEquals 0.5, sym.opacity.value
        assertEquals "RANDOM", sym.overlap.value.toString()
        assertEquals "grid", sym.geometry.value

        raster = new RasterSymbolizer(0.75, "AVERAGE", "the_geom")
        sym = Symbolizer.styleFactory.createRasterSymbolizer()
        raster.apply(sym)
        assertEquals 0.75, sym.opacity.value
        assertEquals "AVERAGE", sym.overlap.value
        assertEquals "the_geom", sym.geometry.value
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createRasterSymbolizer())
        RasterSymbolizer raster = new RasterSymbolizer(0.5)
        raster.prepare(rule)
        def sym = rule.symbolizers[0]
        assertEquals 0.5, sym.opacity.value
        assertEquals "RANDOM", sym.overlap.value.toString()
        assertEquals "grid", sym.geometry.value
    }

    @Test void string() {
        RasterSymbolizer raster = new RasterSymbolizer(0.5)
        assertEquals "Raster(opacity = 0.5, overlap = RANDOM, geometry = grid)", raster.toString()
        raster = new RasterSymbolizer(0.75, "AVERAGE", "the_geom")
        assertEquals "Raster(opacity = 0.75, overlap = AVERAGE, geometry = the_geom)", raster.toString()
    }
}

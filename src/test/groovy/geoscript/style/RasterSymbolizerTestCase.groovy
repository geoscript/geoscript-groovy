package geoscript.style

import geoscript.raster.*
import org.junit.Test
import static org.junit.Assert.*

/**
 * The RasterSymbolizer UnitTest
 * @author Jared Erickson
 */
class RasterSymbolizerTestCase {

    @Test void emptyConstructor() {
        def sym = new RasterSymbolizer()
        assertNotNull sym
    }

    @Test void opacity() {
        def sym = new RasterSymbolizer()
        sym.opacity = 0.26
        assertEquals 0.26, sym.opacity, 0.01
    }

    @Test void createGrayscale() {
        def sym = RasterSymbolizer.createGrayscale(1)
        assertNotNull sym
    }

    @Test void createRGB() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)

        def sym = RasterSymbolizer.createRGB(raster)
        assertNotNull sym
    }

    @Test void createColorMap() {
        def sym = RasterSymbolizer.createColorMap([
            [color: "#ffffff", opacity: 0.54, quantity: 100, label: "#1"],
            [color: "#ffffff", opacity: 0.54, quantity: 100],
            [color: "#ffffff", quantity: 100]
        ])
        assertNotNull sym
    }


}

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

    @Test void addToColorMap() {
        def sym = new RasterSymbolizer()
        sym.addToColorMap(100, "#ff0000")
        sym.addToColorMap(200, "#00ff00","Level 200")
        sym.addToColorMap(300, "#0000ff", "Level 300", 0.25)
        def colorMap = sym.colorMap
        assertEquals 100, colorMap[0].quantity
        assertEquals 200, colorMap[1].quantity
        assertEquals 300, colorMap[2].quantity
        assertEquals "#ff0000", colorMap[0].color
        assertEquals "#00ff00", colorMap[1].color
        assertEquals "#0000ff", colorMap[2].color
        assertNull colorMap[0].label
        assertEquals "Level 200", colorMap[1].label
        assertEquals "Level 300", colorMap[2].label
        assertEquals 1.0, colorMap[0].opacity, 0.1
        assertEquals 1.0, colorMap[1].opacity, 0.1
        assertEquals 0.25, colorMap[2].opacity, 0.01
    }

    @Test void setColorMap() {
        def sym = new RasterSymbolizer()
        sym.colorMap = [
            [quantity: 100, color: "#ff0000"],
            [quantity: 200, color: "#00ff00", label: "Level 200"],
            [quantity: 300, color: "#0000ff", label: "Level 300", opacity: 0.25]
        ]
        def colorMap = sym.colorMap
        assertEquals 100, colorMap[0].quantity
        assertEquals 200, colorMap[1].quantity
        assertEquals 300, colorMap[2].quantity
        assertEquals "#ff0000", colorMap[0].color
        assertEquals "#00ff00", colorMap[1].color
        assertEquals "#0000ff", colorMap[2].color
        assertNull colorMap[0].label
        assertEquals "Level 200", colorMap[1].label
        assertEquals "Level 300", colorMap[2].label
        assertEquals 1.0, colorMap[0].opacity, 0.1
        assertEquals 1.0, colorMap[1].opacity, 0.1
        assertEquals 0.25, colorMap[2].opacity, 0.01
    }


}

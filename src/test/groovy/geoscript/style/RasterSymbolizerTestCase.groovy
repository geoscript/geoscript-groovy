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

    @Test void shadedRelief() {
        def sym = new RasterSymbolizer()
        assertNull sym.shadedReliefBrightnessOnly
        assertNull sym.shadedReliefFactor

        sym.setShadedRelief(false, 55)
        assertFalse sym.shadedReliefBrightnessOnly
        assertEquals 55, sym.shadedReliefFactor, 1

        sym = new RasterSymbolizer(
            shadedReliefBrightnessOnly: true,
            shadedReliefFactor: 43
        )
        assertTrue sym.shadedReliefBrightnessOnly
        assertEquals 43, sym.shadedReliefFactor, 1
    }

    @Test void contrastEnhancement() {
        def sym = new RasterSymbolizer()
        assertNull sym.contrastEnhancementMethod
        assertNull sym.contrastEnhancementGammaValue

        sym.contrastEnhancementMethod = "histogram"
        assertEquals "histogram", sym.contrastEnhancementMethod
        
        sym.contrastEnhancementMethod = "normalize"
        assertEquals "normalize", sym.contrastEnhancementMethod

        sym.contrastEnhancementGammaValue = 5.5
        assertEquals 5.5, sym.contrastEnhancementGammaValue, 0.1
    }

    @Test void imageOutline() {
        def sym = new RasterSymbolizer()
        assertNull(sym.imageOutline)
        sym.imageOutline = new LineSymbolizer(
                strokeWidth: 0.25,
                strokeColor: "#ff00ff"
        )
        assertTrue sym.imageOutline instanceof LineSymbolizer
        sym.imageOutline = new PolygonSymbolizer(
                strokeWidth: 0.55,
                strokeColor: "black",
                fillColor: "white",
                fillOpacity: 0.75
        )
        assertTrue sym.imageOutline instanceof PolygonSymbolizer
    }

    @Test void channelSelection() {
        def sym = new RasterSymbolizer()
        assertNull sym.channelSelection

        sym.channelSelection = [
            "gray": [
                "name": "1",
                "contrastEnhancement": [
                    "method": "histogram",
                    "gammaValue": 2.3
                ]
            ]
        ]

        def channels = sym.channelSelection
        assertNotNull channels
        assertTrue channels.containsKey("gray")
        assertEquals "1", channels.gray.name
        assertEquals "histogram", channels.gray.contrastEnhancement.method
        assertEquals  2.3, channels.gray.contrastEnhancement.gammaValue, 0.1

        sym.channelSelection = [
            "red": [
                "name": "1",
                "contrastEnhancement": [
                    "method": "normalize",
                    "gammaValue": 1.0
                ]
            ],
            "green": [
                "name": "2",
                "contrastEnhancement": [
                    "method": "histogram",
                    "gammaValue": 2.0
                ]
            ],
            "blue": [
                "name": "3",
                "contrastEnhancement": [
                    "method": "normalize",
                    "gammaValue": 3.0
                ]
            ]
        ]

        channels = sym.channelSelection
        assertNotNull channels
        assertTrue channels.containsKey("red")
        assertTrue channels.containsKey("green")
        assertTrue channels.containsKey("blue")
        assertEquals "1", channels.red.name
        assertEquals "2", channels.green.name
        assertEquals "3", channels.blue.name
        assertEquals "normalize", channels.red.contrastEnhancement.method
        assertEquals "histogram", channels.green.contrastEnhancement.method
        assertEquals "normalize", channels.blue.contrastEnhancement.method
        assertEquals  1.0, channels.red.contrastEnhancement.gammaValue, 0.1
        assertEquals  2.0, channels.green.contrastEnhancement.gammaValue, 0.1
        assertEquals  3.0, channels.blue.contrastEnhancement.gammaValue, 0.1
    }

}

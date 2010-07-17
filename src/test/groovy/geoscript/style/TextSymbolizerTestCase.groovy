package geoscript.style

import org.junit.Test
import static org.junit.Assert.*

/**
 * The TextSymbolizer UnitTest
 * @author Jared Erickson
 */
class TextSymbolizerTestCase {

    @Test void simple() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#000000"
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
    }

    @Test void colorFromRGB() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "0,0,0"
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
    }

    @Test void colorFromName() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "black"
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
    }

    @Test void styled() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#000000",
            fontFamily: "Arial",
            fontSize: 12,
            fontStyle: "normal",
            fontWeight: "bold",
            anchorPointX: 0.5,
            anchorPointY: 0.5,
            displacementX: 0,
            displacementY: 10
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
        assertEquals("Arial", sym.fontFamily)
        assertEquals(12, sym.fontSize)
        assertEquals("normal", sym.fontStyle)
        assertEquals("bold", sym.fontWeight)
        assertEquals(0.5, sym.anchorPointX, 0.1)
        assertEquals(0.5, sym.anchorPointY, 0.1)
        assertEquals(0, sym.displacementX, 0.1)
        assertEquals(10, sym.displacementY, 0.1)
    }

    @Test void rotated() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#990099",
            fontFamily: "Arial",
            fontSize: 12,
            fontStyle: "normal",
            fontWeight: "bold",
            anchorPointX: 0.5,
            anchorPointY: 0,
            displacementX: 0,
            displacementY: 25,
            rotation: -45
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#990099", sym.color)
        assertEquals("Arial", sym.fontFamily)
        assertEquals(12, sym.fontSize)
        assertEquals("normal", sym.fontStyle)
        assertEquals("bold", sym.fontWeight)
        assertEquals(0.5, sym.anchorPointX, 0.1)
        assertEquals(0, sym.anchorPointY, 0.1)
        assertEquals(0, sym.displacementX, 0.1)
        assertEquals(25, sym.displacementY, 0.1)
        assertEquals(-45, sym.rotation, 0.1)
    }

    @Test void followLine() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#000000",
            followLine: true
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
        assertEquals(true, sym.followLine)
    }

    @Test void followLineOptimized() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#000000",
            followLine: true,
            maxAngleDelta: 90,
            maxDisplacement: 400,
            repeat: 150
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
        assertEquals(true, sym.followLine)
        assertEquals(90, sym.maxAngleDelta, 0.1)
        assertEquals(400, sym.maxDisplacement, 0.1)
        assertEquals(150, sym.repeat, 0.1)
    }

    @Test void followLineOptimizedStyled() {
        def sym = new TextSymbolizer(
            label: "name",
            color: "#000000",
            fontFamily: "Arial",
            fontSize: 10,
            fontStyle: "normal",
            fontWeight: "bold",
            followLine: true,
            maxAngleDelta: 90,
            maxDisplacement: 400,
            repeat: 150
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
        assertEquals("Arial", sym.fontFamily)
        assertEquals(10, sym.fontSize)
        assertEquals("normal", sym.fontStyle)
        assertEquals("bold", sym.fontWeight)
        assertEquals(true, sym.followLine)
        assertEquals(90, sym.maxAngleDelta, 0.1)
        assertEquals(400, sym.maxDisplacement, 0.1)
        assertEquals(150, sym.repeat, 0.1)
    }

    @Test void simpleHalo() {
        def sym = new TextSymbolizer(
            label: "name",
            haloColor: "#FFFFFF",
            haloRadius: 3
        )
        assertEquals("name", sym.label)
        assertEquals("#ffffff", sym.haloColor)
        assertEquals(3, sym.haloRadius, 0.1)
    }

    @Test void styledLabelForPolygons() {
        def sym = new TextSymbolizer(
            label: "name",
            fontFamily: "Arial",
            fontSize: 11,
            fontStyle: "normal",
            fontWeight: "bold",
            anchorPointX: 0.5,
            anchorPointY: 0.4,
            color: "#000000",
            autoWrap: 30,
            maxDisplacement: 150
        )
        assertNotNull(sym)
        assertEquals("name", sym.label)
        assertEquals("#000000", sym.color)
        assertEquals("Arial", sym.fontFamily)
        assertEquals(11, sym.fontSize)
        assertEquals("normal", sym.fontStyle)
        assertEquals("bold", sym.fontWeight)
        assertEquals(0.5, sym.anchorPointX, 0.1)
        assertEquals(0.4, sym.anchorPointY, 0.1)
        assertEquals(30, sym.autoWrap, 0.1)
        assertEquals(150, sym.maxDisplacement, 0.1)
    }
}


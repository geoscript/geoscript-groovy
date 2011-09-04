package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*

/**
 * The ColorUtil Unit Test
 * @author Jared Erickson
 */
class ColorTestCase {

    private void assertColorsEqual(java.awt.Color c1, java.awt.Color c2) {
        assertEquals c1.red, c2.red
        assertEquals c1.green, c2.green
        assertEquals c1.blue, c2.blue
    }

    @Test void constructors() {

        Color color = new Color("black")
        assertTrue color.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "#000000", color.toString()

        color = new Color(color)
        assertTrue color.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "#000000", color.toString()

        color = new Color("#000000")
        assertTrue color.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "#000000", color.toString()

        color = new Color([0,0,0])
        assertTrue color.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "#000000", color.toString()
    }

    @Test void getColor() {

        // ColorUtil
        assertColorsEqual java.awt.Color.BLACK, Color.getColor(java.awt.Color.BLACK)

        // RGB String
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("0,255,0")

        // Hexadecimal
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("#00ff00")
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("#0f0")

        // CSS Color Names
        assertColorsEqual new java.awt.Color(245,222,179), Color.getColor("wheat")

        // RGB As List
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor([0,255,0])

        // RGB As Map
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor([r: 0, g: 255, b: 0, a: 125])

        // Null
        assertNull Color.getColor("NOT A COLOR")

    }

    @Test void toHex() {

        // Hexadecimal
        assertEquals "#00ff00", Color.toHex("#00ff00")

        // Color
        assertEquals "#00ff00", Color.toHex(new java.awt.Color(0,255,0))

        // RGB
        assertEquals "#00ff00", Color.toHex("0,255,0")

        // CSS Color Name
        assertEquals "#f5deb3", Color.toHex("wheat")
    }

    @Test void getRandom() {
        java.awt.Color c1 = Color.getRandom()
        assertNotNull c1
        assertTrue c1 instanceof java.awt.Color

        java.awt.Color c2 = Color.getRandom()
        assertNotNull c2
        assertTrue c2 instanceof java.awt.Color

        assertFalse c1.equals(c2)
    }

    @Test void getRandomPastel() {
        java.awt.Color c1 = Color.getRandomPastel()
        assertNotNull c1
        assertTrue c1 instanceof java.awt.Color

        java.awt.Color c2 = Color.getRandomPastel()
        assertNotNull c2
        assertTrue c2 instanceof java.awt.Color

        assertFalse c1.equals(c2)
    }

    @Test void getPaletteNames() {

        // All
        List names = Color.getPaletteNames()
        assertTrue(names.size() > 0)

        // Qualitative
        names = Color.getPaletteNames("qualitative")
        assertTrue(names.size() > 0)

        // Sequential
        names = Color.getPaletteNames("sequential")
        assertTrue(names.size() > 0)

        // Diverging
        names = Color.getPaletteNames("diverging")
        assertTrue(names.size() > 0)
    }

    @Test void getPaletteColors() {

        // 5 Greens
        List colors = Color.getPaletteColors("Greens", 5)
        assertTrue(colors.size() == 5)

        // 11 RdYlGn
        colors = Color.getPaletteColors("RdYlGn", 999)
        assertTrue(colors.size() == 11)

        // Empty
        colors = Color.getPaletteColors("NOT A REAL PALETTE", 5)
        assertTrue(colors.isEmpty())

    }

}

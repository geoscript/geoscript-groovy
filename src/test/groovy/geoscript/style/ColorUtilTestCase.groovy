package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import java.awt.Color

/**
 * The ColorUtil Unit Test
 * @author Jared Erickson
 */
class ColorUtilTestCase {

    private void assertColorsEqual(Color c1, Color c2) {
        assertEquals c1.red, c2.red
        assertEquals c1.green, c2.green
        assertEquals c1.blue, c2.blue
    }

    @Test void getColor() {

        // ColorUtil
        assertColorsEqual Color.BLACK, ColorUtil.getColor(Color.BLACK)

        // RGB String
        assertColorsEqual new Color(0,255,0), ColorUtil.getColor("0,255,0")

        // Hexadecimal
        assertColorsEqual new Color(0,255,0), ColorUtil.getColor("#00ff00")
        assertColorsEqual new Color(0,255,0), ColorUtil.getColor("#0f0")

        // CSS Color Names
        assertColorsEqual new Color(245,222,179), ColorUtil.getColor("wheat")

        // RGB As List
        assertColorsEqual new Color(0,255,0), ColorUtil.getColor([0,255,0])

        // RGB As Map
        assertColorsEqual new Color(0,255,0), ColorUtil.getColor([r: 0, g: 255, b: 0, a: 125])

        // Null
        assertNull ColorUtil.getColor("NOT A COLOR")

    }

    @Test void toHex() {

        // Hexadecimal
        assertEquals "#00ff00", ColorUtil.toHex("#00ff00")

        // Color
        assertEquals "#00ff00", ColorUtil.toHex(new Color(0,255,0))

        // RGB
        assertEquals "#00ff00", ColorUtil.toHex("0,255,0")

        // CSS Color Name
        assertEquals "#f5deb3", ColorUtil.toHex("wheat")
    }

    @Test void getRandom() {
        Color c1 = ColorUtil.getRandom()
        assertNotNull c1
        assertTrue c1 instanceof Color

        Color c2 = ColorUtil.getRandom()
        assertNotNull c2
        assertTrue c2 instanceof Color

        assertFalse c1.equals(c2)
    }

    @Test void getRandomPastel() {
        Color c1 = ColorUtil.getRandomPastel()
        assertNotNull c1
        assertTrue c1 instanceof Color

        Color c2 = ColorUtil.getRandomPastel()
        assertNotNull c2
        assertTrue c2 instanceof Color

        assertFalse c1.equals(c2)
    }

    @Test void getPaletteNames() {

        // All
        List names = ColorUtil.getPaletteNames()
        assertTrue(names.size() > 0)

        // Qualitative
        names = ColorUtil.getPaletteNames("qualitative")
        assertTrue(names.size() > 0)

        // Sequential
        names = ColorUtil.getPaletteNames("sequential")
        assertTrue(names.size() > 0)

        // Diverging
        names = ColorUtil.getPaletteNames("diverging")
        assertTrue(names.size() > 0)
    }

    @Test void getPaletteColors() {

        // 5 Greens
        List colors = ColorUtil.getPaletteColors("Greens", 5)
        assertTrue(colors.size() == 5)

        // 11 RdYlGn
        colors = ColorUtil.getPaletteColors("RdYlGn", 999)
        assertTrue(colors.size() == 11)

        // Empty
        colors = ColorUtil.getPaletteColors("NOT A REAL PALETTE", 5)
        assertTrue(colors.isEmpty())

    }

}

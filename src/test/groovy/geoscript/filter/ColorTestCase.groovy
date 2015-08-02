package geoscript.filter

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The ColorUtil Unit Test
 * @author Jared Erickson
 */
class ColorTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

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

        // Make sure HSL run trips
        Color c1 = new Color("silver")
        def hsl = c1.hsl
        Color c2 = new Color([h: hsl[0], s: hsl[1], l: hsl[2]])
        assertEquals hsl, c2.hsl
    }

    @Test void evaluate() {
        Color color = new Color("black")
        assertEquals "#000000", color.evaluate()
    }

    @Test void getColor() {

        // java.awt.Color
        assertColorsEqual java.awt.Color.BLACK, Color.getColor(java.awt.Color.BLACK)

        // RGB String
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("0,255,0")
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("0:255:0")
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("0 255 0")

        // Hexadecimal
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("#00ff00")
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor("#0f0")

        // CSS Color Names
        assertColorsEqual new java.awt.Color(245,222,179), Color.getColor("wheat")

        // RGB As List
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor([0,255,0])

        // RGB As Map
        assertColorsEqual new java.awt.Color(0,255,0), Color.getColor([r: 0, g: 255, b: 0, a: 125])

        // HSL as Map
        assertColorsEqual new java.awt.Color(255,0,0), Color.getColor([h: 0, s: 1.0, l: 0.5])

        // Null
        assertNull Color.getColor("NOT A COLOR")
        assertNull Color.getColor("0 1 COLOR")
        assertNull Color.getColor("0 1")

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
        Color c1 = Color.getRandom()
        assertNotNull c1
        assertTrue c1 instanceof Color

        Color c2 = Color.getRandom()
        assertNotNull c2
        assertTrue c2 instanceof Color

        assertFalse c1.equals(c2)
    }

    @Test void getRandomPastel() {
        Color c1 = Color.getRandomPastel()
        assertNotNull c1
        assertTrue c1 instanceof Color

        Color c2 = Color.getRandomPastel()
        assertNotNull c2
        assertTrue c2 instanceof Color

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
        colors.each{c -> assertTrue c instanceof Color}
        assertTrue(colors.size() == 5)

        // 11 RdYlGn
        colors = Color.getPaletteColors("RdYlGn", 999)
        assertTrue(colors.size() == 11)

        // Empty
        colors = Color.getPaletteColors("NOT A REAL PALETTE", 5)
        assertTrue(colors.isEmpty())
        colors = Color.getPaletteColors("NOT A REAL PALETTE")
        assertTrue(colors.isEmpty())

        // 5 Greens (wrong case)
        colors = Color.getPaletteColors("greens", 5)
        colors.each{c -> assertTrue c instanceof Color}
        assertTrue(colors.size() == 5)

        // Custom
        colors = Color.getPaletteColors("YellowToRedHeatMap")
        colors.each{c -> assertTrue c instanceof Color}
        assertTrue(colors.size() == 8)

        // Custom with number
        colors = Color.getPaletteColors("BoldLandUse", 4)
        colors.each{c -> assertTrue c instanceof Color}
        assertTrue(colors.size() == 4)
    }

    @Test void getHex() {
        Color c = new Color("red")
        assertEquals "#ff0000", c.hex

        c = new Color([0,255,0])
        assertEquals "#00ff00", c.hex

        c = new Color("blue")
        assertEquals "#0000ff", c.hex

        c = new Color("wheat")
        assertEquals "#f5deb3", c.hex
    }

    @Test void getRgb() {
        Color c = new Color("red")
        def rgb = c.rgb
        assertEquals 255, rgb[0]
        assertEquals 0, rgb[1]
        assertEquals 0, rgb[2]

        c = new Color([0,255,0])
        rgb = c.rgb
        assertEquals 0, rgb[0]
        assertEquals 255, rgb[1]
        assertEquals 0, rgb[2]

        c = new Color("blue")
        rgb = c.rgb
        assertEquals 0, rgb[0]
        assertEquals 0, rgb[1]
        assertEquals 255, rgb[2]

        c = new Color("wheat")
        rgb = c.rgb
        assertEquals 245, rgb[0]
        assertEquals 222, rgb[1]
        assertEquals 179, rgb[2]
    }

    @Test void getHsl() {
        Color c = new Color("red")
        def hsl = c.hsl
        assertEquals 0.0, hsl[0], 0.01
        assertEquals 1.0, hsl[1], 0.01
        assertEquals 0.5, hsl[2], 0.01

        c = new Color([0,255,0])
        hsl = c.hsl
        assertEquals 0.333, hsl[0], 0.01
        assertEquals 1.0, hsl[1], 0.01
        assertEquals 0.5, hsl[2], 0.01

        c = new Color("blue")
        hsl = c.hsl
        assertEquals 0.666,  hsl[0], 0.01
        assertEquals 1.0, hsl[1], 0.01
        assertEquals 0.5, hsl[2], 0.01

        c = new Color("wheat")
        hsl = c.hsl
        assertEquals 0.108, hsl[0], 0.01
        assertEquals 0.767, hsl[1], 0.01
        assertEquals 0.831, hsl[2], 0.01
    }

    @Test void interpolate() {

       Color c = new Color("red")
       List colors = c.interpolate(new Color("blue"), 8)
       assertEquals 8, colors.size()

       c = new Color("white")
       colors = c.interpolate(new Color("blue"), 10)
       assertEquals 10, colors.size()
    }

	@Test void darker() {
		Color c = new Color("red")
		Color darkerColor = c.darker()
		assertTrue darkerColor instanceof Color
		assertEquals("#b20000", darkerColor.hex)
        darkerColor = c.darker(2)
        assertTrue darkerColor instanceof Color
        assertEquals("#7c0000", darkerColor.hex)
        darkerColor = c.darker(3)
        assertTrue darkerColor instanceof Color
        assertEquals("#560000", darkerColor.hex)
	}
	
	@Test void brighter() {
		Color c = new Color([100,0,0])
		Color brighterColor = c.brighter()
		assertTrue brighterColor instanceof Color
		assertEquals("#8e0000", brighterColor.hex)
        brighterColor = c.brighter(2)
        assertTrue brighterColor instanceof Color
        assertEquals("#ca0000", brighterColor.hex)
        brighterColor = c.brighter(3)
        assertTrue brighterColor instanceof Color
        assertEquals("#ff0000", brighterColor.hex)
	}
	
    @Test void drawToImage() {
        def colors = Color.interpolate(new Color("white"), new Color("red"))
        def image = Color.drawToImage(colors)
        File file = folder.newFile("colors.png")
        javax.imageio.ImageIO.write(image, "png", file)
    }

}

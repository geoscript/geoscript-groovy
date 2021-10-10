package geoscript.style.io

import geoscript.style.ColorMap
import geoscript.style.Symbolizer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir


import static org.junit.jupiter.api.Assertions.*

/**
 * The ColorTableReader Unit Test
 * @author Jared Erickson
 */
class ColorTableReaderTest {

    @TempDir
    File folder

    @Test void readColorTableFromString() {

        Reader reader = new ColorTableReader()

        // Semi-colon separated Color values
        String str = """0  255:255:255
2  255:255:0
5  0:255:0
10 0:255:255
15 0:0:255
30 255:0:255
50 255:0:0
90 0:0:0
"""
        Symbolizer sym = reader.read(str)
        assertTrue sym instanceof ColorMap
        ColorMap colorMap = sym as ColorMap
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 8, colorMap.values.size()
        assertEquals 0, colorMap.values[0].quantity as int
        assertEquals "#ffffff", colorMap.values[0].color.hex
        assertEquals 2, colorMap.values[1].quantity as int
        assertEquals "#ffff00", colorMap.values[1].color.hex
        assertEquals 5, colorMap.values[2].quantity as int
        assertEquals "#00ff00", colorMap.values[2].color.hex
        assertEquals 10, colorMap.values[3].quantity as int
        assertEquals "#00ffff", colorMap.values[3].color.hex
        assertEquals 15, colorMap.values[4].quantity as int
        assertEquals "#0000ff", colorMap.values[4].color.hex
        assertEquals 30, colorMap.values[5].quantity as int
        assertEquals "#ff00ff", colorMap.values[5].color.hex
        assertEquals 50, colorMap.values[6].quantity as int
        assertEquals "#ff0000", colorMap.values[6].color.hex
        assertEquals 90, colorMap.values[7].quantity as int
        assertEquals "#000000", colorMap.values[7].color.hex

        // Space delimited color values
        str = """0 46 154 88
1800 251 255 128
2800 224 108 31
3500 200 55 55
4000 215 244 244"""
        sym = reader.read(str)
        assertTrue sym instanceof ColorMap
        colorMap = sym as ColorMap
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 5, colorMap.values.size()
        assertEquals 0, colorMap.values[0].quantity as int
        assertEquals "#2e9a58", colorMap.values[0].color.hex
        assertEquals 1800, colorMap.values[1].quantity as int
        assertEquals "#fbff80", colorMap.values[1].color.hex
        assertEquals 2800, colorMap.values[2].quantity as int
        assertEquals "#e06c1f", colorMap.values[2].color.hex
        assertEquals 3500, colorMap.values[3].quantity as int
        assertEquals "#c83737", colorMap.values[3].color.hex
        assertEquals 4000, colorMap.values[4].quantity as int
        assertEquals "#d7f4f4", colorMap.values[4].color.hex

        // Named colors
        str = """0 white
1 yellow
90 green
180 cyan
270 red
360 yellow"""
        sym = reader.read(str)
        assertTrue sym instanceof ColorMap
        colorMap = sym as ColorMap
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 6, colorMap.values.size()
        assertEquals 0, colorMap.values[0].quantity as int
        assertEquals "#ffffff", colorMap.values[0].color.hex
        assertEquals 1, colorMap.values[1].quantity as int
        assertEquals "#ffff00", colorMap.values[1].color.hex
        assertEquals 90, colorMap.values[2].quantity as int
        assertEquals "#008000", colorMap.values[2].color.hex
        assertEquals 180, colorMap.values[3].quantity as int
        assertEquals "#00ffff", colorMap.values[3].color.hex
        assertEquals 270, colorMap.values[4].quantity as int
        assertEquals "#ff0000", colorMap.values[4].color.hex
        assertEquals 360, colorMap.values[5].quantity as int
        assertEquals "#ffff00", colorMap.values[5].color.hex
    }

    @Test void readColorTableFromFile() {

        Reader reader = new ColorTableReader()

        File file = new File(folder,"colortable.txt")
        file.write("""1 black
2 white
3 red""")
        Symbolizer sym = reader.read(file)
        assertTrue sym instanceof ColorMap
        ColorMap colorMap = sym as ColorMap
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 3, colorMap.values.size()
        assertEquals 1, colorMap.values[0].quantity as int
        assertEquals "#000000", colorMap.values[0].color.hex
        assertEquals 2, colorMap.values[1].quantity as int
        assertEquals "#ffffff", colorMap.values[1].color.hex
        assertEquals 3, colorMap.values[2].quantity as int
        assertEquals "#ff0000", colorMap.values[2].color.hex
    }

    @Test void readColorTableFromInputStream() {

        Reader reader = new ColorTableReader()

        File file = new File(folder,"colortable.txt")
        file.write("""1 black
2 white
3 red""")
        Symbolizer sym = reader.read(new FileInputStream(file))
        assertTrue sym instanceof ColorMap
        ColorMap colorMap = sym as ColorMap
        assertEquals "ramp", colorMap.type
        assertFalse colorMap.extended
        assertEquals 3, colorMap.values.size()
        assertEquals 1, colorMap.values[0].quantity as int
        assertEquals "#000000", colorMap.values[0].color.hex
        assertEquals 2, colorMap.values[1].quantity as int
        assertEquals "#ffffff", colorMap.values[1].color.hex
        assertEquals 3, colorMap.values[2].quantity as int
        assertEquals "#ff0000", colorMap.values[2].color.hex
    }
}
package geoscript.style.io

import geoscript.style.Composite
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class UniqueValuesReaderTest {

    @TempDir
    File folder

    @Test void readEqualsHex() {
        String str = """AHa=#aa0c74
AHat=#b83b1f
AHcf=#964642
AHh=#78092e
AHpe=#78092e
AHt=#5f025a
AHt3=#e76161
Aa1=#fcedcd
Aa2=#94474b
"""
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(str)
        assertEquals 9, symbolizer.parts.size()
        // Fill
        assertEquals("#aa0c74", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#760851", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readCsvRgb() {
        String str = """Unit,R,G,B
AHa,175,0,111
AHat,192,54,22
AHcf,150,70,72
AHh,109,13,60
AHpe,232,226,82
AHt,99,0,95
AHt3,233,94,94
"""
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(str)
        assertEquals 7, symbolizer.parts.size()
        // Fill
        assertEquals("#af006f", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#7a004d", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readCsvColorNames() {
        String str = """AHa,red
AHat,green
AHcf,blue
AHh,orange
AHpe,wheat
AHt,aqua
AHt3,pink
"""
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(str)
        assertEquals 7, symbolizer.parts.size()
        // Fill
        assertEquals("#ff0000", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#b20000", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readEqualsCsvFromFile() {
        String str = """AHa,#aa0c74
AHat,#b83b1f
AHcf,#964642
AHh,#78092e
AHpe,#78092e
AHt,#5f025a
AHt3,#e76161
Aa1,#fcedcd
Aa2,#94474b
"""
        File file = new File(folder,"colors.txt")
        file.text = str
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(file)
        assertEquals 9, symbolizer.parts.size()
        // Fill
        assertEquals("#aa0c74", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#760851", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readEqualsRgbFromFile() {
        String str = """AHa=175,0,111
AHat=192,54,22
AHcf=150,70,72
AHh=109,13,60
AHpe=232,226,82
AHt=99,0,95
AHt3=233,94,94
"""
        File file = new File(folder,"colors.txt")
        file.text = str
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(file)
        assertEquals 7, symbolizer.parts.size()
        // Fill
        assertEquals("#af006f", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#7a004d", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readEqualsColorNamesFromFile() {
        String str = """AHa=red
AHat=green
AHcf=blue
AHh=orange
AHpe=wheat
AHt=aqua
AHt3=pink
"""
        File file = new File(folder,"colors.txt")
        file.text = str
        UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
        Composite symbolizer = reader.read(file)
        assertEquals 7, symbolizer.parts.size()
        // Fill
        assertEquals("#ff0000", symbolizer.parts[0].parts[0].color.hex)
        assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
        // Stroke
        assertEquals("#b20000", symbolizer.parts[0].parts[1].color.hex)
        assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
        assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
    }

    @Test void readEqualsColorNamesFromInputStream() {
        String str = """AHa=red
AHat=green
AHcf=blue
AHh=orange
AHpe=wheat
AHt=aqua
AHt3=pink
"""
        File file = new File(folder,"colors.txt")
        file.text = str
        file.withInputStream { InputStream inputStream ->
            UniqueValuesReader reader = new UniqueValuesReader("UnitType", "Polygon")
            Composite symbolizer = reader.read(inputStream)
            assertEquals 7, symbolizer.parts.size()
            // Fill
            assertEquals("#ff0000", symbolizer.parts[0].parts[0].color.hex)
            assertEquals(1.0, symbolizer.parts[0].parts[0].opacity.value as double, 0.1)
            assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[0].filter.toString())
            // Stroke
            assertEquals("#b20000", symbolizer.parts[0].parts[1].color.hex)
            assertEquals(0.5, symbolizer.parts[0].parts[1].width.value as double, 0.1)
            assertEquals("[ UnitType = AHa ]", symbolizer.parts[0].parts[1].filter.toString())
        }

    }
}

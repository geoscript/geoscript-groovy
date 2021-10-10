package geoscript.style.io

import geoscript.style.Style
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The SLDReader UnitTest
 * @author Jared Erickson
 */
class YSLDReaderTest {

    @Test void readFromFile() {
        File file = new File(getClass().getClassLoader().getResource("polygon.yml").toURI())
        assertNotNull(file)
        YSLDReader reader = new YSLDReader()
        Style style = reader.read(file)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readFromInputStream() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("polygon.yml")
        assertNotNull(inputStream)
        YSLDReader reader = new YSLDReader()
        Style style = reader.read(inputStream)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readFromString() {
        File file = new File(getClass().getClassLoader().getResource("polygon.yml").toURI())
        YSLDReader reader = new YSLDReader()
        Style style = reader.read(file.text)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void reader() {
        Reader reader = Readers.find("ysld")
        assertNotNull reader
        assertTrue reader instanceof YSLDReader
    }
}

package geoscript.style.io

import geoscript.style.Style
import org.junit.Test
import static org.junit.Assert.*

/**
 * The SLDReader UnitTest
 * @author Jared Erickson
 */
class YSLDReaderTestCase {

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

    @Test void readerReadYsldString() {
        Style style = Readers.read("""name: Default Styler
feature-styles:
- name: name
  rules:
  - scale: [min, max]
    symbolizers:
    - polygon:
        fill-color: '#F5DEB3'
    - line:
        stroke-color: '#A52A2A'
        stroke-width: 1
""")
        assertNotNull style
        assertTrue style instanceof YSLDReader.YsldStyle
    }
}

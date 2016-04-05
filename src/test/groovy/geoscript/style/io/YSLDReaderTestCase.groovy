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
        File file = new File(getClass().getClassLoader().getResource("states.yml").toURI())
        assertNotNull(file)
        YSLDReader reader = new YSLDReader()
        Style style = reader.read(file)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readFromInputStream() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("states.yml")
        assertNotNull(inputStream)
        YSLDReader reader = new YSLDReader()
        Style style = reader.read(inputStream)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readFromString() {

        String yaml = """
yaml
        """

        YSLDReader reader = new YSLDReader()
        Style style = reader.read(yaml)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

}

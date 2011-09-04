package geoscript.style.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.style.Style

/**
 * The CSSReader UnitTest
 * @author Jared Erickson
 */
class CSSReaderTestCase {

    @Test void readFromFile() {

        File file = new File(getClass().getClassLoader().getResource("states.css").toURI())
        assertNotNull(file)

        CSSReader reader = new CSSReader()
        Style style = reader.read(file)
        assertNotNull(style)
        assertNotNull(style.style)
    }

    @Test void readFromInputStream() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("states.css")
        assertNotNull(inputStream)

        CSSReader reader = new CSSReader()
        Style style = reader.read(inputStream)
        assertNotNull(style)
        assertNotNull(style.style)
    }

    @Test void readFromString() {

        String css = """
            states {
               fill-color: 'wheat';
               stroke-color: 'steelblue';
            }
        """

        CSSReader reader = new CSSReader()
        Style style = reader.read(css)
        assertNotNull(style)
        assertNotNull(style.style)
    }

}

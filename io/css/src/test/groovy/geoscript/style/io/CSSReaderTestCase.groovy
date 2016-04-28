package geoscript.style.io

import geoscript.style.Style
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

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
        assertNotNull(style.gtStyle)
    }

    @Test void readFromInputStream() {

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("states.css")
        assertNotNull(inputStream)

        CSSReader reader = new CSSReader()
        Style style = reader.read(inputStream)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readFromString() {

        String css = """
            states {
              fill: #E6E6E6;
              fill-opacity: 0.5;
              stroke: #4C4C4C;
              stroke-width: 0.1;
            }
        """

        CSSReader reader = new CSSReader()
        Style style = reader.read(css)
        assertNotNull(style)
        assertNotNull(style.gtStyle)
    }

    @Test void readerReadCssString() {
        Style style = Readers.read("""
            states {
              fill: #E6E6E6;
              fill-opacity: 0.5;
              stroke: #4C4C4C;
              stroke-width: 0.1;
            }
        """)
        assertNotNull style
        assertTrue style instanceof CSSReader.CSSStyle
    }
}

package geoscript.style.io

import geoscript.style.Style
import org.junit.Test
import static org.junit.Assert.*

/**
 * The Style Readers Unit Test
 * @author Jared Erickson
 */
class ReadersTestCase {

    @Test void list() {
        List<Reader> readers = Readers.list()
        assertNotNull readers
        assertTrue readers.size() > 0
    }

    @Test void find() {
        Reader reader = Readers.find("sld")
        assertNotNull reader

        reader = Readers.find("asdf")
        assertNull reader
    }

    @Test void readSimpleStyleString() {
        Style style = Readers.read("fill=#555555 fill-opacity=0.6 stroke=#555555 stroke-width=0.5")
        assertNotNull style
        assertEquals style.toString(), "Composite (Fill(color = #555555, opacity = 0.6), Stroke(color = #555555, width = 0.5))"
    }

    @Test void readColorTableStyleString() {
        Style style = Readers.read("""0  255:255:255
2  255:255:0
5  0:255:0
10 0:255:255
15 0:0:255
30 255:0:255
50 255:0:0
90 0:0:0
""")
        assertNotNull style
        assertEquals style.toString(), "ColorMap(values = [[quantity:0, color:#ffffff], [quantity:2, color:#ffff00], " +
                "[quantity:5, color:#00ff00], [quantity:10, color:#00ffff], [quantity:15, color:#0000ff], " +
                "[quantity:30, color:#ff00ff], [quantity:50, color:#ff0000], [quantity:90, color:#000000]], " +
                "type = ramp, extended = false)"
    }

}

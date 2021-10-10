package geoscript.style.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The Style Writers Unit Test
 * @author Jared Erickson
 */
class WritersTest {

    @Test void list() {
        List<Writer> writers = Writers.list()
        assertNotNull writers
        assertTrue writers.size() > 0
    }

    @Test void find() {
        Writer writer = Writers.find("sld")
        assertNotNull writer

        writer = Writers.find("asdf")
        assertNull writer
    }
}

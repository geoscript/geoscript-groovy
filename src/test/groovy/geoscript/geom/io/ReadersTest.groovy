package geoscript.geom.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The Readers Unit Test
 * @author Jared Erickson
 */
class ReadersTest {

    @Test void list() {
        List<Reader> readers = Readers.list()
        assertNotNull readers
        assertTrue readers.size() > 0
    }

    @Test void find() {
        Reader reader = Readers.find("wkt")
        assertNotNull reader

        reader = Readers.find("wkb")
        assertNotNull reader

        reader = Readers.find("twkb")
        assertNotNull reader

        reader = Readers.find("asdf")
        assertNull reader
    }
}

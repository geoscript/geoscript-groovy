package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Layer Readers Unit Test.
 * @author Jared Erickson
 */
class ReadersTestCase {

    @Test void list() {
        List<Reader> readers = Readers.list()
        assertNotNull readers
    }

    @Test void find() {
        Reader reader = Readers.find("asdf")
        assertNull reader
    }
}

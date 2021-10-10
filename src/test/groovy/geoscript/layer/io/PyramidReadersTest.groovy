package geoscript.layer.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The PyramidReaders Unit Test.
 * @author Jared Erickson
 */
class PyramidReadersTest {

    @Test void list() {
        List<PyramidReader> readers = PyramidReaders.list()
        assertNotNull readers
        assertTrue readers.size() > 0
    }

    @Test void find() {
        PyramidReader reader = PyramidReaders.find("csv")
        assertNotNull reader

        reader = PyramidReaders.find("asdf")
        assertNull reader
    }
}

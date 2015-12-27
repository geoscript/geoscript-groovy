package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*

/**
 * The PyramidReaders Unit Test.
 * @author Jared Erickson
 */
class PyramidReadersTestCase {

    @Test void list() {
        List<PyramidReader> readers = PyramidReaders.list()
        assertNotNull readers
    }

    @Test void find() {
        PyramidReader reader = PyramidReaders.find("asdf")
        assertNull reader
    }
}

package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*

/**
 * The PyramidWriters Unit Test.
 * @author Jared Erickson
 */
class PyramidWritersTestCase {

    @Test void list() {
        List<PyramidWriter> writers = PyramidWriters.list()
        assertNotNull writers
    }

    @Test void find() {
        PyramidWriter writer = PyramidWriters.find("asdf")
        assertNull writer
    }
}

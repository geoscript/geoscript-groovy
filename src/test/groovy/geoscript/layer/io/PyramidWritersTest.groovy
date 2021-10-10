package geoscript.layer.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The PyramidWriters Unit Test.
 * @author Jared Erickson
 */
class PyramidWritersTest {

    @Test void list() {
        List<PyramidWriter> writers = PyramidWriters.list()
        assertNotNull writers
        assertTrue writers.size() > 0
    }

    @Test void find() {
        PyramidWriter writer = PyramidWriters.find("csv")
        assertNotNull writer

        writer = PyramidWriters.find("asdf")
        assertNull writer
    }
}

package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Layer Writers Unit Test.
 * @author Jared Erickson
 */
class WritersTestCase {

    @Test void list() {
        List<Writer> writers = Writers.list()
        assertNotNull writers
        assertTrue writers.size() > 0
    }

    @Test void find() {
        Writer writer = Writers.find("csv")
        assertNotNull writer

        writer = Writers.find("asdf")
        assertNull writer
    }
}

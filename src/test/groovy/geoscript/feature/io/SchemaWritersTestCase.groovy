package geoscript.feature.io

import org.junit.Test
import static org.junit.Assert.*

/**
 * The SchemaWriters Unit Test
 * @author Jared Erickson
 */
class SchemaWritersTestCase {

    @Test void list() {
        List<SchemaWriter> writers = SchemaWriters.list()
        assertNotNull writers
        assertTrue writers.size() > 0
    }

    @Test void find() {
        SchemaWriter writer = SchemaWriters.find("json")
        assertNotNull writer

        writer = SchemaWriters.find("string")
        assertNotNull writer

        writer = SchemaWriters.find("asdf")
        assertNull writer
    }

}

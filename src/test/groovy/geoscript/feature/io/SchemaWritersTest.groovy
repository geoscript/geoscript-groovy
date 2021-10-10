package geoscript.feature.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The SchemaWriters Unit Test
 * @author Jared Erickson
 */
class SchemaWritersTest {

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

        writer = SchemaWriters.find("xml")
        assertNotNull writer

        writer = SchemaWriters.find("asdf")
        assertNull writer
    }

}

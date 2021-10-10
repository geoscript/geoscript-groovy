package geoscript.feature.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The SchemaReaders Unit Test
 * @author Jared Erickson
 */
class SchemaReadersTest {

    @Test void list() {
        List<SchemaReader> readers = SchemaReaders.list()
        assertNotNull readers
        assertTrue readers.size() > 0
    }

    @Test void find() {
        SchemaReader reader = SchemaReaders.find("json")
        assertNotNull reader

        reader = SchemaReaders.find("string")
        assertNotNull reader

        reader = SchemaReaders.find("xml")
        assertNotNull reader

        reader = SchemaReaders.find("asdf")
        assertNull reader
    }

}

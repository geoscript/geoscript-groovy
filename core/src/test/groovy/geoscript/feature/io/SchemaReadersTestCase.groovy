package geoscript.feature.io

import org.junit.Test

import static org.junit.Assert.*

/**
 * The SchemaReaders Unit Test
 * @author Jared Erickson
 */
class SchemaReadersTestCase {

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

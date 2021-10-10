package geoscript.render.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class MapReadersTest {

    @Test void find() {
        assertNotNull(MapReaders.find("xml"))
        assertNotNull(MapReaders.find("json"))
        assertNull(MapReaders.find("asdf"))
    }

    @Test void list() {
        List<MapReader> mapReaders = MapReaders.list()
        assertFalse(mapReaders.isEmpty())
    }

}

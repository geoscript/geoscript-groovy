package geoscript.render.io

import org.junit.Test
import static org.junit.Assert.*

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

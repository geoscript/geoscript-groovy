package geoscript.carto.io

import org.junit.Test
import static org.junit.Assert.*

class CartoReadersTest {

    @Test void find() {
        assertNotNull(CartoReaders.find("xml"))
        assertNotNull(CartoReaders.find("json"))
        assertNull(CartoReaders.find("asdf"))
    }

    @Test void list() {
        List<CartoReader> mapReaders = CartoReaders.list()
        assertFalse(mapReaders.isEmpty())
    }

}

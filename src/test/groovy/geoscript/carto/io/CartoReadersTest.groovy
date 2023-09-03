package geoscript.carto.io

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class CartoReadersTest {

    @Test void find() {
        assertNotNull(CartoReaders.find("xml"))
        assertNotNull(CartoReaders.find("json"))
        assertNotNull(CartoReaders.find("yaml"))
        assertNull(CartoReaders.find("asdf"))
    }

    @Test void list() {
        List<CartoReader> mapReaders = CartoReaders.list()
        assertFalse(mapReaders.isEmpty())
    }

}

package geoscript.carto

import org.junit.Test
import static org.junit.Assert.*

class CartoFactoriesTest {

    @Test
    void list() {
        assertFalse(CartoFactories.list().isEmpty())
    }

    @Test
    void findByName() {
        assertNotNull(CartoFactories.findByName("png"))
        assertNotNull(CartoFactories.findByName("jpeg"))
        assertNotNull(CartoFactories.findByName("pdf"))
        assertNotNull(CartoFactories.findByName("svg"))
        assertNull(CartoFactories.findByName("doc"))
    }

    @Test
    void findByMimeType() {
        assertNotNull(CartoFactories.findByMimeType("image/png"))
        assertNotNull(CartoFactories.findByMimeType("image/jpeg"))
        assertNotNull(CartoFactories.findByMimeType("application/pdf"))
        assertNotNull(CartoFactories.findByMimeType("application/svg"))
        assertNull(CartoFactories.findByMimeType("text/plain"))
    }
}

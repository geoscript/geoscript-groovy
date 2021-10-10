package geoscript.layer

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The TileLayerFactories Unit Test.
 * @author Jared Erickson
 */
class TileLayerFactoriesTest {

    @Test void list() {
        List<TileLayerFactory> factories = TileLayerFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

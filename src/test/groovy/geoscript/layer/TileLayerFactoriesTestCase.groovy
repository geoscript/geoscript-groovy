package geoscript.layer

import org.junit.Test
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * The TileLayerFactories Unit Test.
 * @author Jared Erickson
 */
class TileLayerFactoriesTestCase {

    @Test void list() {
        List<TileLayerFactory> factories = TileLayerFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

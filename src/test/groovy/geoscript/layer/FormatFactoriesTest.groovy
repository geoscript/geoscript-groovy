package geoscript.layer

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The FormatFactories Unit Test.
 * @author Jared Erickson
 */
class FormatFactoriesTest {

    @Test void list() {
        List<FormatFactory> factories = FormatFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

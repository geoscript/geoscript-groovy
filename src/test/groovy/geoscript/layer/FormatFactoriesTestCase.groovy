package geoscript.layer

import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * The FormatFactories Unit Test.
 * @author Jared Erickson
 */
class FormatFactoriesTestCase {

    @Test void list() {
        List<FormatFactory> factories = FormatFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

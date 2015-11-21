package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

/**
 * The WorkspaceFactories Unit Test
 * @author Jared Erickson
 */
class WorkspaceFactoriesTestCase {

    @Test void list() {
        List<WorkspaceFactory> factories = WorkspaceFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

package geoscript.workspace

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertNotNull
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The WorkspaceFactories Unit Test
 * @author Jared Erickson
 */
class WorkspaceFactoriesTest {

    @Test void list() {
        List<WorkspaceFactory> factories = WorkspaceFactories.list()
        assertNotNull factories
        assertTrue factories.size() > 0
    }

}

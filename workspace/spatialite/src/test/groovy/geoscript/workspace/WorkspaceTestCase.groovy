package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test void getParametersFromString() {
        Map params = Workspace.getParametersFromString("dbtype=spatialite database=layers.sqlite")
        assertEquals(params['dbtype'], "spatialite")
        assertEquals(params['database'], "layers.sqlite")

        params = Workspace.getParametersFromString("layers.sqlite")
        assertEquals(params['dbtype'], "spatialite")
        assertTrue(params['database'].toString().endsWith("layers.sqlite"))
    }

}
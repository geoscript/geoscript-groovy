package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test void getParametersFromString() {
        Map params = Workspace.getParametersFromString("dbtype=geopkg database=layers.gpkg")
        assertEquals(params['dbtype'], "geopkg")
        assertEquals(params['database'], "layers.gpkg")

        params = Workspace.getParametersFromString("layers.gpkg")
        assertEquals(params['dbtype'], "geopkg")
        assertTrue(params['database'].toString().endsWith("layers.gpkg"))
    }

}
package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test void getParametersFromString() {
        // PostGIS
        Map params = Workspace.getParametersFromString("dbtype=postgis database=postgres host=localhost port=5432");
        assertEquals(params['dbtype'], "postgis")
        assertEquals(params['database'], "postgres")
        assertEquals(params['host'], "localhost")
        assertEquals(params['port'], "5432")
    }

}

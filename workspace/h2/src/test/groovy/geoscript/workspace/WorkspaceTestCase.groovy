package geoscript.workspace

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class WorkspaceTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void constructorWithMapOfParams() {
        Workspace h2 = new Workspace(["dbtype": "h2", "database": folder.newFile("roads.db").absolutePath])
        assertNotNull(h2.ds)
        assertEquals("org.geotools.jdbc.JDBCDataStore", h2.format)
        h2.close()
    }

    @Test void constructorWithParamString() {
        Workspace h2 = new Workspace("dbtype=h2 database=" + folder.newFile("roads.db").absolutePath)
        assertNotNull(h2.ds)
        assertEquals("org.geotools.jdbc.JDBCDataStore", h2.format)
        h2.close()
    }

    @Test void getParametersFromString() {
        Map params = Workspace.getParametersFromString("dbtype=h2 database='C:\\My Data\\my.db'");
        assertEquals(params['dbtype'], "h2")
        assertEquals(params['database'], "C:\\My Data\\my.db")

        params = Workspace.getParametersFromString("C:\\My Data\\my.db");
        assertEquals(params['dbtype'], "h2")
        assertTrue params['database'].toString().endsWith("my.db")
    }

    @Test void getWorkspaceParameters() {
        List params = Workspace.getWorkspaceParameters("H2")
        assertTrue params.size() > 0
        assertNotNull params.find { p ->
            p.key.equals("dbtype") ? p : null
        }
        assertNotNull params.find { p ->
            p.key.equals("database") ? p : null
        }
    }

    @Test void getWorkspaceWithParams() {
        Workspace h2 = Workspace.getWorkspace(["dbtype": "h2", "database": folder.newFile("roads.db").absolutePath])
        assertNotNull(h2.ds)
        assertTrue(h2 instanceof H2)
        h2.close()
    }

    @Test void getWorkspaceWithParamString() {
        Workspace h2 = Workspace.getWorkspace("dbtype=h2 database=" + folder.newFile("roads.db").absolutePath)
        assertNotNull(h2.ds)
        assertTrue(h2 instanceof H2)
        h2.close()
    }

    @Test void withWorkspace() {
        Workspace.withWorkspace(["dbtype": "h2", "database": folder.newFile("roads.db").absolutePath]) { Workspace w ->
            assertNotNull w
            assertEquals "H2", w.format
        }
        Workspace.withWorkspace("dbtype=h2 database=" + folder.newFile("roads.db").absolutePath) { Workspace w ->
            assertNotNull w
            assertEquals "H2", w.format
        }
        Workspace.withWorkspace(new H2(folder.newFile("roads.db").absolutePath)) { Workspace w ->
            assertNotNull w
            assertEquals "H2", w.format
        }
    }

}
package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Field
import geoscript.layer.Layer

/**
 * The Workspace Unit Test
 * @author Jared Erickson
 */
class WorkspaceTestCase {

    @Test void constructorWithMapOfParams() {
        // H2
        Workspace h2 = new Workspace(["dbtype": "h2", "database": File.createTempFile("roads",".db").absolutePath])
        assertNotNull(h2.ds)
        assertEquals("org.geotools.jdbc.JDBCDataStore", h2.format)

        // Shapefile
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace(["url": url])
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.indexed.IndexedShapefileDataStore", shp.format)
    }

    @Test void constructorWithParamString() {
        // H2
        Workspace h2 = new Workspace("dbtype=h2 database=" + File.createTempFile("roads",".db").absolutePath)
        assertNotNull(h2.ds)
        assertEquals("org.geotools.jdbc.JDBCDataStore", h2.format)

        // Shapefile
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace("url='${url}' 'create spatial index'=true")
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.indexed.IndexedShapefileDataStore", shp.format)

        Workspace shp2 = new Workspace("${url}")
        assertNotNull(shp2.ds)
        assertEquals("org.geotools.data.shapefile.indexed.IndexedShapefileDataStore", shp2.format)

        // Properties
        File propFile = new File(getClass().getClassLoader().getResource("points.properties").file)
        Workspace prop = new Workspace("directory='${propFile}'")
        assertNotNull(prop.ds)
        assertEquals("org.geotools.data.property.PropertyDataStore", prop.format)
    }

    @Test void getWorkspaceNames() {
        List names = Workspace.workspaceNames
        assertTrue names.size() > 0
        assertTrue names.contains("Shapefile")
    }

    @Test void getWorkspaceParameters() {
        // H2
        List params = Workspace.getWorkspaceParameters("H2")
        assertTrue params.size() > 0
        assertNotNull params.find {p ->
            p.key.equals("dbtype") ? p : null
        }
        assertNotNull params.find {p ->
            p.key.equals("database") ? p : null
        }

        // Shapefile
        params = Workspace.getWorkspaceParameters("Shapefile")
        assertTrue params.size() > 0
        assertNotNull params.find {p ->
            p.key.equals("url") ? p : null
        }
        assertNotNull params.find {p ->
            p.key.equals("create spatial index") ? p : null
        }
    }

}

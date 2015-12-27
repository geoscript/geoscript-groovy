package geoscript.workspace

import org.geotools.data.shapefile.ShapefileDataStore
import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test
    void constructorWithMapOfParams() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace(["url": url])
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.ShapefileDataStore", shp.format)
    }

    @Test
    void constructorWithParamString() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace("url='${url}' 'create spatial index'=true")
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.ShapefileDataStore", shp.format)

        Workspace shp2 = new Workspace("${url}")
        assertNotNull(shp2.ds)
        assertEquals("org.geotools.data.directory.DirectoryDataStore", shp2.format)

        Workspace shp3 = new Workspace("${url.file}")
        assertNotNull(shp3.ds)
        assertEquals("org.geotools.data.directory.DirectoryDataStore", shp3.format)

        Workspace dir = new Workspace("${new File(url.file).getAbsoluteFile().getParent()}")
        assertNotNull(dir.ds)
        assertEquals("org.geotools.data.directory.DirectoryDataStore", dir.format)
    }

    @Test
    void getParametersFromString() {
        // Shapefile
        Map params = Workspace.getParametersFromString("/my/states.shp")
        assertTrue(params.containsKey("url"))
        assertTrue(params["url"] instanceof URL)
        assertTrue(params["url"].toString().endsWith("my"))

        params = Workspace.getParametersFromString("url='/my/states.shp' 'create spatial index'=true")
        assertTrue(params.containsKey("url"))
        assertTrue(params["url"] instanceof URL)
        assertTrue(params["url"].toString().endsWith("my/states.shp"))
        assertEquals(params['create spatial index'], "true")
    }

    @Test
    void getWorkspaceWithParams() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = Workspace.getWorkspace(["url": url])
        assertNotNull(shp.ds)
        assertTrue(shp instanceof Directory)
    }

    @Test
    void getWorkspaceWithParamString() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = Workspace.getWorkspace("url='${url}' 'create spatial index'=true")
        assertNotNull(shp.ds)
        assertTrue(shp instanceof Directory)

        Workspace shp2 = Workspace.getWorkspace("${url}")
        assertNotNull(shp2.ds)
        assertTrue(shp instanceof Directory)

        Workspace shp3 = Workspace.getWorkspace("${url.file}")
        assertNotNull(shp3.ds)
        assertTrue(shp instanceof Directory)

        Workspace dir = Workspace.getWorkspace("${new File(url.file).getAbsoluteFile().getParent()}")
        assertNotNull(dir.ds)
        assertTrue(shp instanceof Directory)
    }
}
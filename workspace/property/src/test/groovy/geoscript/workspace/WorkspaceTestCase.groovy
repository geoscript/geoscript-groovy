package geoscript.workspace

import org.junit.Test
import static org.junit.Assert.*

class WorkspaceTestCase {

    @Test void constructorWithParamString() {
        File propFile = new File(getClass().getClassLoader().getResource("points.properties").file)
        Workspace prop = new Workspace("directory='${propFile}'")
        assertNotNull(prop.ds)
        assertEquals("org.geotools.data.property.PropertyDataStore", prop.format)

        Workspace prop2 = new Workspace(propFile.absolutePath)
        assertNotNull(prop2.ds)
        assertEquals("org.geotools.data.property.PropertyDataStore", prop2.format)

        Workspace prop3 = new Workspace(new File(propFile.parentFile, "asdfasdfas.properties").absolutePath)
        assertNotNull(prop3.ds)
        assertEquals("org.geotools.data.property.PropertyDataStore", prop3.format)
    }

    @Test void getParametersFromString() {
        Map params = Workspace.getParametersFromString("directory=/my/states.properties")
        assertTrue(params.containsKey("directory"))
        assertTrue(params['directory'].toString().endsWith("my/states.properties"))

        params = Workspace.getParametersFromString("directory=/my/propertyfiles")
        assertTrue(params.containsKey("directory"))
        assertTrue(params['directory'].toString().endsWith("my/propertyfiles"))

        params = Workspace.getParametersFromString("/my/states.properties")
        assertTrue(params.containsKey("directory"))
        assertTrue(params['directory'].toString().endsWith("my"))
    }

    @Test void getWorkspaceWithParamString() {
        File propFile = new File(getClass().getClassLoader().getResource("points.properties").file)
        Workspace prop = Workspace.getWorkspace("directory='${propFile}'")
        assertNotNull(prop.ds)
        assertTrue(prop instanceof Property)

        Workspace prop2 = Workspace.getWorkspace(propFile.absolutePath)
        assertNotNull(prop2.ds)
        assertTrue(prop instanceof Property)

        Workspace prop3 = Workspace.getWorkspace(new File(propFile.parentFile, "asdfasdfas.properties").absolutePath)
        assertNotNull(prop3.ds)
        assertTrue(prop instanceof Property)
    }

}
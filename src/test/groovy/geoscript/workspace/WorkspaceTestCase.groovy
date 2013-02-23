package geoscript.workspace

import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*
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
        assertEquals("org.geotools.data.directory.DirectoryDataStore", shp2.format)

        Workspace shp3 = new Workspace("${url.file}")
        assertNotNull(shp3.ds)
        assertEquals("org.geotools.data.directory.DirectoryDataStore", shp3.format)

        Workspace dir = new Workspace("${new File(url.file).getAbsoluteFile().getParent()}")
        assertNotNull(dir.ds)
        assertEquals("org.geotools.data.directory.DirectoryDataStore", dir.format)

        // Properties
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

    @Test void add() {
        Layer layer1 = new Memory().create(new Schema("points", [["the_geom", "Point", "EPSG:4326"],["name","String"]]))
        layer1.add([new Point(1,1),"point1"])
        layer1.add([new Point(2,2),"point2"])
        layer1.add([new Point(3,3),"point3"])

        File file = new File(System.getProperty("java.io.tmpdir"))
        Directory dir = new Directory(file)
        Layer layer2 = dir.add(layer1)
        assertTrue(new File(file,"points.shp").exists())
        assertEquals 3, layer2.count
    }

}

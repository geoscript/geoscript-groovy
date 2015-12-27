package geoscript.workspace

import geoscript.feature.Schema
import geoscript.geom.Point
import org.geotools.data.DataUtilities
import org.geotools.data.shapefile.ShapefileDataStore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer

/**
 * The Workspace Unit Test
 * @author Jared Erickson
 */
class WorkspaceTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void constructorWithMapOfParams() {
        // Shapefile
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace(["url": url])
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.ShapefileDataStore", shp.format)

        // Memory
        Workspace w = new Workspace(["type": "memory"])
        assertNotNull(w.ds)
        assertEquals("org.geotools.data.memory.MemoryDataStore", w.format)
    }

    @Test void constructorWithParamString() {
        // Shapefile
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = new Workspace("url='${url}' 'create spatial index'=true")
        assertNotNull(shp.ds)
        assertEquals("org.geotools.data.shapefile.ShapefileDataStore", shp.format)

        // Memory
        Workspace w = new Workspace("memory")
        assertNotNull(w.ds)
        assertEquals("org.geotools.data.memory.MemoryDataStore", w.format)
    }

    @Test void getParametersFromString() {
        // PostGIS
        Map params = Workspace.getParametersFromString("dbtype=postgis database=postgres host=localhost port=5432");
        assertEquals(params['dbtype'], "postgis")
        assertEquals(params['database'], "postgres")
        assertEquals(params['host'], "localhost")
        assertEquals(params['port'], "5432")

        // Neo4j
        params = Workspace.getParametersFromString("'The directory path of the neo4j database'=/opt/neo4j/data/graph.db")
        assertEquals(params['The directory path of the neo4j database'], "/opt/neo4j/data/graph.db")

        // H2
        params = Workspace.getParametersFromString("dbtype=h2 database='C:\\My Data\\my.db'");
        assertEquals(params['dbtype'], "h2")
        assertEquals(params['database'], "C:\\My Data\\my.db")

        // Shapefile
        params = Workspace.getParametersFromString("url='/my/states.shp' 'create spatial index'=true")
        assertTrue(params.containsKey("url"))
        assertTrue(params["url"] instanceof URL)
        assertTrue(params["url"].toString().endsWith("my/states.shp"))
        assertEquals(params['create spatial index'], "true")

        // Property
        params = Workspace.getParametersFromString("directory=/my/states.properties")
        assertTrue(params.containsKey("directory"))
        assertTrue(params['directory'].toString().endsWith("my/states.properties"))

        params = Workspace.getParametersFromString("directory=/my/propertyfiles")
        assertTrue(params.containsKey("directory"))
        assertTrue(params['directory'].toString().endsWith("my/propertyfiles"))

        // GeoPackage
        params = Workspace.getParametersFromString("dbtype=geopkg database=layers.gpkg")
        assertEquals(params['dbtype'], "geopkg")
        assertEquals(params['database'], "layers.gpkg")

        // Spatialite
        params = Workspace.getParametersFromString("dbtype=spatialite database=layers.sqlite")
        assertEquals(params['dbtype'], "spatialite")
        assertEquals(params['database'], "layers.sqlite")

        // Memory
        params = Workspace.getParametersFromString("memory")
        assertTrue params.containsKey("type")
        assertEquals "memory", params.type
    }

    @Test void getWorkspaceNames() {
        List names = Workspace.workspaceNames
        assertTrue names.size() > 0
        assertTrue names.contains("Shapefile")
    }

    @Test void getWorkspaceParameters() {
        List params = Workspace.getWorkspaceParameters("Shapefile")
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

        File file = folder.newFolder("points")
        Workspace w = Workspace.getWorkspace([url: DataUtilities.fileToURL(file)])
        Layer layer2 = w.add(layer1)
        assertTrue(new File(file,"points.shp").exists())
        assertEquals 3, layer2.count
    }

    @Test void has() {
        Workspace workspace = new Memory()
        assertFalse workspace.has("points")
        workspace.create("points", [["the_geom", "Point", "EPSG:4326"]])
        assertTrue workspace.has("points")
    }

    @Test void getWorkspaceWithParams() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = Workspace.getWorkspace(["url": url])
        assertNotNull(shp.ds)
        assertTrue(shp instanceof Workspace)
        assertTrue(shp.ds instanceof ShapefileDataStore)
    }

    @Test void getWorkspaceWithParamString() {
        // Shapefile
        URL url = getClass().getClassLoader().getResource("states.shp")
        Workspace shp = Workspace.getWorkspace("url='${url}' 'create spatial index'=true")
        assertNotNull(shp.ds)
        assertTrue(shp instanceof Workspace)
        assertTrue(shp.ds instanceof ShapefileDataStore)

        // Memory
        Workspace mem = Workspace.getWorkspace("memory")
        assertNotNull(mem.ds)
        assertTrue(mem instanceof Memory)
        mem = Workspace.getWorkspace([type: "memory"])
        assertNotNull(mem.ds)
        assertTrue(mem instanceof Memory)
    }

    @Test(expected=IllegalArgumentException) void badWorkspaceString() {
        Workspace w = Workspace.getWorkspace("BAD_INPUT")
    }

    @Test void withWorkspace() {
        Workspace.withWorkspace(["type": "memory"]) { Workspace w ->
            assertNotNull w
            assertEquals "Memory", w.format
        }
    }
}

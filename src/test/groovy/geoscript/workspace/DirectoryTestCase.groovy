package geoscript.workspace

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer
import geoscript.feature.Field
import geoscript.feature.Schema

/**
 * The Directory UnitTest
 */
class DirectoryTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void constructors() {

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        assertNotNull(file)

        Directory dir = new Directory(file)
        assertNotNull(dir)
        assertEquals "Directory", dir.format
        assertEquals "Directory[${file}]".toString(), dir.toString()

        Directory dir2 = new Directory(file.absolutePath)
        assertNotNull(dir2)
        assertEquals "Directory", dir2.format
        assertEquals "Directory[${file}]".toString(), dir2.toString()

    }

    @Test void getNames() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        assertNotNull(file)
        Directory dir = new Directory(file)
        assertNotNull(dir)
        assertEquals 2, dir.names.size()
        assertEquals "[points, states]", dir.names.toString()
        assertTrue dir.names[0] instanceof String
    }

    @Test void getLayers() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        assertNotNull(file)
        Directory dir = new Directory(file)
        assertNotNull(dir)
        assertEquals 2, dir.layers.size()
        assertEquals "[points, states]", dir.layers.toString()
        assertTrue dir.layers[0] instanceof Layer
    }
    
    @Test void get() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        assertNotNull(file)
        Directory dir = new Directory(file)
        assertNotNull(dir)

        Layer layer = dir.get("states")
        assertNotNull(layer)
        assertEquals "states", layer.name

        layer = dir.get("states.shp")
        assertNotNull(layer)
        assertEquals "states", layer.name
    }

    @Test void getAt() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        assertNotNull(file)
        Directory dir = new Directory(file)
        assertNotNull(dir)

        Layer layer = dir["states"]
        assertNotNull(layer)
        assertEquals "states", layer.name
    }

    @Test void create() {
        File file = folder.newFolder("points")
        Directory dir = new Directory(file)
        Layer layer = dir.create("points", [new Field("geom","Point","EPSG:4326")])
        assertNotNull(layer)
        assertTrue(new File(file,"points.shp").exists())

        Layer layer2 = dir.create(new Schema("lines", [new Field("geom","Point","EPSG:4326")]))
        assertNotNull(layer2)
        assertTrue(new File(file,"lines.shp").exists())
    }

    @Test void add() {

        File file1 = new File(getClass().getClassLoader().getResource("states.shp").toURI()).parentFile
        Directory dir1 = new Directory(file1)
        Layer layer1 = dir1.get("states")

        File file2 = folder.newFolder("countries")
        Directory dir2 = new Directory(file2)
        Layer layer2 = dir2.add(layer1, "countries")
        assertTrue(new File(file2,"countries.shp").exists())

        dir2.add(layer1)
        assertTrue(new File(file2,"states.shp").exists())

    }

    @Test void remove() {
        File directory = folder.newFolder("shps")
        Workspace workspace = new Directory(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.create("lines",[new Field("geom","LineString","EPSG:4326")])
        workspace.create("polygons",[new Field("geom","Polygon","EPSG:4326")])
        assertTrue workspace.has("points")
        assertTrue workspace.has("lines")
        assertTrue workspace.has("polygons")
        workspace.remove("points")
        workspace.remove(workspace.get("lines"))
        workspace.remove(workspace.get("polygons"))
        assertFalse workspace.has("points")
        assertFalse workspace.has("lines")
        assertFalse workspace.has("polygons")
    }

}


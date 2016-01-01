package geoscript.workspace

import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The Geobuf Workspace Unit Test
 * @author Jared Erickson
 */
class GeobufTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test void add() {

        // Create a new Geobuf Workspace
        File directory = temporaryFolder.newFolder("geobufs")
        Geobuf geobuf = new Geobuf(directory)

        // Create an in memory Layer
        Memory memory = new Memory()
        Layer memoryLayer = memory.create('locations',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(memoryLayer)
        memoryLayer.add([new Point(1,1), "Seattle"])
        memoryLayer.add([new Point(2,2), "Portland"])
        memoryLayer.add([new Point(3,3), "Tacoma"])
        assertEquals 3, memoryLayer.count()

        // And add it to Geobuf
        assertNotNull geobuf.add(memoryLayer)
        assertTrue new File(directory, "locations.pbf").exists()

        // Make sure we can read it from Geobuf
        Layer geobufLayer = geobuf.get("locations")
        assertEquals 3, memoryLayer.count
    }

    @Test void remove() {
        File directory = temporaryFolder.newFolder("geobufs")
        Workspace workspace = new Geobuf(directory)
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

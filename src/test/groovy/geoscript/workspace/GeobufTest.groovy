package geoscript.workspace

import geoscript.FileUtil
import geoscript.feature.Field
import geoscript.geom.Point
import geoscript.layer.Layer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import static org.junit.jupiter.api.Assertions.*

/**
 * The Geobuf Workspace Unit Test
 * @author Jared Erickson
 */
class GeobufTest {

    @TempDir
    File folder

    @Test void add() {

        // Create a new Geobuf Workspace
        File directory = FileUtil.createDir(folder,"geobufs")
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
        File directory = FileUtil.createDir(folder,"geobufs")
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

    @Test void getWorkspace() {
        Workspace w = Workspace.getWorkspace("layers.pbf")
        assertNotNull w
        assertEquals("Geobuf", w.format)
    }

    @Test void getWorkspaceFromString() {
        File directory = FileUtil.createDir(folder,"geobufs")
        Workspace workspace = new Geobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        Geobuf geobuf = Workspace.getWorkspace("type=geobuf file=${directory}")
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace("type=geobuf file=${new File(directory, 'points.pbf')}")
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
    }

    @Test void getWorkspaceFromMap() {
        File directory = FileUtil.createDir(folder,"geobufs")
        Workspace workspace = new Geobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.close()
        Geobuf geobuf = Workspace.getWorkspace([type: 'geobuf', file: directory])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'geobuf', file: new File(directory, 'points.pbf')])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'geobuf', file: directory.absolutePath])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'geobuf', file: new File(directory, 'points.pbf').absolutePath])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
    }

}

package geoscript.workspace

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.geom.LineString
import geoscript.geom.Point
import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The FlatGeobuf Workspace Unit Test
 * @author Jared Erickson
 */
class FlatGeobufTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test void add() {

        // Create a new FlatGeobuf Workspace
        File directory = temporaryFolder.newFolder("flatgeobufs")
        FlatGeobuf geobuf = new FlatGeobuf(directory)

        // Create an in memory Layer
        Memory memory = new Memory()
        Layer memoryLayer = memory.create('locations',[new Field("geom", "Point"), new Field("name", "String")])
        assertNotNull(memoryLayer)
        memoryLayer.add([new Point(1,1), "Seattle"])
        memoryLayer.add([new Point(2,2), "Portland"])
        memoryLayer.add([new Point(3,3), "Tacoma"])
        assertEquals 3, memoryLayer.count()

        // And add it to FlatGeobuf
        assertNotNull geobuf.add(memoryLayer)
        assertTrue new File(directory, "locations.fgb").exists()

        // Make sure we can read it from FlatGeobuf
        Layer geobufLayer = geobuf.get("locations")
        assertEquals 3, memoryLayer.count
    }

    @Test void remove() {
        File directory = temporaryFolder.newFolder("flatgeobufs")
        Workspace workspace = new FlatGeobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.get("points").add(new Feature([geom: new Point(1,1)],"id.1"))
        workspace.create("lines",[new Field("geom","LineString","EPSG:4326")])
        workspace.get("lines").add(new Feature([geom: new LineString([[1,1],[10,10]])],"id.1"))
        workspace.create("polygons",[new Field("geom","Polygon","EPSG:4326")])
        workspace.get("polygons").add(new Feature([geom: new Point(1,1).buffer(5)],"id.1"))
        assertTrue workspace.has("points")
        assertTrue workspace.has("lines")
        assertTrue workspace.has("polygons")
        workspace.remove("points")
        workspace.remove(workspace.get("lines"))
        workspace.remove(workspace.get("polygons"))
        // @TODO Removed layers are not being remove from typeNames
        workspace = new FlatGeobuf(directory)
        assertFalse workspace.has("points")
        assertFalse workspace.has("lines")
        assertFalse workspace.has("polygons")
    }

    @Test void getWorkspace() {
        File directory = temporaryFolder.newFolder("flatgeobufs")
        Workspace workspace = new FlatGeobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.get("points").add(new Feature([geom: new Point(1,1)],"id.1"))

        Workspace w = Workspace.getWorkspace(new File(directory, "points.fgb").absolutePath)
        assertNotNull w
        assertEquals("FlatGeobuf", w.format)
    }

    @Test void getWorkspaceFromString() {
        File directory = temporaryFolder.newFolder("flatgeobufs")
        Workspace workspace = new FlatGeobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.get("points").add(new Feature([geom: new Point(1,1)],"id.1"))
        workspace.close()
        FlatGeobuf geobuf = Workspace.getWorkspace("type=flatgeobuf file=${directory}")
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace("type=flatgeobuf file=${new File(directory, 'points.fgb')}")
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
    }

    @Test void getWorkspaceFromMap() {
        File directory = temporaryFolder.newFolder("flatgeobufs")
        Workspace workspace = new FlatGeobuf(directory)
        workspace.create("points",[new Field("geom","Point","EPSG:4326")])
        workspace.get("points").add(new Feature([geom: new Point(1,1)],"id.1"))
        workspace.close()
        FlatGeobuf geobuf = Workspace.getWorkspace([type: 'flatgeobuf', file: directory])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'flatgeobuf', file: new File(directory, 'points.fgb')])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'flatgeobuf', file: directory.absolutePath])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
        geobuf = Workspace.getWorkspace([type: 'flatgeobuf', file: new File(directory, 'points.fgb').absolutePath])
        assertNotNull geobuf
        assertTrue geobuf.names.contains("points")
    }

}

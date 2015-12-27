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

}

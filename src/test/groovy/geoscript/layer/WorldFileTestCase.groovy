package geoscript.layer

import geoscript.geom.Bounds
import geoscript.layer.WorldFile
import org.junit.Test
import static org.junit.Assert.*

/**
 * The WorldFile Unit Test
 * @author Jared Erickson
 */
class WorldFileTestCase {

    @Test void create() {

        // Create a WorldFile from bounds and size
        File file = File.createTempFile("worldfile_",".txt")
        WorldFile worldFile = new WorldFile(new Bounds(-123.06, 46.66, -121.15, 47.48), [500,500], file)
        assertEquals(0.003819, worldFile.pixelSize[0], 0.00001)
        assertEquals(-0.00164, worldFile.pixelSize[1], 0.00001)
        assertEquals(0.0, worldFile.rotation[0], 0.01)
        assertEquals(0.0, worldFile.rotation[1], 0.01)
        assertEquals(-123.05809, worldFile.ulc.x, 0.0001)
        assertEquals(47.47918, worldFile.ulc.y, 0.0001)
        assertNotNull(worldFile.file)
        assertEquals(file.name, worldFile.file.name)
        assertEquals("WorldFile: ${file.absolutePath}".toString(), worldFile.toString())
        assertNotNull(worldFile.file.text)

        // Create a WorldFile from a File
        worldFile = new WorldFile(file)
        assertEquals(0.003819, worldFile.pixelSize[0], 0.00001)
        assertEquals(-0.00164, worldFile.pixelSize[1], 0.00001)
        assertEquals(0.0, worldFile.rotation[0], 0.01)
        assertEquals(0.0, worldFile.rotation[1], 0.01)
        assertEquals(-123.05809, worldFile.ulc.x, 0.0001)
        assertEquals(47.47918, worldFile.ulc.y, 0.0001)
        assertNotNull(worldFile.file)
        assertEquals(file.name, worldFile.file.name)
        assertEquals("WorldFile: ${file.absolutePath}".toString(), worldFile.toString())
        assertNotNull(worldFile.file.text)
    }
}
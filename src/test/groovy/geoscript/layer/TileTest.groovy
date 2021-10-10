package geoscript.layer

import org.junit.jupiter.api.Test
import java.awt.image.BufferedImage
import static org.junit.jupiter.api.Assertions.*

/**
 * The Tile Unit Test
 * @author Jared Erickson
 */
class TileTest {

    @Test
    void tile() {
        Tile t = new Tile(1, 0, 2)
        assertEquals 1, t.z
        assertEquals 0, t.x
        assertEquals 2, t.y
        assertNull t.data
        assertNull t.base64String
        assertEquals "Tile(x:0, y:2, z:1)", t.toString()

        File f = new File(getClass().getClassLoader().getResource("0.png").toURI())
        t = new Tile(1, 0, 2, f.bytes)
        assertEquals 1, t.z
        assertEquals 0, t.x
        assertEquals 2, t.y
        assertNotNull t.data
        assertEquals "Tile(x:0, y:2, z:1)", t.toString()

        String base64str = t.base64String
        assertNotNull base64str
        assertTrue base64str.length() > 0
    }

}

package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The ASCII Unit Test
 * @author Jared Erickson
 */
class ASCIITest {

    private static final String NEW_LINE = System.getProperty("line.separator")

    @Test
    void renderToString() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        ASCII renderer = new ASCII(width: 50)
        String str = renderer.render(map)
        assertNotNull str
        List rows = str.split(NEW_LINE)
        assertEquals 33, rows.size()
        assertEquals 50, rows[0].length()
    }

    @Test
    void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        ASCII renderer = new ASCII()
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        renderer.render(map, out)
        assertTrue out.toByteArray().length > 0
    }
}

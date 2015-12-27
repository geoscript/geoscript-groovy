package geoscript.render

import geoscript.layer.Layer
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.junit.Test

import static org.junit.Assert.*

/**
 * The Base64 Unit Test
 * @author Jared Erickson
 */
class Base64TestCase {

    @Test
    void renderToString() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        // PNG with prefix
        Base64 base64 = new Base64()
        String str = base64.render(map)
        assertNotNull(str)
        assertTrue(str.startsWith("image/png;base64,"))
        // PNG with out prefix
        base64 = new Base64(renderer: new PNG(), includePrefix: false)
        str = base64.render(map)
        assertNotNull(str)
        assertFalse(str.startsWith("image/png;base64,"))
        // JPEG with prefix
        base64 = new Base64(renderer: new JPEG(), includePrefix: false)
        str = base64.render(map)
        assertNotNull(str)
        assertFalse(str.startsWith("image/jpeg;base64,"))
        // GIF with prefix
        base64 = new Base64(renderer: new GIF(), includePrefix: false)
        str = base64.render(map)
        assertNotNull(str)
        assertFalse(str.startsWith("image/gif;base64,"))
    }

    @Test
    void renderToOutputStream() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer], backgroundColor: "white")
        Base64 base64 = new Base64()
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        base64.render(map, out)
        byte[] bytes = out.toByteArray()
        assertTrue bytes.length > 0
    }

}

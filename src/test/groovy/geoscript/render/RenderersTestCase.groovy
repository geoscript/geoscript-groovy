package geoscript.render

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Renderers Unit Test
 * @author Jared Erickson
 */
class RenderersTestCase {

    @Test void list() {
        List<Renderer> renderers = Renderers.list()
        assertNotNull renderers
        assertTrue renderers.size() > 0
    }

    @Test void find() {
        Renderer renderer = Renderers.find("png")
        assertNotNull renderer
        renderer = Renderers.find("jpeg")
        assertNotNull renderer

        renderer = Renderers.find("asdf")
        assertNull renderer
    }
}

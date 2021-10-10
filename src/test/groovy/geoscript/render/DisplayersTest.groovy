package geoscript.render

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class DisplayersTest {

    @Test void list() {
        assertTrue(Displayers.list().size() > 0)
        assertNotNull(Displayers.list().find { Displayer d -> d.class.simpleName.equalsIgnoreCase("MapWindow")})
        assertNotNull(Displayers.list().find { Displayer d -> d.class.simpleName.equalsIgnoreCase("Window")})
    }

    @Test void find() {
        assertNotNull(Displayers.find("MapWindow"))
        assertNotNull(Displayers.find("Window"))
        assertNull(Displayers.find("ASDF"))
    }

}

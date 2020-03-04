package geoscript.carto

import org.junit.Test
import static org.junit.Assert.*

class ImageItemTest {

    @Test
    void create() {

        ImageItem item = new ImageItem(10,20,300,400)
            .path(new File(getClass().getClassLoader().getResource("image.png").toURI()))

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals("image.png", item.path.name)
        assertTrue(item.toString().contains("ImageItem(x = 10, y = 20, width = 300, height = 400, path = "))
        assertTrue(item.toString().endsWith("image.png)"))
    }


}

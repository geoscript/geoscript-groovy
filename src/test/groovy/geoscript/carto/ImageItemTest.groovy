package geoscript.carto

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

class ImageItemTest extends AbstractCartoTest {

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

    @Test
    void draw() {
        draw("image", 292, 295, { CartoBuilder cartoBuilder ->
            cartoBuilder.image(new ImageItem(10,10,272,275)
                    .path(new File(getClass().getClassLoader().getResource("image.png").toURI()))
            )
        })
    }

}

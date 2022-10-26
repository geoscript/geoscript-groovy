package geoscript.carto

import org.junit.jupiter.api.Test

import java.awt.*

import static org.junit.jupiter.api.Assertions.assertEquals

class GridItemTest extends AbstractCartoTest {

    @Test
    void create() {

        GridItem item = new GridItem(10,20,300,400)
            .strokeColor(Color.BLUE)
            .strokeWidth(2)
            .size(20)

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(20, item.size)
        assertEquals(Color.BLUE, item.strokeColor)
        assertEquals(2f, item.strokeWidth, 0.1f)
        assertEquals("GridItem(x = 10, y = 20, width = 300, height = 400, size = 20, " +
                "stroke-color = java.awt.Color[r=0,g=0,b=255], stroke-width = 2.0)", item.toString())
    }

    @Test
    void draw() {
        draw("grid", 100, 100, { CartoBuilder cartoBuilder ->
            cartoBuilder.grid(new GridItem(0,0,100,100)
                .size(10)
                .strokeColor(Color.BLUE)
                .strokeWidth(0.5f)
            )
        })
    }

}

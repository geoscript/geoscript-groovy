package geoscript.carto

import org.junit.Test

import java.awt.*

import static org.junit.Assert.assertEquals

class RectangleItemTest {

    @Test
    void create() {

        RectangleItem item = new RectangleItem(10,20,300,400)
            .fillColor(Color.WHITE)
            .strokeColor(Color.BLUE)
            .strokeWidth(1.2)

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(Color.BLUE, item.strokeColor)
        assertEquals(1.2f, item.strokeWidth, 0.1f)
        assertEquals(Color.WHITE, item.fillColor)
        assertEquals("RectangleItem(x = 10, y = 20, width = 300, height = 400, " +
                "stroke-color = java.awt.Color[r=0,g=0,b=255], " +
                "fill-color = java.awt.Color[r=255,g=255,b=255], " +
                "stroke-width = 1.2)", item.toString())
    }


}

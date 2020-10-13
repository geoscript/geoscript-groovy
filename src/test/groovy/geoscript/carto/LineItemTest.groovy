package geoscript.carto

import org.junit.Test

import java.awt.*

import static org.junit.Assert.assertEquals

class LineItemTest {

    @Test
    void create() {

        LineItem item = new LineItem(10,20,300,400)
            .strokeColor(Color.BLUE)
            .strokeWidth(2)

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(Color.BLUE, item.strokeColor)
        assertEquals(2f, item.strokeWidth, 0.1f)
        assertEquals("LineItem(x = 10, y = 20, width = 300, height = 400, " +
                "stroke-color = java.awt.Color[r=0,g=0,b=255], stroke-width = 2.0)", item.toString())
    }


}

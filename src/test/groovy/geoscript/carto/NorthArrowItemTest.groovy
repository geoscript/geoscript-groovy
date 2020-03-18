package geoscript.carto

import org.junit.Test

import java.awt.*

import static org.junit.Assert.assertEquals

class NorthArrowItemTest {

    @Test
    void create() {

        NorthArrowItem item = new NorthArrowItem(10,20,300,400)
            .fillColor1(Color.WHITE)
            .strokeColor1(Color.BLUE)
            .fillColor2(Color.YELLOW)
            .strokeColor2(Color.RED)
            .strokeWidth(1.2)

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(Color.BLUE, item.strokeColor1)
        assertEquals(Color.WHITE, item.fillColor1)
        assertEquals(Color.RED, item.strokeColor2)
        assertEquals(Color.YELLOW, item.fillColor2)
        assertEquals(1.2f, item.strokeWidth, 0.1)
        assertEquals("NorthArrowItem(x = 10, y = 20, width = 300, height = 400, " +
                "fill-color1 = java.awt.Color[r=255,g=255,b=255], stroke-color1 = java.awt.Color[r=0,g=0,b=255], " +
                "fill-color2 = java.awt.Color[r=255,g=255,b=0], stroke-color2 = java.awt.Color[r=255,g=0,b=0], " +
                "stroke-width = 1.2)", item.toString())
    }


}

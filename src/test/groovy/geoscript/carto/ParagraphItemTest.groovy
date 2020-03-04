package geoscript.carto

import org.junit.Test

import java.awt.*

import static org.junit.Assert.assertEquals

class ParagraphItemTest {

    @Test
    void create() {

        ParagraphItem item = new ParagraphItem(10,20,300,400)
            .font(new Font("Verdana", Font.BOLD, 14))
            .color(Color.BLUE)
            .text("Text Here")

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(item.font.name, "Verdana")
        assertEquals(item.font.style, Font.BOLD)
        assertEquals(item.font.size, 14)
        assertEquals(item.color, Color.BLUE)
        assertEquals("Text Here", item.text)
        assertEquals("ParagraphItem(x = 10, y = 20, width = 300, height = 400, " +
                "text = Text Here, color = java.awt.Color[r=0,g=0,b=255], " +
                "font = java.awt.Font[family=Verdana,name=Verdana,style=bold,size=14])", item.toString())
    }


}

package geoscript.carto

import org.junit.jupiter.api.Test

import java.awt.Color
import java.awt.Font
import java.text.SimpleDateFormat
import static org.junit.jupiter.api.Assertions.*

class DateTextItemTest extends AbstractCartoTest {

    @Test
    void create() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy")

        DateTextItem item = new DateTextItem(10,20,300,400)
            .font(new Font("Verdana", Font.BOLD, 14))
            .color(Color.BLUE)
            .verticalAlign(VerticalAlign.MIDDLE)
            .horizontalAlign(HorizontalAlign.CENTER)
            .date(dateFormat.parse("2/25/2020"))
            .format("M/d/yyyy")

        assertEquals(10, item.x)
        assertEquals(20, item.y)
        assertEquals(300, item.width)
        assertEquals(400, item.height)
        assertEquals(item.font.name, "Verdana")
        assertEquals(item.font.style, Font.BOLD)
        assertEquals(item.font.size, 14)
        assertEquals(item.color, Color.BLUE)
        assertEquals(VerticalAlign.MIDDLE, item.verticalAlign)
        assertEquals(HorizontalAlign.CENTER, item.horizontalAlign)
        assertEquals("2/25/2020", dateFormat.format(item.date))
        assertEquals("M/d/yyyy", item.format)
        String itemString = item.toString()
        assertTrue(itemString.startsWith("DateTextItem(x = 10, y = 20, width = 300, height = 400,"))
        assertTrue(itemString.contains("date = "))
        assertTrue(itemString.contains("color = "))
        assertTrue(itemString.contains("font = "))
        assertTrue(itemString.endsWith("horizontal-align = CENTER, vertical-align = MIDDLE)"))
    }

    @Test
    void draw() {
        draw("datetext", 150, 50, { CartoBuilder cartoBuilder ->
            cartoBuilder.dateText(new DateTextItem(10, 10, 140, 30)
                    .date(new Date())
                    .format("MM/dd/yyy")
                    .horizontalAlign(HorizontalAlign.CENTER)
                    .verticalAlign(VerticalAlign.MIDDLE)
                    .color(Color.BLACK)
                    .font(new Font("Arial", Font.PLAIN, 14))
            )
        })
    }

}

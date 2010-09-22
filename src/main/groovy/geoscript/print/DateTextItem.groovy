package geoscript.print

import java.awt.Graphics
import java.awt.Font
import java.awt.Color
import java.text.SimpleDateFormat

/**
 * A Date text Item
 * @author Jared Erickson
 */
class DateTextItem extends Item {

    String format = "dd/MM/yyyy"
    Date date = null
    Color color = Color.BLACK
    Font font = new Font("Default", Font.PLAIN, 12)

    void draw(Graphics g) {
        if (date == null) date = new Date()
        g.color = color
        g.font = font
        def formatter = new SimpleDateFormat(format)
        g.drawString(formatter.format(date), x, y)
    }

}


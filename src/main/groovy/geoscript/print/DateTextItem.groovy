package geoscript.print

import java.awt.Graphics
import java.awt.Font
import java.awt.Color
import java.text.SimpleDateFormat

/**
 * The DateTextItem displays a Date with the given datet format
 * @author Jared Erickson
 */
class DateTextItem extends Item {

    /**
     * The SimpleDateFormat String
     */
    String format = "dd/MM/yyyy"

    /**
     * The Date
     */
    Date date = null

    /**
     * The text Color
     */
    Color color = Color.BLACK

    /**
     * The text Font
     */
    Font font = new Font("Default", Font.PLAIN, 12)

    /**
     * Draw the DateTextItem
     * @g The Graphics context
     */
    void draw(Graphics g) {
        if (date == null) date = new Date()
        g.color = color
        g.font = font
        def formatter = new SimpleDateFormat(format)
        g.drawString(formatter.format(date), x, y)
    }
}


package geoscript.print

import java.awt.Graphics
import java.awt.Font
import java.awt.Color

/**
 * A Text Item
 * @author Jared Erickson
 */
class TextItem extends Item {

    String text
    Color color = Color.BLACK
    Font font = new Font("Default", Font.PLAIN, 12)
    String align = "left" // left, center, right

    void draw(Graphics g) {
        g.color = color
        g.font = font
        int drawX = x
        int drawY = y
        if (width > 0 && !align.equalsIgnoreCase("left")) {
            def metrics = g.fontMetrics
            int stringWidth = metrics.stringWidth(text)
            if (align.equalsIgnoreCase("center")) {
                drawX = width / 2 - stringWidth / 2
            }
            else if (align.equalsIgnoreCase("right")) {
                drawX = width - stringWidth
            }
        }
        g.drawString(text, drawX, drawY)
    }

}


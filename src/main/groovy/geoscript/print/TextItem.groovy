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
    String halign = "left" // left, center, right
    String valign = "bottom" // top, middle, bottom

    void draw(Graphics g) {
        g.color = color
        g.font = font
        int drawX = x
        int drawY = y
        if (width > 0 && !halign.equalsIgnoreCase("left")) {
            def metrics = g.fontMetrics
            int stringWidth = metrics.stringWidth(text)
            if (halign.equalsIgnoreCase("center")) {
                drawX = x + (width / 2 - stringWidth / 2)
            }
            else if (halign.equalsIgnoreCase("right")) {
                drawX = x+ (width - stringWidth)
            }
        }
        if (height > 0 && !valign.equalsIgnoreCase("bottom")) {
            def metrics = g.fontMetrics
            int stringHeight = metrics.getStringBounds(text, g).height
            if (valign.equalsIgnoreCase("middle")) {
                drawX = height / 2 - stringHeight / 2
            }
            else if (valign.equalsIgnoreCase("top")) {
                drawX = height + stringHeight
            }
        }
        g.drawString(text, drawX, drawY)
    }

}


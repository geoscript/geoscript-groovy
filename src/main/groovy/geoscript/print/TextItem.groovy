package geoscript.print

import java.awt.Graphics
import java.awt.Font
import java.awt.Color

/**
 * The Text Item can display text.
 * @author Jared Erickson
 */
class TextItem extends Item {

    /**
     * The text to display
     */
    String text

    /**
     * The text color
     */
    Color color = Color.BLACK

    /**
     * The text Font
     */
    Font font = new Font("Default", Font.PLAIN, 12)

    /**
     * The horizontal alignment of the text (left, center, right)
     */
    String halign = "left" // left, center, right

    /**
     * The vertical alignment of the text (top, middle, bottom)
     */
    String valign = "bottom" // top, middle, bottom

    /**
     * Draw the TextItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        g.color = color
        g.font = font
        drawString(g, text, x, y, width, height, halign, valign)
    }

    /**
     * Draw the text to the Graphics
     * @param g The Graphics
     * @param text The text
     * @param x The x coordinate
     * @param y The y coordinate
     * @param w The width
     * @param h The height
     * @param halign The horizontal alignment of the text (left, center, right)
     * @param valign The vertical alignment of the text (top, middle, bottom)
     */
    private void drawString(Graphics g, String text, int x, int y, int w, int h, String halign, String valign) {
        def metrics = g.fontMetrics
        def bounds = metrics.getStringBounds(text, g)

        int textX = x
        if (w > 0) {
            if (halign.equalsIgnoreCase("center")) {
                textX = x + w/2 - bounds.width/2
            }
            else if (halign.equalsIgnoreCase("right")) {
                textX = x + w - bounds.width
            }
        }

        int textY = y
        if (h > 0) {
            if (valign.equalsIgnoreCase("top")) {
                textY = y + bounds.height
            }
            else if (valign.equalsIgnoreCase("middle")) {
                textY = y + h/2 + bounds.height / 2 - metrics.descent
            }
            else if (valign.equalsIgnoreCase("bottom")) {
                textY = y + h - metrics.descent
            }
        }
        
        //g.drawRect(textX, textY - bounds.height + metrics.descent as int, bounds.width as int, bounds.height as int)
        g.drawString(text, textX, textY)
    }
}
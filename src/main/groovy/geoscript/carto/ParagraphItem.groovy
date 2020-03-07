package geoscript.carto

import java.awt.Color
import java.awt.Font

/**
 * Add a paragraph to a cartographic document.
 * @author Jared Erickson
 */
class ParagraphItem extends Item {

    String text

    Font font = new Font("Arial", Font.PLAIN, 12)

    Color color = Color.BLACK

    /**
     * Create a paragraph from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    ParagraphItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the text
     * @param text The text
     * @return The ParagraphItem
     */
    ParagraphItem text(String text) {
        this.text = text
        this
    }

    /**
     * Set the text color
     * @param color The text color
     * @return The ParagraphItem
     */
    ParagraphItem color(Color color) {
        this.color = color
        this
    }

    /**
     * Set the font
     * @param font The font
     * @return The ParagraphItem
     */
    ParagraphItem font(Font font) {
        this.font = font
        this
    }

    @Override
    String toString() {
        "ParagraphItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, text = ${text}, color = ${color}, font = ${font})"
    }

}

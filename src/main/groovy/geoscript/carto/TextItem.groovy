package geoscript.carto

import java.awt.Color
import java.awt.Font

/**
 * Add text to a cartographic document
 * @author Jared Erickson
 */
class TextItem extends Item {

    String text

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    /**
     * Create  text from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    TextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the text
     * @param text The text
     * @return The TextItem
     */
    TextItem text(String text) {
        this.text = text
        this
    }

    /**
     * Set the color
     * @param color The text color
     * @return The TextItem
     */
    TextItem color(Color color) {
        this.color = color
        this
    }

    /**
     * Set the font
     * @param font The font
     * @return The TextItem
     */
    TextItem font(Font font) {
        this.font = font
        this
    }

    /**
     * Set the horizontal alignment
     * @param horizontalAlign The HorizontalAlign
     * @return The TextItem
     */
    TextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    /**
     * Set the vertical alignment
     * @param verticalAlign The VerticalAlign
     * @return The TextItem
     */
    TextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "TextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, text = ${text}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign})"
    }
}

package geoscript.carto

import java.awt.Color
import java.awt.Font

/**
 * Adds date text to a cartographic document.
 * @author Jared Erickson
 */
class DateTextItem extends Item {

    String format = "MM/dd/yyyy"

    Date date = null

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    /**
     * Create a DateTextItem from the top left
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    DateTextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the date format string
     * @param format The date format string
     * @return The DateTextItem
     */
    DateTextItem format(String format) {
        this.format = format
        this
    }

    /**
     * Set the Date
     * @param date The Date
     * @return The DateTextItem
     */
    DateTextItem date(Date date) {
        this.date = date
        this
    }

    /**
     * Set the text Color
     * @param color The text Color
     * @return The DateTextItem
     */
    DateTextItem color(Color color) {
        this.color = color
        this
    }

    /**
     * Set the text Font
     * @param font The text Font
     * @return The DateTextItem
     */
    DateTextItem font(Font font) {
        this.font = font
        this
    }

    /**
     * Set the horizontal alignment of the text
     * @param horizontalAlign The HorizontalAlign
     * @return The DateTextItem
     */
    DateTextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    /**
     * Set the vertical alignment of the text
     * @param verticalAlign The VerticalAlign
     * @return The DateTextItem
     */
    DateTextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "DateTextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, date = ${date}, format = ${format}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign})"
    }

}

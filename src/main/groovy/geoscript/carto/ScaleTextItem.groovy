package geoscript.carto

import geoscript.render.Map

import java.awt.Color
import java.awt.Font

/**
 * Add scale text to a cartographic document.
 * @author Jared Erickson
 */
class ScaleTextItem extends Item {

    Map map

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    String format = "#"

    String prefixText = "Scale: "

    /**
     * Create a scale text from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    ScaleTextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the Map to use when calculating the map scale
     * @param map The Map
     * @return The ScaleTextItem
     */
    ScaleTextItem map(Map map) {
        this.map = map
        this
    }

    /**
     * Set the number format
     * @param format The number format
     * @return The ScaleTextItem
     */
    ScaleTextItem format(String format) {
        this.format = format
        this
    }

    /**
     * Set the prefix text
     * @param prefixText The prefix text
     * @return The ScaleTextItem
     */
    ScaleTextItem prefixText(String prefixText) {
        this.prefixText = prefixText
        this
    }

    /**
     * Set the text color
     * @param color The text color
     * @return The ScaleTextItem
     */
    ScaleTextItem color(Color color) {
        this.color = color
        this
    }

    /**
     * Set the font
     * @param font The Font
     * @return The ScaleTextItem
     */
    ScaleTextItem font(Font font) {
        this.font = font
        this
    }

    /**
     * Set the horizontal alignment
     * @param horizontalAlign The HorizontalAlign
     * @return The ScaleTextItem
     */
    ScaleTextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    /**
     * Set the vertical alignment
     * @param verticalAlign The VerticalAlign
     * @return The ScaleTextItem
     */
    ScaleTextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "ScaleTextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, prefixText = ${prefixText}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign}, map = ${map}, format = ${format})"
    }
}

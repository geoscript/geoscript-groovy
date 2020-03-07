package geoscript.carto

import java.awt.Color

/**
 * Add a line to a cartographic document
 * @author Jared Erickson
 */
class LineItem extends Item {

    Color strokeColor = Color.BLACK

    float strokeWidth = 1

    /**
     * Create a line from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    LineItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the stroke color
     * @param color The stroke color
     * @return The LineItem
     */
    LineItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    /**
     * Set the stroke width
     * @param width The stroke width
     * @return The LineItem
     */
    LineItem strokeWidth(float width) {
        this.strokeWidth = width
        this
    }

    @Override
    String toString() {
        "LineItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, stroke-color = ${strokeColor}, stroke-width = ${strokeWidth})"
    }

}

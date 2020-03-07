package geoscript.carto

import java.awt.Color

/**
 * Add a rectangle to a cartographic document.
 * @author Jared Erickson
 */
class RectangleItem extends Item {

    Color strokeColor = Color.BLACK

    Color fillColor

    float strokeWidth = 1

    /**
     * Create a rectangle from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    RectangleItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the stroke color
     * @param color The stroke color
     * @return The RectangleItem
     */
    RectangleItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    /**
     * Set the fill color
     * @param color The fill color
     * @return The RectangleItem
     */
    RectangleItem fillColor(Color color) {
        this.fillColor = color
        this
    }

    /**
     * Set the stroke width
     * @param strokeWidth The stroke width
     * @return The RectangleItem
     */
    RectangleItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "RectangleItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, stroke-color = ${strokeColor}, fill-color = ${fillColor}, stroke-width = ${strokeWidth})"
    }

}

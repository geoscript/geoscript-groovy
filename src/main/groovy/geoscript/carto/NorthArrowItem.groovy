package geoscript.carto

import java.awt.Color

/**
 * Add a north arrow to a cartographic document.
 * @author Jared Erickson
 */
class NorthArrowItem extends Item {

    Color fillColor1 = Color.BLACK

    Color strokeColor1 = Color.BLACK

    Color fillColor2 = Color.WHITE

    Color strokeColor2 = Color.BLACK

    float strokeWidth = 1

    /**
     * Create a north arrow from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    NorthArrowItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the first stroke color
     * @param color The first stroke color
     * @return The NorthArrowItem
     */
    NorthArrowItem strokeColor1(Color color) {
        this.strokeColor1 = color
        this
    }

    /**
     * Set the first fill color
     * @param color The first fill color
     * @return The NorthArrowItem
     */
    NorthArrowItem fillColor1(Color color) {
        this.fillColor1 = color
        this
    }

    /**
     * Set the second stroke Color
     * @param color The second stroke Color
     * @return The NorthArrowItem
     */
    NorthArrowItem strokeColor2(Color color) {
        this.strokeColor2 = color
        this
    }

    /**
     * Set the second fill color
     * @param color The second fill Color
     * @return The NorthArrowItem
     */
    NorthArrowItem fillColor2(Color color) {
        this.fillColor2 = color
        this
    }

    /**
     * Set the stroke width
     * @param strokeWidth The stroke width
     * @return The NorthArrowItem
     */
    NorthArrowItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "NorthArrowItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, fill-color1 = ${fillColor1}, stroke-color1 = ${strokeColor1}, fill-color2 = ${fillColor2}, stroke-color2 = ${strokeColor2}, stroke-width = ${strokeWidth})"
    }

}

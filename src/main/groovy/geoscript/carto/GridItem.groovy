package geoscript.carto

import java.awt.Color

/**
 * Add a grid to a cartographic document.
 * Useful for positioning items.
 * @author Jared Erickson
 */
class GridItem extends Item {

    int size = 10

    Color strokeColor = Color.LIGHT_GRAY

    float strokeWidth = 1

    /**
     * Create a GridItem at a location from the top left
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    GridItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the cell size of the grid
     * @param size The cell size
     * @return The GridItem
     */
    GridItem size(int size) {
        this.size = size
        this
    }

    /**
     * Set the stroke color
     * @param color The stroke color
     * @return The GridItem
     */
    GridItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    /**
     * Set the stroke width
     * @param strokeWidth The stroke width
     * @return The GridItem
     */
    GridItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "GridItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, size = ${size}, stroke-color = ${strokeColor}, stroke-width = ${strokeWidth})"
    }

}

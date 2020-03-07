package geoscript.carto

import java.awt.Rectangle

/**
 * The abstract base class for all cartographic items.
 * @author Jared Erickson
 */
abstract class Item {

    final int x

    final int y

    final int width

    final int height

    /**
     * Create an Item from the top left
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    Item(int x, int y, int width, int height) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    /**
     * Get a Rectangle
     * @return A Rectangle
     */
    Rectangle getRectangle() {
        return new Rectangle(x, y, width, height)
    }

}

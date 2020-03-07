package geoscript.carto

import geoscript.render.Map

/**
 * Add a Map to a cartographic document.
 * @author Jared Erickson
 */
class MapItem extends Item {

    Map map

    /**
     * Create a Map from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    MapItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the Map
     * @param map The Map
     * @return The MapItem
     */
    MapItem map(Map map) {
        this.map = map
        this
    }

    @Override
    String toString() {
        "MapItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, map = ${map})"
    }

}

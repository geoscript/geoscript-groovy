package geoscript.carto

import geoscript.render.Map
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Style

/**
 * Add an overview map to a carto document
 * @author Jared Erickson
 */
class OverviewMapItem extends Item {

    Map linkedMap

    Map overviewMap

    Style areaStyle = new Fill("red", 0.2) + new Stroke("red", 1)

    boolean zoomIntoBounds = false

    double scaleFactor = 2.0

    /**
     * Create a Overview Map from the top left with the given width and height.
     * @param x The number of pixels from the left
     * @param y The number of pixels from the top
     * @param width The width in pixels
     * @param height The height in pixels
     */
    OverviewMapItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    /**
     * Set the linked Map
     * @param map The linked Map
     * @return This OverviewMapItem
     */
    OverviewMapItem linkedMap(Map map) {
        this.linkedMap = map
        this
    }

    /**
     * Set the overview Map
     * @param map The overview Map
     * @return This OverviewMapItem
     */
    OverviewMapItem overviewMap(Map map) {
        this.overviewMap = map
        this
    }

    /**
     * Set the area Style
     * @param style The area Style
     * @return This OverviewMapItem
     */
    OverviewMapItem areaStyle(Style style) {
        this.areaStyle = style
        this
    }

    /**
     * Set whether to zoom into the bounds of the linked Map or not
     * @param zoomIntoBounds Whether to zoom into the bounds of the linked Map or not
     * @return This OverviewMap
     */
    OverviewMapItem zoomIntoBounds(boolean zoomIntoBounds) {
        this.zoomIntoBounds = zoomIntoBounds
        this
    }

    /**
     * Set the scale factor for expanding the linked Map bounds
     * @param scaleFactor The scale factor
     * @return This OverviewMap
     */
    OverviewMapItem scaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor
        this
    }

    @Override
    String toString() {
        "OverviewMapItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, " +
                "linkedMap = ${linkedMap}, overViewMap = ${overviewMap}, areaStyle = ${areaStyle}, " +
                "zoomIntoBounds = ${zoomIntoBounds}, scaleFactor = ${scaleFactor})"
    }
}

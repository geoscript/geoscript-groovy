package geoscript.render

import geoscript.layer.Layer
import geoscript.geom.Bounds
import geoscript.workspace.Memory
import geoscript.geom.Geometry
import geoscript.style.Style

/**
 * Draw a Layer or Geometry.
 * @author Jared Erickson
 */
class Draw {

    /**
     * Draw Geometry
     * @param geometries The List of Geometries
     * @param style A Style
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(List<Geometry> geometries, Style style = null, List size = [500,500], String format) {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        geometries.each {g -> layer.add([g])}
        draw(layer, style, size, format)
    }

    /**
     * Draw a Layer
     * @param layer The Layer
     * @param style A Style
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(Layer layer, Style style = null, List size = [500,500], String format) {
        Bounds bounds = layer.bounds.scale(1.1)
        Map map = new Map([layer], style ? [style] : [])
        map.render(format, bounds, size)
    }
}
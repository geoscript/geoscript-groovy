package geoscript.render

import geoscript.layer.Layer
import geoscript.feature.Feature
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
     * Draw List of Geometries
     * @param geometries The List of Geometries
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(List<Geometry> geometries, Style style = null, Bounds bounds = null, List size = [500,500], String format="window") {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        geometries.each {g -> layer.add([g])}
        draw(layer, style, bounds, size, format)
    }

    /**
     * Draw Geometry
     * @param geometry The Geometry
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(Geometry geometry, Style style = null, Bounds bounds = null, List size = [500,500], String format="window") {
        draw([geometry], style, bounds, size, format)
    }

    /**
     * Draw a Feature
     * @param feature The Feature
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(Feature feature, Style style = null, Bounds bounds = null, List size = [500,500], String format="window") {
        Memory memory = new Memory()
        Layer layer = memory.create(feature.schema)
        layer.add(feature)
        draw(layer, style, bounds, size, format)
    }

    /**
     * Draw a Layer
     * @param layer The Layer
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param format The format ("jpeg", "png", "window", "mapwindow")
     */
    static void draw(Layer layer, Style style = null, Bounds bounds = null, List size = [500,500], String format="window") {
        if(!bounds) bounds = layer.bounds.scale(1.1)
        Map map = new Map([layer], style ? [style] : [])
        map.render(format, bounds, size)
    }
}

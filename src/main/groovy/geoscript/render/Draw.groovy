package geoscript.render

import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.layer.Layer
import geoscript.style.Style
import geoscript.style.Symbolizer
import geoscript.workspace.Memory
import java.awt.image.BufferedImage

/**
 * Easily draw Geometry, Feature, and Layers to an image or interactive App.
 * @author Jared Erickson
 */
class Draw {

    /**
     * Draw Geometry
     * @param geometry The Geometry
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param out The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.
     * @param format The format ("jpeg", "png", "pdf", "svg")
     */
    static void draw(Geometry geometry, Style style = null, Bounds bounds = null, List size = [500, 500], def out = null, String format = "png") {
        draw([geometry], style, bounds, size, out, format)
    }

    /**
     * Draw List of Geometries
     * @param geometries The List of Geometries
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param out The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.
     * @param format The format ("jpeg", "png", "pdf", "svg")
     */
    static void draw(List<Geometry> geometries, Style style = null, Bounds bounds = null, List size = [500, 500], def out = null, String format = "png") {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        layer.style = style ? style : Symbolizer.getDefault(geometries[0].geometryType)
        geometries.each {g -> layer.add([g])}
        draw(layer, bounds, size, out, format)
    }

    /**
     * Draw a Feature
     * @param feature The Feature
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param out The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.
     * @param format The format ("jpeg", "png", "pdf", "svg")
     */
    static void draw(Feature feature, Style style = null, Bounds bounds = null, List size = [500, 500], def out = null, String format = "png") {
        Memory memory = new Memory()
        Layer layer = memory.create(feature.schema)
        layer.style = style ? style : Symbolizer.getDefault(feature.geom.geometryType)
        layer.add(feature)
        draw(layer, bounds, size, out, format)
    }

    /**
     * Draw a Layer
     * @param layer The Layer
     * @param style A Style
     * @param bounds The Bounds
     * @param size The size of the canvas ([400,350])
     * @param out The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.
     * @param format The format ("jpeg", "png", "pdf", "svg")
     */
    static void draw(Layer layer, Bounds bounds = null, List size = [500, 500], def out = null, String format = "png") {
        if (!bounds) bounds = layer.bounds.scale(1.1)
        Map map = new Map(
                layers: [layer],
                bounds: bounds,
                width: size[0],
                height: size[1],
                type: format
        )
        // Display in a Window
        if (out == null) {
            new Window(map)
        }
        // Draw to an OutputStream
        else if (out instanceof OutputStream) {
            map.render(out)
        }
        // Draw to a File
        else {
            File file = out instanceof File ? out : new File(out.toString())
            map.render(file)
        }
    }

    /**
     * Draw a Geometry to an image
     * @param geometry The Geometry
     * @param style The Style
     * @param bounds The Bounds
     * @param size The image size
     * @param imageType The image type
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(Geometry geometry, Style style = null, Bounds bounds = null, List size = [500, 500], String imageType = "png") {
        drawToImage([geometry], style, bounds, size, imageType)
    }

    /**
     * Draw a List of Geometries to an image
     * @param geometries A List of Geometries
     * @param style A Style
     * @param bounds The Bounds
     * @param size The image size
     * @param imageType The image type
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(List geometries, Style style = null, Bounds bounds = null, List size = [500, 500], String imageType = "png") {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        layer.style = style ? style : Symbolizer.getDefault(geometries[0].geometryType)
        geometries.each {g -> layer.add([g])}
        drawToImage(layer, bounds, size, imageType)
    }

    /**
     * Draw a Feature to an image
     * @param feature The Feature
     * @param style The Style
     * @param bounds The Bounds
     * @param size The image size
     * @param imageType The image type
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(Feature feature, Style style = null, Bounds bounds = null, List size = [500, 500], String imageType = "png") {
        Memory memory = new Memory()
        Layer layer = memory.create(feature.schema)
        layer.style = style ? style : Symbolizer.getDefault(feature.geom.geometryType)
        layer.add(feature)
        drawToImage(layer, bounds, size, imageType)
    }

    /**
     * Draw a Layer to an image
     * @param layer The Layer
     * @param bounds The Bounds
     * @param size The Image size
     * @param imageType The image type
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(Layer layer, Bounds bounds = null, List size = [500,500], String imageType = "png") {
        if (!bounds) bounds = layer.bounds.scale(1.1)
        Map map = new Map(
            layers: [layer],
            bounds: bounds,
            width: size[0],
            height: size[1],
            type: imageType
        )
        map.renderToImage()
    }
}

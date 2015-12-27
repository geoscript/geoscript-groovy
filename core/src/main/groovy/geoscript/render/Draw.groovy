package geoscript.render

import geoscript.feature.Feature
import geoscript.geom.Geometry
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.layer.Raster
import geoscript.style.Symbolizer
import geoscript.workspace.Memory
import java.awt.image.BufferedImage

/**
 * Easily draw a {@link geoscript.geom.Geometry Geometry}, a {@link geoscript.feature.Feature Feature}, and a {@link geoscript.layer.Layer Layer} to an image or interactive App.
 * <p><blockquote><pre>
 * import static geoscript.render.Draw.*
 * import geoscript.style.*
 * import geoscript.geom.*
 * draw(new Point(0,0).buffer(10),
 *    style: new Stroke("black",2) + new Fill("gray", 0.75),
 *    size: [250,250]
 * )
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Draw {

    /**
     * Draw a Geometry
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>format = The format ("jpeg", "png", "pdf", "svg")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param geometry The Geometry
     */
    static void draw(java.util.Map options = [:], Geometry geometry) {
        draw(options, [geometry])
    }

    /**
     * Draw a List of Geometries
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>format = The format ("jpeg", "png", "pdf", "svg")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param geometries The List of Geometries
     */
    static void draw(java.util.Map options = [:], List<Geometry> geometries) {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        layer.style = options.get("style", Symbolizer.getDefault(geometries[0].geometryType))
        geometries.each {g -> layer.add([g])}
        draw(options, layer)
    }

    /**
     * Draw a Feature
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>format = The format ("jpeg", "png", "pdf", "svg")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param feature The Feature
     */
    static void draw(java.util.Map options = [:], Feature feature) {
        Memory memory = new Memory()
        Layer layer = memory.create(feature.schema)
        layer.style = options.get("style", Symbolizer.getDefault(feature.geom.geometryType))
        layer.add(feature)
        draw(options, layer)
    }

    /**
     * Draw a Layer
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>format = The format ("jpeg", "png", "pdf", "svg")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param layer The Layer
     */
    static void draw(java.util.Map options = [:], Layer layer) {
        List size = options.get("size",[500,500])
        Map map = new Map(
                layers: [layer],
                bounds: options.get("bounds", layer.bounds.scale(1.1)),
                width: size[0],
                height: size[1],
                type: options.get("format","png"),
                proj: options.get("proj", layer.proj),
                backgroundColor: options.get("backgroundColor")
        )
        def out = options.get("out", null)
        // Display in a Window
        if (out == null) {
            Displayers.find(options.get("displayer","window"))?.display(map)
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
     * Draw a Raster
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>format = The format ("jpeg", "png", "pdf", "svg")</li>
     *  <li>proj = The Projection</li>
     * </ul>
     * @param raster The Raster
     */
    static void draw(java.util.Map options = [:], Raster raster) {
        List size = options.get("size",[500,500])
        Map map = new Map(
                layers: [raster],
                bounds: options.get("bounds", raster.bounds.scale(1.1)),
                width: size[0],
                height: size[1],
                type: options.get("format","png"),
                proj: options.get("proj", raster.proj)
        )
        def out = options.get("out", null)
        // Display in a Window
        if (out == null) {
            Displayers.find(options.get("displayer","window"))?.display(map)
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
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, format, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>imageType = The format ("jpeg", "png", "gif")</li>
     *  <li>proj = The Projection</li>
     * </ul>
     * @param geometry The Geometry
     * @return A BufferedImage
     */
    // Style style = null, Bounds bounds = null, List size = [500, 500], String imageType = "png"
    static BufferedImage drawToImage(java.util.Map options = [:], Geometry geometry) {
        drawToImage(options, [geometry])
    }

    /**
     * Draw a List of Geometries to an image
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, imageType, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>imageType = The format ("jpeg", "png", "gif")</li>
     *  <li>proj = The Projection</li>
     * </ul>
     * @param geometries A List of Geometries
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(java.util.Map options = [:], List geometries) {
        Memory memory = new Memory()
        Layer layer = memory.create("feature")
        layer.style = options.get("style", Symbolizer.getDefault(geometries[0].geometryType))
        geometries.each {g -> layer.add([g])}
        drawToImage(options, layer)
    }

    /**
     * Draw a Feature to an image
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, imageType, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>imageType = The format ("jpeg", "png", "gif")</li>
     *  <li>proj = The Projection</li>
     * </ul>
     * @param feature The Feature
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(java.util.Map options = [:], Feature feature) {
        Memory memory = new Memory()
        Layer layer = memory.create(feature.schema)
        layer.style = options.get("style", Symbolizer.getDefault(feature.geom.geometryType))
        layer.add(feature)
        drawToImage(options, layer)
    }

    /**
     * Draw a Layer to an image
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, imageType, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>imageType = The format ("jpeg", "png", "gif")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param layer The Layer
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(java.util.Map options = [:], Layer layer) {
        List size = options.get("size",[500,500])
        Map map = new Map(
                layers: [layer],
                bounds: options.get("bounds", layer.bounds.scale(1.1)),
                width: size[0],
                height: size[1],
                type: options.get("imageType","png"),
                proj: options.get("proj", layer.proj),
                backgroundColor: options.get("backgroundColor")
        )
        map.renderToImage()
    }

    /**
     * Draw a Raster to an image
     * @param options A java.util.Map of options or named parameters (style, bounds, size, out, imageType, proj)
     * <ul>
     *  <li>style = A Style</li>
     *  <li>bounds = The Bounds</li>
     *  <li>size = The size of the canvas ([400,350])</li>
     *  <li>out = The OutputStream, File, or File name.  If null (which is the default) a GUI will be opened.</li>
     *  <li>imageType = The format ("jpeg", "png", "gif")</li>
     *  <li>proj = The Projection</li>
     *  <li>backgroundColor = The background color (defaults to null)</li>
     * </ul>
     * @param raster The Raster
     * @return A BufferedImage
     */
    static BufferedImage drawToImage(java.util.Map options = [:], Raster raster) {
        List size = options.get("size",[500,500])
        Map map = new Map(
                layers: [raster],
                bounds: options.get("bounds", raster.bounds.scale(1.1)),
                width: size[0],
                height: size[1],
                type: options.get("imageType","png"),
                proj: options.get("proj", raster.proj),
                backgroundColor: options.get("backgroundColor")
        )
        map.renderToImage()
    }
}

package geoscript.render

import geoscript.geom.Bounds
import java.awt.image.BufferedImage
import java.awt.Graphics
import java.awt.Rectangle
import java.awt.Color
import java.awt.RenderingHints
import org.geotools.map.DefaultMapContext
import org.geotools.map.DefaultMapLayer
import org.geotools.renderer.lite.StreamingRenderer


/**
 * The astract base class for Renderers
 */
abstract class Renderer {

    /**
     * The GeoTools DefaultMapContext
     */
    protected DefaultMapContext map

    /**
     * Create a GeoTools StreamingRenderer
     * @param layers The List of Layers
     * @param styles The List of Styles
     * @param bounds The geographic Bounds
     * @param size The size of the canvas
     * @param options The additional options
     * @return The GeoTools StreamingRenderer
     */
    protected StreamingRenderer createStreamingRenderer(List layers, List styles, Bounds bounds, List size, java.util.Map options) {

        // Create a MapContext
        map = new DefaultMapContext(bounds?.proj?.crs)
        map.areaOfInterest = bounds.env

        // Add all of our Layers with their Style
        layers.eachWithIndex {layer, i ->
            map.addLayer(new DefaultMapLayer(layer.fs, styles[i].style))
        }

        // Create the StreamingRenderer
        StreamingRenderer renderer = new StreamingRenderer()

        // Add RenderingHints to get antialiasing for graphics and text
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        renderer.setJava2DHints(hints)

        // Add our MapContext to the StreamingRenderer
        renderer.setContext(map)

        // Return the StreamingRenderer
        renderer
    }

    /**
     * Render the List of Layers and Styles
     * @param layers The List of Layers
     * @param styles The List of Styles
     * @param bounds The geographic Bounds
     * @param size The size of the canvas
     * @param options The additional options
     */
    void render(List layers, List styles, Bounds bounds, List size, java.util.Map options = [:]) {

        // Create our StreamingRenderer
        StreamingRenderer renderer = createStreamingRenderer(layers, styles, bounds, size, options)

        // Extract width and height
        def (w,h) = size

        // Create BufferedImage
        int imageType = (this instanceof Jpeg || (this instanceof Image && this.format.equalsIgnoreCase("jpeg"))) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
        BufferedImage img = new BufferedImage(w, h, imageType)
        Graphics g = img.createGraphics()
        g.color = Color.white
        g.fillRect(0, 0, w, h)

        // Use the Renderer to paint the map onto the image
        renderer.paint(g, new Rectangle(w,h), bounds.env)

        // Most implementations will further encode the Image
        encode(img, g, size, options)

        // Dispose of the Java2D graphics
        g.dispose()
    }

    /**
     * Dispose of this Renderer
     */
    void dispose() {
        if (map) {
            map.dispose()
        }
    }

    /**
     * Encode the BufferedImage
     * @param img The BufferedImage
     * @param g The Java2D Graphics
     * @param size The size of the canvas
     * @param options The additional options
     */
    protected abstract void encode(BufferedImage img, Graphics g, List size, java.util.Map options)

}

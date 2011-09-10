package geoscript.render

import geoscript.layer.Layer
import geoscript.style.Style
import geoscript.style.Symbolizer
import geoscript.geom.Bounds
import geoscript.geom.Point

/**
 * The Map can render Layers and Styles using a Renderer.
 * @author Jared Erickson
 */
class Map {

    /**
     * The Renderer
     */
    private Renderer renderer

    /**
     * The List of Layers
     */
    private List<Layer> layers = []

    /**
     * The List of Styles
     */
    private List<Style> styles = []

    /**
     * The title
     */
    private String title

    /**
     * Create a new Map with a List of Layers and Styles and optionally a title
     * @param layers The List of Layers
     * @param styles The List of Styles
     * @param title The optional title
     */
    Map(List layers, List styles, String title = null) {

        // Add all Layers and Styles to our internal store
        this.layers.addAll(layers)
        this.styles.addAll(styles)

        // Make sure each Layer has a Style, if not create a
        // default Style based on it's geometry type
        layers.eachWithIndex {layer, i ->
            if (i >= styles.size()) {
                Style style = Symbolizer.getDefault(layer.schema.geom.typ)
                this.styles.add(style)
            }
        }

        // If not title was provided create a default
        this.title = title ? title : layers[0].schema.name
    }

    /**
     * Render this Map with the given format, Bounds, size, and addition options.
     * @param format The Renderer format (window, mapwindow, png, jpeg)
     * @param bounds The geographic Bounds
     * @param size The size of the canvas
     * @param options The additional options
     * @return The Renderer
     */
    Renderer render(String format, Bounds bounds, List size, java.util.Map options = [:]) {
        this.renderer = lookup(format)
        if (this.title && !options.containsKey("title")) {
            options["title"] = this.title
        }
        if (bounds.width == 0 || bounds.height == 0) {
            if (bounds.height > 0) {
                double h = bounds.height / 2.0
                bounds = new Bounds(bounds.west - h, bounds.south, bound.east + h, bounds.north, bounds.proj)
            } else if (bounds.width > 0) {
                double w = bounds.width / 2.0
                bounds = new Bounds(bounds.west, bounds.south - w, bound.east, bounds.north + w, bounds.proj)
            } else {
                def e = new Point(bounds.west, bounds.south).buffer(0.1).envelopeInternal
                bounds = new Bounds(e.minX, e.minY, e.maxX, e.maxY, bounds.proj)
            }
        }
        renderer.render(this.layers, this.styles, bounds, size, options)
        renderer
    }

    /**
     * Render this Map with options (format, bounds, size)
     * @param options A java.util.Map of options
     * @return The Renderer
     */
    Renderer render(java.util.Map options) {
        // Format
        String format = options.get("format", "window")
        // Bounds
        Bounds bounds
        if (options.containsKey("bounds")) {
            bounds = options.get("bounds") as Bounds
        } else {
            // Create Bounds covering all Layers
            bounds = layers[0].bounds
            layers.eachWithIndex {layer, i ->
                if (i > 1) bounds.expand(layer.bounds)
            }
        }
        // Size
        List size = options.get("size", [500,500])
        // Render
        render(format, bounds, size, options)
    }

    /**
     * Lookup a Renderer by a String
     * @param format The format name
     * @return A Renderer
     */
    private Renderer lookup(String format) {
        if (format.equalsIgnoreCase("window")) {
            return new Window()
        } else if (format.equalsIgnoreCase("mapwindow")) {
            return new MapWindow()
        } else if (format.equalsIgnoreCase("png")) {
            return new Png()
        } else if (format.equalsIgnoreCase("jpeg")) {
            return new Jpeg()
        } else {
            return new Window()
        }
    }

    /**
     * Dispose of this Map
     */
    void dispose() {
        if (renderer) {
            renderer.dispose()
        }
    }

}
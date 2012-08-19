package geoscript.render

import geoscript.filter.Color
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.raster.Raster
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import org.geotools.map.DefaultMapContext
import org.geotools.map.DefaultMapLayer
import org.geotools.map.MapContent
import org.geotools.map.MapLayer
import org.geotools.renderer.GTRenderer
import org.geotools.renderer.label.LabelCacheImpl
import org.geotools.renderer.lite.LabelCache
import org.geotools.renderer.lite.RendererUtilities
import org.geotools.renderer.lite.StreamingRenderer

import org.geotools.referencing.crs.DefaultGeographicCRS

/**
 * The GeoScript Map for rendering {@link geoscript.layer.Layer Layers}.
 * <p><blockquote><pre>
 * import geoscript.render.*
 * import geoscript.layer.*
 * import geoscript.style.*
 *
 * Map map = new Map(layers:[new Shapefile("states.shp")])
 * map.renderToImage()
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Map {

    /**
     * The width of the Map
     */
    int width = 600

    /**
     * The height of the Map
     */
    int height = 400

    /**
     * The output type (png, jpg, pdf, svg)
     */
    String type = "png"

    /**
     * The background color (if any)
     */
    String backgroundColor = null

    /**
     * A flag to fix the aspect ration (true) or not (false)
     */
    boolean fixAspectRatio = true

    /**
     * The GeoTools Renderer
     */
    protected GTRenderer renderer

    /**
     * The GeoTools MapContext
     */
    protected MapContent context

    /**
     * The LabelCache
     */
    private LabelCache labelCache = new LabelCacheImpl()

    /**
     * The List of Layers
     */
    private List layers = []

    /**
     * The Projection
     */
    private Projection projection

    /**
     * A lookup Map of Renderers by type
     */
    private java.util.Map renderers = [
        "jpeg": new Image("jpeg"),
        "jpg": new Image("jpeg"),
        "png": new Image("png"),
        "gif": new Image("gif"),
        "pdf": new Pdf(),
        "svg": new Svg()
    ]

    /**
     * Create a new Map
     */
    Map() {
        context = new DefaultMapContext()
        renderer = new StreamingRenderer()
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        renderer.setJava2DHints(hints)
        renderer.setRendererHints([
            //(StreamingRenderer.OPTIMIZED_DATA_LOADING_KEY): Boolean.TRUE,
            (StreamingRenderer.LABEL_CACHE_KEY): labelCache,
            (StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY): StreamingRenderer.SCALE_ACCURATE,
            (StreamingRenderer.LINE_WIDTH_OPTIMIZATION_KEY): Boolean.FALSE,
        ])
        renderer.setContext(context)
    }

    /**
     * Set the Map Projection
     * @param proj The Projection
     */
    void setProj(def projection) {
        this.projection = new Projection(projection)
        context.setCoordinateReferenceSystem(new Projection(projection).crs)
    }

    /**
     * Get the Map's Projection
     * @return The Map's Projection
     */
    Projection getProj() {
        this.projection
    }

    /**
     * Set the background color
     * @param color  The background color (#ffffff, red)
     */
    void setBackgroundColor(def color) {
        this.backgroundColor = Color.toHex(color)
    }

    /**
     * Add a Layer
     * @param layer The Layer
     */
    void addLayer(Layer layer) {
        layers.add(layer)
    }

    /**
     * Add a Raster
     * @param raster The Raster
     */
    void addRaster(Raster raster) {
        layers.add(raster)
    }

    /**
     * Get the Bounds
     * @return The Bounds
     */
    Bounds getBounds() {
        new Bounds(context.areaOfInterest)
    }

    /**
     * Set the Bounds
     * @param bounds The Bounds
     */
    void setBounds(Bounds bounds) {
        // If the Bounds doesn't have a Projection
        // assume it's the same as the Map
        if (bounds.proj == null) {
            bounds = new Bounds(bounds.minX, bounds.minY, bounds.maxX, bounds.maxY, getProj())
        }
        context.setAreaOfInterest(bounds.env, bounds.proj?.crs)
    }

    /**
     * Get the scale denominator
     * @param The scale denminator
     */
    double getScaleDenominator() {
        RendererUtilities.calculateOGCScale(getBounds().env, width, [:])
    }

    /**
     * Render the Map at a Bounds to a file name
     * @param bounds The Bounds
     * @param fileName The file name
     */
    void render(String fileName) {
        FileOutputStream out = new FileOutputStream(new File(fileName))
        render(out)
        out.close()
    }

    /**
     * Render the Map at a Bounds to a File
     * @param bounds The Bounds
     * @param file The File
     */
    void render(File file) {
        FileOutputStream out = new FileOutputStream(file)
        render(out)
        out.close()
    }

    /**
     * Render the Map at a Bounds to an OutputStream
     * @param out The OutputStream
     */
    void render(OutputStream out) {
        setUpRendering()
        renderers[type].render(this, out)
    }

    /**
     * Render the Map to a BufferedImage for the given Bounds
     * @return A BufferedImage
     */ 
    BufferedImage renderToImage() {
        // Look up the Renderer by type
        def r = renderers[type]
        // If the Renderer is not an Image (Pdf or Svg)
        // default to Png
        if (!(r instanceof Image)) {
            r = renderers['png']
        }
        setUpRendering()
        return r.render(this)
    }

    /**
     * Render the Map directly to a Graphics2D context
     * @param g The Graphics2D context
     */
    void render(Graphics2D g) {
        // JPEGs really need a background Color, so default to white
        if (backgroundColor == null && (type.equalsIgnoreCase("jpeg") || type.equalsIgnoreCase("jpg"))) {
            backgroundColor = "white"
        }
        if (backgroundColor != null) {
            g.color = new Color(backgroundColor).asColor()
            g.fillRect(0,0,width,height)
        }
        setUpRendering()
        renderer.paint(g, new Rectangle(0, 0, width, height), getBounds().env)
        g.dispose()
        labelCache.clear()
        context.clearLayerList()
    }

    /**
     * Display the Map in an interactive GUI
     */
    void display() {
        new MapWindow(this)
    }

    /**
     * Set up for rendering (add layers, configure bounds and projection)
     */
    protected void setUpRendering() {
        // Add Layers
        layers.each{layer ->
            MapLayer mapLayer
            if (layer instanceof Layer) {
                mapLayer = new DefaultMapLayer(layer.fs, layer.style.createGtStyle())
            } else if (layer instanceof Raster) {
                mapLayer = new DefaultMapLayer(layer.coverage, layer.style.createGtStyle())
            }
            context.addLayer(mapLayer)
        }
        // Set Bounds and Projections
        def b = getBounds()
        // If bounds is not set build it from all layers
        if (b == null || b.empty) {
            layers.each{lyr ->
                if (b == null || b.empty) {
                    b = lyr.bounds
                } else {
                    b.expand(lyr.bounds)
                }
            }
        }
        // Make sure that the Bounds has non 0 width and height
        // This covers points and horizontal/vertical lines
        b = b.ensureWidthAndHeight()
        // Fix the aspect ratio (or not)
        if (fixAspectRatio) {
            b = fixAspectRatio(width, height, b)
        }
        // If the Bounds doesn't have a Projection, assume it is the same
        // Projection as the Map.  If the Map doesn't have a Projection
        // get if from the first Layer that has a Projection
        if (b.proj == null) {
            def p = getProj()
            if (p == null || p.crs == null) {
                layers.each{layer->
                    if (layer.proj != null) {
                        p = layer.proj
                        setProj(p)
                        return
                    }
                }
            }
            // Apply a default Projection or GeoTools will throw Exceptions
            // Should this be EPSG:4326 or
            // CartesianAuthorityFactory.GENERIC_2D
            if (p == null || p.crs == null) {
                p = new Projection(DefaultGeographicCRS.WGS84)
            }
            b = new Bounds(b.minX, b.minY, b.maxX, b.maxY, p)
        }
        setBounds(b)
    }

    /**
     * Fix the aspect ration
     * @param w The image width
     * @param h The image height
     * @param mapBounds The geographic/map Bounds
     */
    private Bounds fixAspectRatio(int w, int h, Bounds mapBounds) {
        double mapWidth = mapBounds.width
        double mapHeight = mapBounds.height
        double scaleX = w / mapWidth
        double scaleY = h / mapHeight
        double scale
        if (scaleX < scaleY) {
            scale = scaleX
        } else {
            scale = scaleY
        }
        double deltaX = w / scale - mapWidth
        double deltaY = h / scale - mapHeight
        double minX = mapBounds.minX - deltaX / 2D
        double maxX = mapBounds.maxX + deltaX / 2D
        double minY = mapBounds.minY - deltaY / 2D
        double maxY = mapBounds.maxY + deltaY / 2D
        return new Bounds(minX, minY, maxX, maxY, mapBounds.proj)
    }

    /**
     * Closes the Map by disposing of any resources.
     */
    void close() {
        context.dispose()
    }
}


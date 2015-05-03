package geoscript.render

import geoscript.filter.Color
import geoscript.geom.Bounds
import geoscript.layer.ImageTileLayer
import geoscript.layer.Layer
import geoscript.layer.VectorTiles
import geoscript.layer.WMS
import geoscript.proj.Projection
import geoscript.layer.Raster
import geoscript.style.RasterSymbolizer
import geoscript.layer.TileLayer
import org.geotools.map.FeatureLayer
import org.geotools.map.GridCoverageLayer
import org.geotools.map.WMSLayer

import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import org.geotools.map.MapContent
import org.geotools.map.Layer as GtLayer
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
     * The output type (png, jpg, pdf, svg, base64)
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
     * The GeoTools MapContent
     */
    protected MapContent content

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
        "svg": new Svg(),
        "base64":  new Base64(),
        "ascii" : new ASCII()
    ]

    /**
     * Create a new Map
     */
    Map() {
        content = new MapContent()
        renderer = new StreamingRenderer()
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON))
        renderer.setJava2DHints(hints)
        renderer.setRendererHints([
            (StreamingRenderer.LABEL_CACHE_KEY): labelCache,
            (StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY): StreamingRenderer.SCALE_ACCURATE,
            (StreamingRenderer.LINE_WIDTH_OPTIMIZATION_KEY): Boolean.FALSE,
            (StreamingRenderer.ADVANCED_PROJECTION_HANDLING_KEY): true,
            (StreamingRenderer.CONTINUOUS_MAP_WRAPPING): true
        ])
        renderer.setMapContent(content)
    }

    /**
     * Set the Map Projection
     * @param proj The Projection
     */
    void setProj(def projection) {
        this.projection = new Projection(projection)
        content.viewport.setCoordinateReferenceSystem(this.projection.crs)
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
     * Add a WMS Layer
     * @param wms The WMS Layer
     */
    void addWMSLayer(WMS wms) {
        layers.add(wms)
    }

    /**
     * Add a TileLayer
     * @param layer The TileLayer
     */
    void addTileLayer(TileLayer layer) {
        layers.add(layer)
    }

    /**
     * Get the Bounds
     * @return The Bounds
     */
    Bounds getBounds() {
        new Bounds(content.viewport.bounds)
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
        content.viewport.bounds = bounds.env
        content.viewport.coordinateReferenceSystem = bounds.proj?.crs
    }

    /**
     * Get the scale denominator
     * @param The scale denminator
     */
    double getScaleDenominator() {
        RendererUtilities.calculateOGCScale(getBounds().env, width, [:])
    }

    /**
     * Set the scale computation algorithm (accurate or ogc). Accurate the default.
     * @param type The scale computation algorithm (accurate or ogc).
     * @return This Map
     */
    Map setScaleComputation(String type) {
        java.util.Map hints = renderer.rendererHints
        hints[StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY] = type.equalsIgnoreCase("accurate") ?
                StreamingRenderer.SCALE_ACCURATE : StreamingRenderer.SCALE_OGC
        renderer.rendererHints = hints
        this
    }

    /**
     * Set whether to use advanced projection handling or not.  The default is true.
     * @param value Whether to use advanced projection handling or not.
     * @return This Map
     */
    Map setAdvancedProjectionHandling(boolean value) {
        java.util.Map hints = renderer.rendererHints
        hints[StreamingRenderer.ADVANCED_PROJECTION_HANDLING_KEY] = value
        renderer.rendererHints = hints
        this
    }

    /**
     * Set whether to enable continuous map wrapping or not. The default is true.
     * @param value Whether to enable continuous map wrapping ot not
     * @return This Map
     */
    Map setContinuousMapWrapping(boolean value) {
        java.util.Map hints = renderer.rendererHints
        hints[StreamingRenderer.CONTINUOUS_MAP_WRAPPING] = value
        renderer.rendererHints = hints
        this
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
        content.layers().clear()
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
        layers.each { layer ->
            GtLayer mapLayer
            if (layer instanceof Layer) {
                mapLayer = new FeatureLayer(layer.fs, layer.style.gtStyle)
            } else if (layer instanceof Raster) {
                mapLayer = new GridCoverageLayer(layer.coverage, layer.style.gtStyle)
            } else if (layer instanceof ImageTileLayer) {
                ImageTileLayer tileLayer = layer as ImageTileLayer
                def raster = tileLayer.getRaster(this.bounds.reproject(tileLayer.proj), this.width, this.height)
                mapLayer = new GridCoverageLayer(raster.coverage, new RasterSymbolizer().gtStyle)
            } else if (layer instanceof VectorTiles) {
                VectorTiles vectorTiles = layer as VectorTiles
                vectorTiles.getLayers(vectorTiles.tiles(this.bounds.reproject(vectorTiles.proj), this.width, this.height)).each { Layer lyr ->
                    content.addLayer(new FeatureLayer(lyr.fs, lyr.style.gtStyle))
                }
            } else if (layer instanceof geoscript.layer.WMSLayer) {
                def wmsLayer = layer as geoscript.layer.WMSLayer
                def gtWmsLayer = new WMSLayer(wmsLayer.wms.wms, wmsLayer.layers[0].layer)
                (1..<wmsLayer.layers.size()).each{i ->
                    gtWmsLayer.addLayer(wmsLayer.layers[i].layer)
                }
                mapLayer = gtWmsLayer
            } else if (layer instanceof GtLayer) {
                mapLayer = layer
            }
            if (mapLayer) {
                content.addLayer(mapLayer)
            }
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
        // Set width and height
        content.viewport.setScreenArea(new Rectangle(width, height))
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
        content.dispose()
    }
}


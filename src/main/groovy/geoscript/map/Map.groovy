package geoscript.map

import geoscript.layer.Layer
import geoscript.style.Style
import geoscript.geom.Bounds
import geoscript.proj.Projection
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Graphics2D
import java.awt.Rectangle
import java.awt.Color
import org.geotools.map.DefaultMapContext
import org.geotools.map.DefaultMapLayer
import org.geotools.map.MapContext
import org.geotools.map.MapLayer
import org.geotools.renderer.GTRenderer
import org.geotools.renderer.lite.StreamingRenderer
import org.geotools.renderer.lite.RendererUtilities
import org.geotools.renderer.lite.LabelCache
import org.geotools.renderer.label.LabelCacheImpl

/**
 * The GeoScript Map for rendering Layers as Images
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
     * The image type (png, jpg)
     */
    String imageType = "png"

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
    private GTRenderer renderer

    /**
     * The GeoTools MapContext
     */
    private MapContext context

    /**
     * The LabelCache
     */
    private LabelCache labelCache = new LabelCacheImpl()

    /**
     * The List of Layers
     */
    private List layers = []

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
            (StreamingRenderer.OPTIMIZED_DATA_LOADING_KEY): Boolean.TRUE,
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
    void setProj(def proj) {
        context.setCoordinateReferenceSystem(new Projection(proj).crs)
    }

    /**
     * Get the Map's Projection
     * @return The Map's Projection
     */
    Projection getProj() {
        new Projection(context.coordinateReferenceSystem)
    }

    /**
     * Add a Layer with a Style
     * @param layer The Layer
     * @param style The Style
     */
    void addLayer(Layer layer) {
        layers.add(layer)
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
        context.areaOfInterest = bounds.env
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
        ImageIO.write(renderToImage(), imageType, out)
    }
   
    /**
     * Render the Map to a BufferedImage for the given Bounds
     * @return A BufferedImage
     */ 
    BufferedImage renderToImage() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g = (Graphics2D) image.createGraphics()
        if (backgroundColor != null) {
            g.color = Style.getColor(backgroundColor)
            g.fillRect(0,0,width,height)
        }
        def b = getBounds()
        if (fixAspectRatio) {
            b = fixAspectRatio(width, height, b)
        }
        // If the Bounds doesn't have a Projection, assume it is the same
        // Projection as the Map
        if (b.proj == null) {
            b = new Bounds(b.l, b.r, b.b, b.t, getProj())
        }
        layers.each{layer ->
            MapLayer mapLayer = new DefaultMapLayer(layer.fs, layer.style.gtStyle)
            context.addLayer(mapLayer)
        }
        renderer.paint(g, new Rectangle(0, 0, width, height), b.env)
        g.dispose()
        labelCache.clear()
        context.clearLayerList()
        return image
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
        double scale = 1.0D
        if (scaleX < scaleY) {
            scale = scaleX
        } else {
            scale = scaleY
        }
        double deltaX = w / scale - mapWidth
        double deltaY = h / scale - mapHeight
        double l = mapBounds.l - deltaX / 2D
        double r = mapBounds.r + deltaX / 2D
        double b = mapBounds.b - deltaY / 2D
        double t = mapBounds.t + deltaY / 2D
        return new Bounds(l, b, r, t, mapBounds.proj)
    }

    /**
     * Closes the Map by disposing of any resources.
     */
    void close() {
        context.dispose()
    }
}


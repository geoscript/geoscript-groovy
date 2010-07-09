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
            (StreamingRenderer.LABEL_CACHE_KEY): new LabelCacheImpl(),
            (StreamingRenderer.SCALE_COMPUTATION_METHOD_KEY): StreamingRenderer.SCALE_ACCURATE,
            (StreamingRenderer.LINE_WIDTH_OPTIMIZATION_KEY): Boolean.FALSE,
        ])
        renderer.setContext(context)
    }

    /**
     * Set the Map Projection
     * @param proj The Projection
     */
    void setProjection(Projection proj) {
        context.setCoordinateReferenceSystem(proj.crs)
    }

    /**
     * Get the Map's Projection
     * @return The Map's Projection
     */
    Projection getProjection() {
        new Projection(context.coordinateReferenceSystem)
    }

    /**
     * Set the List of Layers
     * @param The List of Layers
     */
    void setLayers(List<Layer> layers) {
        context.clearLayerList()
        layers.each{layer->addLayer(layer)}
    }

    /**
     * Get the List of Layers
     * @return The List of Layers
     */
    List<Layer> getLayers() {
        context.layers.collect{mapLayer-> new Layer(mapLayer.featureSource)}
    }

    /**
     * Add a Layer with a Style
     * @param layer The Layer
     * @param style The Style
     */
    void addLayer(Layer layer) {
        MapLayer mapLayer = new DefaultMapLayer(layer.fs, layer.style.gtStyle);
        context.addLayer(mapLayer);
    }

    /**
     * Render the Map at a Bounds to a File
     * @param bounds The Bounds
     * @param file The File
     */
    void render(Bounds bounds, File file) {
        FileOutputStream out = new FileOutputStream(file)
        render(bounds, out)
        out.close()
    }

    /**
     * Render the Map at a Bounds to an OutputStream
     * @param bounds The Bounds
     * @param out The OutputStream
     */
    void render(Bounds bounds, OutputStream out) {
        ImageIO.write(renderToImage(bounds), imageType, out);
    }
   
    /**
     * Render the Map to a BufferedImage for the given Bounds
     * @param bounds The Bounds
     * @return A BufferedImage
     */ 
    BufferedImage renderToImage(Bounds bounds) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.createGraphics();
        if (backgroundColor != null) {
            g.color = Color.decode(backgroundColor)
            g.fillRect(0,0,width,height)
        }
        if (fixAspectRatio) {
            bounds = fixAspectRatio(width, height, bounds)
        }
        renderer.paint(g, new Rectangle(0, 0, width, height), bounds.env)
        return image
    }

    /**
     * Fix the aspect ration
     * @param w The image width
     * @param h The image height
     * @param mapBounds The geographic/map Bounds
     */
    private Bounds fixAspectRatio(int w, int h, Bounds mapBounds) {
        double mapWidth = mapBounds.width;
        double mapHeight = mapBounds.height;
        double scaleX = w / mapWidth;
        double scaleY = h / mapHeight;
        double scale = 1.0D;
        if (scaleX < scaleY) {
            scale = scaleX;
        } else {
            scale = scaleY;
        }
        double deltaX = w / scale - mapWidth;
        double deltaY = h / scale - mapHeight;
        double l = mapBounds.l - deltaX / 2D;
        double r = mapBounds.r + deltaX / 2D;
        double b = mapBounds.b - deltaY / 2D;
        double t = mapBounds.t + deltaY / 2D;
        return new Bounds(l, b, r, t, mapBounds.proj);
    }

    /**
     * Closes the Map by disposing of any resources.
     */
    void close() {
        context.dispose()
    }
}


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

    int width = 600

    int height = 400

    String imageType = "png"

    String backgroundColor = null

    boolean fixAspectRatio = true

    private GTRenderer renderer

    private MapContext context

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

    void setProjection(Projection proj) {
        context.setCoordinateReferenceSystem(proj.crs)
    }

    void addLayer(Layer layer, Style style) {
        MapLayer mapLayer = new DefaultMapLayer(layer.fs, style.style);
        context.addLayer(mapLayer);
    }

    void render(Bounds bounds, File file) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.createGraphics();
        if (backgroundColor != null) {
            g.color = Color.WHITE
            g.fillRect(0,0,width,height)
        }
        if (fixAspectRatio) {
            bounds = fixAspectRatio(width, height, bounds)
        }
        renderer.paint(g, new Rectangle(0, 0, width, height), bounds.env);
        ImageIO.write(image, imageType, file);
    }

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

}


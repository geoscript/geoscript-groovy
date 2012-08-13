package geoscript.render

import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * A Renderer that draws a {@link geoscript.render.Map Map} to a BufferedImage.
 * <p><blockquote><pre>
 * import java.awt.image.*
 * import geoscript.render.*
 * import geoscript.layer.*
 * import geoscript.style.*
 *
 * Map map = new Map(layers:[new Shapefile("states.shp")])
 * Image image = new Image("png")
 * BufferedImage img = image.render(map)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Image extends Renderer<BufferedImage> {

    /**
     * The image type (png, gif, jpeg)
     */
    private String imageType

    /**
     * Create a new Image
     * @param type The image type
     */
    Image(String type) {
        this.imageType = type
    }

    /**
     * Render the Map to a BufferedImage
     * @param map The Map
     * @return A BufferedImage
     */
    @Override
    public BufferedImage render(Map map) {
        int type = (imageType.equalsIgnoreCase("jpeg") || imageType.equalsIgnoreCase("jpeg") ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB)
        BufferedImage image = new BufferedImage(map.width, map.height, type)
        Graphics2D g = (Graphics2D) image.createGraphics()
        map.render(g)
        g.dispose()
        return image
    }

    /**
     * Render the Map to the OutputStream
     * @param map The Map
     * @param out The OuptuStream
     */
    @Override
    public void render(Map map, OutputStream out) {
        def image = render(map)
        ImageIO.write(image, imageType, out)
    }
}

package geoscript.render

import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * A Renderer that creates an image File
 * @author Jared Erickson
 */
class Image extends Renderer {

    /**
     * The image format (png, jpeg, gif)
     */
    String format

    /**
     * Create a new Image Renderer
     * @param format The image format (png, jpeg, gif)
     */
    Image(String format) {
        this.format = format
    }

    /**
     * Encode the BufferedImage as an image File
     * @param img The BufferedImage
     * @param g The Java2D Graphics
     * @param size The size of the image
     * @param options A Map of options
     */
    protected void encode(BufferedImage img, Graphics g, List size, java.util.Map options) {
        File file
        def f = options.get("file", "${options.title}.${format}")
        if (f instanceof File) {
            file = f
        } else {
            file = new File(f)
        }
        ImageIO.write(img, format, file)
    }
}
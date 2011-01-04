package geoscript.print

import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color

/**
 * The ImageProducer can write png, gif, or jpeg Images.
 * @author Jared Erickson
 */
class ImageProducer implements Producer {

    /**
     * Does this Producer support the given mime type?
     * @param mimeType The mime type
     * @return Whether this Producer handles the mime type
     */
    boolean handlesMimeType(String mimeType) {
        if (mimeType.equalsIgnoreCase("image/png") || mimeType.equalsIgnoreCase("png") ||
            mimeType.equalsIgnoreCase("image/gif") || mimeType.equalsIgnoreCase("gif") ||
            mimeType.equalsIgnoreCase("image/jpeg") || mimeType.equalsIgnoreCase("jpeg") ||
            mimeType.equalsIgnoreCase("jpg")) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Write the print Template to the OutputStream in the given mime type
     * @param template The print Template
     * @param mimeType The mime type
     * @param out The OutputStream
     */
    void produce(Template template, String mimeType, OutputStream out) {
        ImageIO.write(produce(template, mimeType), getImageType(mimeType), out)
    }

    /**
     * Get the ImageIO image type from the mime type (image/png => png).
     * @param mimeType The mime type (image/png)
     * @return The ImageIO image type (png)
     */
    private String getImageType(String mimeType) {
        int i = mimeType.lastIndexOf("/")
        String type = mimeType.toLowerCase()
        if (i > -1) {
            type = type.substring(i + 1)
        }
        if (type.equals("jpg")) {
            type = "jpeg"
        }
        type
    }

    /**
     * Write the print Template to the OutputStream in the given mime type
     * @param template The print Template
     * @param mimeType The mime type (defaults to image/png)
     * @param out The OutputStream
     */
    BufferedImage produce(Template template, String mimeType = "image/png") {
        int imageType = mimeType.equalsIgnoreCase("image/jpeg") ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB
        BufferedImage image = new BufferedImage(template.width, template.height, imageType)
        Graphics g = image.createGraphics()
        g.renderingHints = [
            (RenderingHints.KEY_ANTIALIASING): RenderingHints.VALUE_ANTIALIAS_ON,
            (RenderingHints.KEY_TEXT_ANTIALIASING): RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ]
        template.draw(g)
        g.dispose()
        image
    }

}


package geoscript.print

import java.awt.Graphics
import javax.imageio.ImageIO

/**
 * The ImageItem can display an Image
 * @author Jared Erickson
 */
class ImageItem extends Item {

    /**
     * The File or URL path to the Image
     */
    def path

    /**
     * Draw the ImageItem
     * @g The Graphics context
     */ 
    void draw(Graphics g) {
        def image = ImageIO.read(path)
        g.drawImage(image,x,y, null)
    }
}


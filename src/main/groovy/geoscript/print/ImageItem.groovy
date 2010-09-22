package geoscript.print

import java.awt.Graphics
import javax.imageio.ImageIO

/**
 * An Image Item
 * @author Jared Erickson
 */
class ImageItem extends Item {

    def path

    void draw(Graphics g) {
        def image = ImageIO.read(path)
        g.drawImage(image,x,y, null)
    }

}


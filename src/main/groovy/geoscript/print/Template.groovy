package geoscript.print

import java.awt.Graphics
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import java.awt.Color

/**
 * A Print Template
 * @author Jared Erickson
 */
class Template {

    int width
    int height
    List<Item> items;
    Color backgroundColor = Color.WHITE

    BufferedImage render() {
        BufferedImage image = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB)
        Graphics g = image.createGraphics()
        g.renderingHints = [
            (RenderingHints.KEY_ANTIALIASING): RenderingHints.VALUE_ANTIALIAS_ON,
            (RenderingHints.KEY_TEXT_ANTIALIASING): RenderingHints.VALUE_TEXT_ANTIALIAS_ON
        ]
        g.color = backgroundColor
        g.fillRect(0,0,width,height)
        items.each{item ->
            item.draw(g)
        }
        g.dispose()
        image
    }

}



package geoscript.print

import geoscript.map.Map
import java.awt.Graphics
import javax.imageio.ImageIO

/**
 *
 * @author jericks
 */
class MapItem extends Item {

    Map map

    void draw(Graphics g) {
        map.width = width
        map.height = height
        g.drawImage(map.renderToImage(), x, y, null)
    }

}


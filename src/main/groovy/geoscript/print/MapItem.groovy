package geoscript.print

import geoscript.map.Map
import java.awt.Graphics
import javax.imageio.ImageIO

/**
 * The MapItem can display a geoscript.map.Map on the print template.
 * @author Jared Erickson
 */
class MapItem extends Item {

    /**
     * The geoscript.map.Map
     */
    Map map

    /**
     * Draw the MapItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        map.width = width
        map.height = height
        g.drawImage(map.renderToImage(), x, y, null)
    }
}


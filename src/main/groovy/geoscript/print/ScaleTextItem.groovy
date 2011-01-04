package geoscript.print

import geoscript.map.Map
import java.awt.Graphics
import java.awt.Font
import java.awt.Color
import java.text.DecimalFormat

/**
 * The ScaleTextItem can display the geoscript.map.Map's current scale (e.g. 1:500).
 * @author Jared Erickson
 */
class ScaleTextItem extends Item {

    /**
     * The geoscript.map.Map
     */
    Map map

    /**
     * The text Color
     */
    Color color = Color.BLACK

    /**
     * The text Font
     */
    Font font = new Font("Default", Font.PLAIN, 12)

    /**
     * The number format string
     */
    String format = "#"

    /**
     * Draw the ScaleTextItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        g.color = color
        g.font = font
        def formatter = new DecimalFormat(format)
        g.drawString("1:${formatter.format(map.scaleDenominator)}", x, y)
    }
}
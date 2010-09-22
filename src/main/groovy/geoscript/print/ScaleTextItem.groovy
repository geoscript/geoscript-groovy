package geoscript.print

import geoscript.map.Map
import java.awt.Graphics
import java.awt.Font
import java.awt.Color
import java.text.DecimalFormat

/**
 * A Scale Text Item 1:500
 * @author Jared Erickson
 */
class ScaleTextItem extends Item {

    Map map
    String text
    Color color = Color.BLACK
    Font font = new Font("Default", Font.PLAIN, 12)
    String format = "#"

    void draw(Graphics g) {
        g.color = color
        g.font = font
        def formatter = new DecimalFormat(format)
        g.drawString("1:${formatter.format(map.scaleDenominator)}", x, y)
    }

}


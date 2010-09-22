package geoscript.print

import java.awt.Color
import java.awt.Graphics
import java.awt.Font
import java.awt.BasicStroke

/**
 * A Recangle Item
 * @author Jared Erickson
 */
class RectangleItem extends Item {

    Color strokeColor = Color.BLACK
    Color fillColor = Color.WHITE
    float strokeWidth = 1

    void draw(Graphics g) {
        g.color = fillColor
        g.fillRect(x,y,width,height)
        g.color = strokeColor
        g.stroke = new BasicStroke(strokeWidth)
        g.drawRect(x,y,width,height)
    }

}


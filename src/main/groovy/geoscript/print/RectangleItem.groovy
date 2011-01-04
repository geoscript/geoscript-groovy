package geoscript.print

import java.awt.Color
import java.awt.Graphics
import java.awt.Font
import java.awt.BasicStroke

/**
 * The RectangleItem can display rectangles
 * @author Jared Erickson
 */
class RectangleItem extends Item {

    /**
     * The stroke Color
     */
    Color strokeColor = Color.BLACK

    /**
     * The fill Color
     */
    Color fillColor = Color.WHITE

    /**
     * The stroke width
     */
    float strokeWidth = 1

    /**
     * Draw the RectangleItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        g.color = fillColor
        g.fillRect(x,y,width,height)
        g.color = strokeColor
        g.stroke = new BasicStroke(strokeWidth)
        g.drawRect(x,y,width,height)
    }
}


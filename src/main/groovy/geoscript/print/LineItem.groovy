package geoscript.print

import java.awt.Graphics
import java.awt.Color
import java.awt.BasicStroke

/**
 * The LineItem can draw a Line
 * @author Jared Erickson
 */
class LineItem extends Item {

    /**
     * The stroke color
     */
    Color strokeColor = Color.BLACK

    /**
     * The stroke width
     */
    float strokeWidth = 1

    /**
     * Draw the LineItem
     * @param g The Graphics
     */
    void draw(Graphics g) {
        g.color = strokeColor
        g.stroke = new BasicStroke(strokeWidth)
        g.drawLine(x,y,x+width,y+height)
    }
}


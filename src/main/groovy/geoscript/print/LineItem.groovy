package geoscript.print

import java.awt.Graphics
import java.awt.Color
import java.awt.BasicStroke

/**
 *
 * @author jericks
 */
class LineItem extends Item {

    Color strokeColor = Color.BLACK
    float strokeWidth = 1

    void draw(Graphics g) {
        g.color = strokeColor
        g.stroke = new BasicStroke(strokeWidth)
        g.drawLine(x,y,x+width,y+height)
    }


}


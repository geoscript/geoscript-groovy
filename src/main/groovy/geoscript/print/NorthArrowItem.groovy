package geoscript.print

import java.awt.Color
import java.awt.Graphics
import java.awt.BasicStroke
import java.awt.geom.GeneralPath

/**
 * A NorthArrow Item
 * @author jericks
 */
class NorthArrowItem extends Item {

    Color fillColor1 = Color.BLACK
    Color strokeColor1 = Color.BLACK

    Color fillColor2 = Color.WHITE
    Color strokeColor2 = Color.BLACK

    float strokeWidth = 1

    void draw(Graphics g) {

        def path1 = new GeneralPath()
        path1.moveTo(x + width/2, y)
        path1.lineTo(x, y + height)
        path1.lineTo(x + width / 2, y + height * 3/4)
        path1.closePath()

        def path2 = new GeneralPath()
        path2.moveTo(x + width/2, y)
        path2.lineTo(x + width, y + height)
        path2.lineTo(x + width/2, y + height * 3/4)
        path2.closePath()

        g.stroke = new BasicStroke(strokeWidth)

        g.color = fillColor1
        g.fill(path1)
        g.color = strokeColor1
        g.draw(path1)

        g.color = fillColor2
        g.fill(path2)
        g.color = strokeColor2
        g.draw(path2)
    }

}


package geoscript.print

import java.awt.Graphics

/**
 * A Print Template Item
 * @author Jared Erickson
 */
abstract class Item {

    int x
    int y
    int width
    int height

    abstract void draw(Graphics g)

}


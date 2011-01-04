package geoscript.print

import java.awt.Graphics

/**
 * The base print Template Item.  This is the base class for all other Item implementations
 * that can display something on a Java2D Graphics object.
 * @author Jared Erickson
 */
abstract class Item {

    /**
     * The x coordinate
     */
    int x

    /**
     * The y coordinate
     */
    int y

    /**
     * The width
     */
    int width

    /**
     * The height
     */
    int height

    /**
     * Draw on the Graphics context
     * @param g The Graphics context
     */
    abstract void draw(Graphics g)

}


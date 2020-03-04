package geoscript.carto

import java.awt.Rectangle

abstract class Item {

    final int x

    final int y

    final int width

    final int height

    Item(int x, int y, int width, int height) {
        this.x = x
        this.y = y
        this.width = width
        this.height = height
    }

    Rectangle getRectangle() {
        return new Rectangle(x, y, width, height)
    }

}

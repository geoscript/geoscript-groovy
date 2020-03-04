package geoscript.carto

import java.awt.Color

class LineItem extends Item {

    Color strokeColor = Color.BLACK

    float strokeWidth = 1

    LineItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    LineItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    LineItem strokeWidth(float width) {
        this.strokeWidth = width
        this
    }

    @Override
    String toString() {
        "LineItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, stroke-color = ${strokeColor}, stroke-width = ${strokeWidth})"
    }

}

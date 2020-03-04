package geoscript.carto

import java.awt.Color

class RectangleItem extends Item {

    Color strokeColor = Color.BLACK

    Color fillColor

    float strokeWidth = 1

    RectangleItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    RectangleItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    RectangleItem fillColor(Color color) {
        this.fillColor = color
        this
    }

    RectangleItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "RectangleItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, stroke-color = ${strokeColor}, fill-color = ${fillColor}, stroke-width = ${strokeWidth})"
    }

}

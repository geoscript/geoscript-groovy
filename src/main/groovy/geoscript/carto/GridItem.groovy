package geoscript.carto

import java.awt.Color

class GridItem extends Item {

    int size = 10

    Color strokeColor = Color.LIGHT_GRAY

    float strokeWidth = 1

    GridItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    GridItem size(int size) {
        this.size = size
        this
    }

    GridItem strokeColor(Color color) {
        this.strokeColor = color
        this
    }

    GridItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "GridItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, size = ${size}, stroke-color = ${strokeColor}, stroke-width = ${strokeWidth})"
    }




}

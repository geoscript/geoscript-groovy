package geoscript.carto

import java.awt.Color

class NorthArrowItem extends Item {

    Color fillColor1 = Color.BLACK

    Color strokeColor1 = Color.BLACK

    Color fillColor2 = Color.WHITE

    Color strokeColor2 = Color.BLACK

    float strokeWidth = 1

    NorthArrowItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    NorthArrowItem strokeColor1(Color color) {
        this.strokeColor1 = color
        this
    }

    NorthArrowItem fillColor1(Color color) {
        this.fillColor1 = color
        this
    }

    NorthArrowItem strokeColor2(Color color) {
        this.strokeColor2 = color
        this
    }

    NorthArrowItem fillColor2(Color color) {
        this.fillColor2 = color
        this
    }

    NorthArrowItem strokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth
        this
    }

    @Override
    String toString() {
        "NorthArrowItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, fill-color1 = ${fillColor1}, stroke-color1 = ${strokeColor1}), fill-color2 = ${fillColor2}, stroke-color2 = ${strokeColor2}, stroke-width = ${strokeWidth}"
    }

}

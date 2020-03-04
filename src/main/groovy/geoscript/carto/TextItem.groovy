package geoscript.carto

import java.awt.Color
import java.awt.Font

class TextItem extends Item {

    String text

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    TextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    TextItem text(String text) {
        this.text = text
        this
    }

    TextItem color(Color color) {
        this.color = color
        this
    }

    TextItem font(Font font) {
        this.font = font
        this
    }

    TextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    TextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "TextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, text = ${text}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign})"
    }
}

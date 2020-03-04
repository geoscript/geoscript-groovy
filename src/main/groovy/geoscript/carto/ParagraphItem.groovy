package geoscript.carto

import java.awt.Color
import java.awt.Font

class ParagraphItem extends Item {

    String text

    Font font = new Font("Arial", Font.PLAIN, 12)

    Color color = Color.BLACK

    ParagraphItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    ParagraphItem text(String text) {
        this.text = text
        this
    }

    ParagraphItem color(Color color) {
        this.color = color
        this
    }

    ParagraphItem font(Font font) {
        this.font = font
        this
    }

    @Override
    String toString() {
        "ParagraphItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, text = ${text}, color = ${color}, font = ${font})"
    }

}

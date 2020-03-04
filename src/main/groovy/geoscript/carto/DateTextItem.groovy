package geoscript.carto

import java.awt.Color
import java.awt.Font


class DateTextItem extends Item {

    String format = "MM/dd/yyyy"

    Date date = null

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    DateTextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    DateTextItem format(String format) {
        this.format = format
        this
    }

    DateTextItem date(Date date) {
        this.date = date
        this
    }

    DateTextItem color(Color color) {
        this.color = color
        this
    }

    DateTextItem font(Font font) {
        this.font = font
        this
    }

    DateTextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    DateTextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "DateTextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, date = ${date}, format = ${format}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign})"
    }

}

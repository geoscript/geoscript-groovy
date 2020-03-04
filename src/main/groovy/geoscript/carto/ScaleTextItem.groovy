package geoscript.carto

import geoscript.render.Map

import java.awt.Color
import java.awt.Font

class ScaleTextItem extends Item {

    Map map

    Color color = Color.BLACK

    Font font = new Font("Arial", Font.PLAIN, 12)

    HorizontalAlign horizontalAlign = HorizontalAlign.LEFT

    VerticalAlign verticalAlign = VerticalAlign.BOTTOM

    String format = "#"

    String prefixText = "Scale: "

    ScaleTextItem(int x, int y, int width, int height) {
        super(x, y, width, height)
    }

    ScaleTextItem map(Map map) {
        this.map = map
        this
    }

    ScaleTextItem format(String format) {
        this.format = format
        this
    }

    ScaleTextItem prefixText(String prefixText) {
        this.prefixText = prefixText
        this
    }

    ScaleTextItem color(Color color) {
        this.color = color
        this
    }

    ScaleTextItem font(Font font) {
        this.font = font
        this
    }

    ScaleTextItem horizontalAlign(HorizontalAlign horizontalAlign) {
        this.horizontalAlign = horizontalAlign
        this
    }

    ScaleTextItem verticalAlign(VerticalAlign verticalAlign) {
        this.verticalAlign = verticalAlign
        this
    }

    @Override
    String toString() {
        "ScaleTextItem(x = ${x}, y = ${y}, width = ${width}, height = ${height}, prefixText = ${prefixText}, color = ${color}, font = ${font}, horizontal-align = ${horizontalAlign}, vertical-align = ${verticalAlign}, map = ${map}, format = ${format})"
    }
}

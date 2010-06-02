package geoscript.style

import org.geotools.styling.SLD

/**
 *
 * @author jericks
 */
class PolygonSymbolizer  extends Symbolizer {

    PolygonSymbolizer() {
        super(Style.builder.createPolygonSymbolizer())
    }

    void setStrokeColor(String color) {
        SLD.stroke(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    void setStrokeWidth(float width) {
        SLD.stroke(symbolizer).setWidth(Style.filterFactory.literal(width))
    }

    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    void setFillColor(String color) {
        SLD.fill(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    void setFillOpacity(float opacity) {
        SLD.fill(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }
}


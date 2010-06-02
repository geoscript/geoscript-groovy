package geoscript.style

import org.geotools.styling.SLD

/**
 * 
 * @author jericks
 */
class PointSymbolizer extends Symbolizer {

    PointSymbolizer() {
        super(Style.builder.createPointSymbolizer())
    }

    void setShape(String shape) {
        SLD.mark(symbolizer).setWellKnownName(Style.filterFactory.literal(shape))
    }

    void setSize(float size) {
        SLD.graphic(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    void setRotation(float rotation) {
        SLD.graphic(symbolizer).setRotation(Style.filterFactory.literal(rotation))
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


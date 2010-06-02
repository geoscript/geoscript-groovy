package geoscript.style

import geoscript.feature.Field

/**
 *
 * @author jericks
 */
class TextSymbolizer  extends Symbolizer {

    TextSymbolizer() {
        super(Style.builder.createTextSymbolizer())
    }

    void setField(Field field) {
        symbolizer.label = Style.filterFactory.property(field.name)
    }

    void setFontFamily(String name) {
        SLD.font(symbolizer).setFontFamily(Style.filterFactory.literal(name))
    }

    void setFontSize(int size) {
        SLD.font(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    void setFontStyle(String style) {
        SLD.font(symbolizer).setStyle(Style.filterFactory.literal(style))
    }

    void setFontWeight(String weight) {
        SLD.font(symbolizer).setWeight(Style.filterFactory.literal(weight))
    }

    void setHaloFill(String color) {
        if (symbolizer.halo == null) {
            symbolizer.halo = Style.builder.createHalo()
        }
        symbolizer.halo.fill.color = Style.filterFactory.literal(color)
    }

    void setHaloRadius(float radius) {
        if (symbolizer.halo == null) {
            symbolizer.halo = Style.builder.createHalo()
        }
        symbolizer.halo.radius = Style.filterFactory.literal(radius)
    }

    // LinePlacement or PointPlacement
    void setPlacement() {

    }

}


package geoscript.style


import org.geotools.styling.SLD

/**
 * A LineSymbolizer
 * @author Jared Erickson
 */
class LineSymbolizer  extends Symbolizer {

    /**
     * Create a new LineSymbolizer
     */
    LineSymbolizer() {
        super(Style.builder.createLineSymbolizer())
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

}


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

    /**
     * Set the stroke color
     * @param color The color
     */
    void setStrokeColor(String color) {
        SLD.stroke(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Set the stroke width
     * @param width The stroke width
     */
    void setStrokeWidth(float width) {
        SLD.stroke(symbolizer).setWidth(Style.filterFactory.literal(width))
    }

    /**
     * Set the stroke opacity
     * @param opacity The stroke opacity
     */
    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Set the line cap (round)
     * @param lineCap The line cap
     */
    void setStrokeLineCap(String lineCap) {
        SLD.stroke(symbolizer).setLineCap(Style.filterFactory.literal(lineCap))
    }

    /**
     * Set the Stroke Dash Array with a String "5 2"
     * @param The stroke dash array as a String "5 2"
     */
    void setStrokeDashArray(String dashArray) {
        float[] floatArray = dashArray.split(" ").collect{Float.parseFloat(it)}.toArray() as float[]
        SLD.stroke(symbolizer).setDashArray(floatArray)
    }

    /**
     * Create a Graphic for the LineSymbolizer/Stroke/GraphicStroke property
     * but only if necessary.
     */
    private void createGraphicStrokeIfNecessary() {
        if (symbolizer.stroke.graphicStroke == null) {
            symbolizer.stroke.graphicStroke = Style.builder.createGraphic()
        }
    }

    /**
     * Set the GraphicStroke/Mark well known name
     * @param The well known name
     */
    void setGraphicStrokeMarkName(String name) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).wellKnownName = Style.filterFactory.literal(name)
    }

    /**
     * Set the GraphicStroke/Mark/Stroke color
     * @param color The Color
     */
    void setGraphicStrokeMarkStrokeColor(String color) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).stroke.color = Style.filterFactory.literal(color)
    }

    /**
     * Set the GraphicStroke/Mark/Stroke width
     * @param width The width
     */
    void setGraphicStrokeMarkStrokeWidth(float width) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).stroke.width = Style.filterFactory.literal(width)
    }

    /**
     * Set the GraphicStroke/Mark size
     * @param size The size
     */
    void setGraphicStrokeMarkSize(float size) {
        createGraphicStrokeIfNecessary()
        symbolizer.stroke.graphicStroke.size = Style.filterFactory.literal(size)
    }
}


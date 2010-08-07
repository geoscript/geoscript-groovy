package geoscript.style

import org.geotools.styling.SLD

/**
 * A LineSymbolizer.
 * <p>You can create a LineSymbolizer using a map like syntax:</p>
 * <p><code><pre>
 * def sym = new LineSymbolizer(
 *      strokeColor: "#000000",
 *      strokeWidth: 3
 * )
 * </pre></code></p>
 * <p>Or you can create a LineSymbolizer using properties:</p>
 * <p><code><pre>
 * def sym = new LineSymbolizer()
 * sym.strokeColor = "#000000"
 * sym.strokeWidth = 3
 * </pre></code></p>
 * @author Jared Erickson
 */
class LineSymbolizer  extends Symbolizer {

    /**
     * Create a new LineSymbolizer from a GeoTools LineSymbolizer
     * @param symbolizer The GeoTools LineSymbolizer
     */
    LineSymbolizer(org.geotools.styling.LineSymbolizer symbolizer) {
        super(symbolizer)
    }

    /**
     * Create a new LineSymbolizer
     */
    LineSymbolizer() {
        super(Style.builder.createLineSymbolizer())
    }

    /**
     * Set the stroke color (#FF0000)
     * @param color The color (#FF0000)
     */
    void setStrokeColor(String color) {
        SLD.stroke(symbolizer).setColor(Style.filterFactory.literal(Style.getHexColor(color)))
    }

    /**
     * Get the stroke color
     * @return The stroke color
     */
    String getStrokeColor() {
        SLD.stroke(symbolizer)?.color
    }

    /**
     * Set the stroke width (1)
     * @param width The stroke width (1)
     */
    void setStrokeWidth(float width) {
        SLD.stroke(symbolizer).setWidth(Style.filterFactory.literal(width))
    }

    /**
     * Get the stroke width
     * @return The stroke width
     */
    float getStrokeWidth() {
        SLD.stroke(symbolizer)?.width?.value
    }

    /**
     * Set the stroke opacity  (0 = transparent to 1 = opaque)
     * @param opacity The stroke opacity (0 = transparent to 1 = opaque)
     */
    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Get the stroke opacity
     * @return The stroke opacity
     */
    float getStrokeOpacity() {
        SLD.stroke(symbolizer)?.opacity?.value
    }

    /**
     * Set the line cap (round, butt, square)
     * @param lineCap The line cap (round, butt, square)
     */
    void setStrokeLineCap(String lineCap) {
        SLD.stroke(symbolizer).setLineCap(Style.filterFactory.literal(lineCap))
    }

    /**
     * Get the stroke line cap
     * @return The stroke line cap
     */
    String getStrokeLineCap() {
        SLD.stroke(symbolizer)?.lineCap
    }

    /**
     * Set the line join (miter)
     * @param lineJoin The line join (miter)
     */
    void setStrokeLineJoin(String lineJoin) {
        SLD.stroke(symbolizer).setLineJoin(Style.filterFactory.literal(lineJoin))
    }

    /**
     * Get the stroke line join
     * @return The stroke line join
     */
    String getStrokeLineJoin() {
        SLD.stroke(symbolizer)?.lineJoin
    }

    /**
     * Set the stroke dash offset
     * @param offset stroke dash offset
     */
    void setStrokeDashOffset(float offset) {
        SLD.stroke(symbolizer).setDashOffset(Style.filterFactory.literal(offset))
    }

    /**
     * Get the stroke dash offset
     * @return The stroke dash offset
     */
    float getStrokeDashOffset() {
        SLD.stroke(symbolizer)?.dashOffset?.value as float
    }

    /**
     * Set the Stroke Dash Array with a String ("5 2")
     * @param The stroke dash array as a String ("5 2")
     */
    void setStrokeDashArray(String dashArray) {
        float[] floatArray = dashArray.split(" ").collect{Float.parseFloat(it)}.toArray() as float[]
        SLD.stroke(symbolizer).setDashArray(floatArray)
    }

    /**
     * Get the stroke dash array
     * @return The stroke dash array
     */
    String getStrokeDashArray() {
        if (SLD.stroke(symbolizer)?.dashArray) {
            SLD.stroke(symbolizer)?.dashArray?.collect{it}.join(" ")
        }
        else {
            return null
        }
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
     * <p>Verticle Line: shape://vertline |-|-|</p>
     * <p>Slash: shape://slash /-/-/</p>
     * <p>Back Slash: shape://backslash \-\-\</p>
     * <p>Times: shape://times X-X-X</p>
     * @param The well known name
     */
    void setGraphicStrokeMarkName(String name) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).wellKnownName = Style.filterFactory.literal(name)
    }

    /**
     * Get the graphic stroke mark name
     * @return The graphic stroke mark name
     */
    String getGraphicStrokeMarkName() {
        if (symbolizer?.stroke?.graphicStroke) {
            return SLD.mark(symbolizer.stroke.graphicStroke).wellKnownName
        }
        else {
            return null
        }
    }

    /**
     * Set the GraphicStroke/Mark/Stroke color (#FF0000)
     * @param color The Color
     */
    void setGraphicStrokeMarkStrokeColor(String color) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).stroke.color = Style.filterFactory.literal(Style.getHexColor(color))
    }

    /**
     * Get the graphic stroke mark stroke color
     * @return The graphic stroke mark stroke color
     */
    String getGraphicStrokeMarkStrokeColor() {
        if (symbolizer?.stroke?.graphicStroke) {
            return SLD.mark(symbolizer.stroke.graphicStroke).stroke?.color
        }
        else {
            return null
        }
    }

    /**
     * Set the GraphicStroke/Mark/Fill color (#FF0000)
     * @param color The Color
     */
    void setGraphicStrokeMarkFillColor(String color) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).fill.color = Style.filterFactory.literal(Style.getHexColor(color))
    }

    /**
     * Get the graphic stroke mark fill color
     * @return The graphic stroke mark fill color
     */
    String getGraphicStrokeMarkFillColor() {
        if (symbolizer?.stroke?.graphicStroke) {
            return SLD.mark(symbolizer.stroke.graphicStroke).fill?.color
        }
        else {
            return null
        }
    }

    /**
     * Set the GraphicStroke/Mark/Stroke width (2)
     * @param width The width
     */
    void setGraphicStrokeMarkStrokeWidth(float width) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).stroke.width = Style.filterFactory.literal(width)
    }

    /**
     * Get the graphic stroke mark stroke width
     * @return The graphic stroke mark stroke width
     */
    float getGraphicStrokeMarkStrokeWidth() {
        if (symbolizer?.stroke?.graphicStroke) {
            return SLD.mark(symbolizer.stroke.graphicStroke).stroke?.width.value as float
        }
        else {
            return 0
        }
    }

    /**
     * Set the GraphicStroke/Mark size (6)
     * @param size The size
     */
    void setGraphicStrokeMarkSize(float size) {
        createGraphicStrokeIfNecessary()
        symbolizer.stroke.graphicStroke.size = Style.filterFactory.literal(size)
    }

    /**
     * Get the graphic stoke mark size
     * @return The graphic stoke mark size
     */
    float getGraphicStrokeMarkSize() {
        if (symbolizer?.stroke?.graphicStroke) {
            return symbolizer?.stroke?.graphicStroke?.size?.value as float
        }
        else {
            return 0
        }
    }
}


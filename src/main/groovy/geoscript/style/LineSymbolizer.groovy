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
        SLD.stroke(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Set the stroke width (1)
     * @param width The stroke width (1)
     */
    void setStrokeWidth(float width) {
        SLD.stroke(symbolizer).setWidth(Style.filterFactory.literal(width))
    }

    /**
     * Set the stroke opacity  (0 = transparent to 1 = opaque)
     * @param opacity The stroke opacity (0 = transparent to 1 = opaque)
     */
    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Set the line cap (round, butt, square)
     * @param lineCap The line cap (round, butt, square)
     */
    void setStrokeLineCap(String lineCap) {
        SLD.stroke(symbolizer).setLineCap(Style.filterFactory.literal(lineCap))
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
     * Set the GraphicStroke/Mark/Stroke color (#FF0000)
     * @param color The Color
     */
    void setGraphicStrokeMarkStrokeColor(String color) {
        createGraphicStrokeIfNecessary()
        SLD.mark(symbolizer.stroke.graphicStroke).stroke.color = Style.filterFactory.literal(color)
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
     * Set the GraphicStroke/Mark size (6)
     * @param size The size
     */
    void setGraphicStrokeMarkSize(float size) {
        createGraphicStrokeIfNecessary()
        symbolizer.stroke.graphicStroke.size = Style.filterFactory.literal(size)
    }
}


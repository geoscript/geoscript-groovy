package geoscript.style

import org.geotools.styling.SLD

/**
 * The PolygonSymbolizer
 * @author Jared Erickson
 */
class PolygonSymbolizer  extends Symbolizer {

    /**
     * Create a new PolygonSymbolizer
     */
    PolygonSymbolizer() {
        super(Style.builder.createPolygonSymbolizer())
    }

    /**
     * Set the stroke color (#FFFFFF)
     * @param color The stroke color (#FFFFFF)
     */
    void setStrokeColor(String color) {
        SLD.stroke(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Set the stroke width (2)
     * @param width The stroke width (2)
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
     * Set the fill color (#000080)
     * @param color The fill color (#000080)
     */
    void setFillColor(String color) {
        SLD.fill(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Set the fill opacity (0.5)
     * @param opacity The fill opacity (0.5)
     */
    void setFillOpacity(float opacity) {
        SLD.fill(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Set the external graphic image
     * @param uri The URI of the external graphic image
     * @param format The format of the image (image/png)
     */
    void setGraphic(String uri) {
        setGraphic(uri, "image/${uri.substring(uri.lastIndexOf('.') + 1)}")
    }

    /**
     * Set the external graphic image
     * @param uri The URI of the external graphic image
     * @param format The format of the image (image/png)
     */
    void setGraphic(String uri, String format) {
        createGraphicFillIfNecessary()
        File file = new File(uri)
        symbolizer.fill.graphicFill.graphicalSymbols().add(Style.builder.createExternalGraphic(file.toURL(),format))
    }

    /**
     * Create a Graphic for the PolygonSymbolizer/Fill/GraphicFill property
     * but only if necessary.
     */
    private void createGraphicFillIfNecessary() {
        if (symbolizer.fill.graphicFill == null) {
            symbolizer.fill.graphicFill = Style.builder.createGraphic()
        }
    }

    /**
     * Set the graphic fill's mark well known name
     * <p>Verticle Line: shape://vertline</p>
     * <p>Horizontal Line: shape://horline</p>
     * <p>Slash: shape://slash</p>
     * <p>Back Slash: shape://backslash</p>
     * <p>Plus: shape://plus</p>
     * <p>Times: shape://times</p>
     * @param The well known name
     */
    void setMarkName(String name) {
        createGraphicFillIfNecessary()
        SLD.mark(symbolizer.fill.graphicFill).wellKnownName = Style.filterFactory.literal(name)
    }

    /**
     * Set the graphic fill's mark's stroke color
     * @param color The Color
     */
    void setMarkStrokeColor(String color) {
        createGraphicFillIfNecessary()
        SLD.mark(symbolizer.fill.graphicFill).stroke.color = Style.filterFactory.literal(color)
    }

    /**
     * Set the graphic fill's mark's stroke width
     * @param width The width
     */
    void setMarkStrokeWidth(float width) {
        createGraphicFillIfNecessary()
        SLD.mark(symbolizer.fill.graphicFill).stroke.width = Style.filterFactory.literal(width)
    }

}


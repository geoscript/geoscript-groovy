package geoscript.style

import org.geotools.styling.SLD

/**
 * The PolygonSymbolizer.
 * <p>You can create a PolygonSymbolizer using a map like syntax:</p>
 * <p><code><pre>
 * def sym = new PolygonSymbolizer(
 *      fillColor: "#000080",
 *      fillOpacity: 0.5,
 *      strokeColor: "#FFFFFF",
 *      strokeWidth: 2
 * )
 * </pre></code></p>
 * <p>Or you can create a PolygonSymbolizer using properties:</p>
 * <p><code><pre>
 * def sym = new PolygonSymbolizer()
 * sym.fillColor = "#000080"
 * sym.fillOpacity = 0.5
 * sym.strokeColor = "#FFFFFF"
 * sym.strokeWidth = 2
 * </pre></code></p>
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
     * Get the stroke color
     * @return The stroke color
     */
    String getStrokeColor() {
        SLD.stroke(symbolizer)?.color
    }

    /**
     * Set the stroke width (2)
     * @param width The stroke width (2)
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
     * Set the stroke opacity (0=transparent to 1=opaque)
     * @param opacity The stroke opacity (0=transparent to 1=opaque)
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
     * Set the fill color (#000080)
     * @param color The fill color (#000080)
     */
    void setFillColor(String color) {
        SLD.fill(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Get the fill color
     * @return The fill color
     */
    String getFillColor() {
        SLD.fill(symbolizer)?.color
    }

    /**
     * Set the fill opacity (0=transparent to 1=opaque)
     * @param opacity The fill opacity (0=transparent to 1=opaque)
     */
    void setFillOpacity(float opacity) {
        SLD.fill(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Get the fill opacity
     * @return The fill opacity
     */
    float getFillOpacity() {
        SLD.fill(symbolizer)?.opacity?.value
    }

    /**
     * Set the external graphic image (images/icon.png)
     * @param uri The URI of the external graphic image (images/icon.png)
     */
    void setGraphic(String uri) {
        setGraphic(uri, "image/${uri.substring(uri.lastIndexOf('.') + 1)}")
    }

    /**
     * Set the external graphic image (images/icon.png and image/png)
     * @param uri The URI of the external graphic image (images/icon.png)
     * @param format The format of the image (image/png)
     */
    void setGraphic(String uri, String format) {
        createGraphicFillIfNecessary()
        File file = new File(uri)
        symbolizer.fill.graphicFill.graphicalSymbols().add(Style.builder.createExternalGraphic(file.toURL(),format))
    }

    /**
     * Get the graphic
     * @return The graphic
     */
    String getGraphic() {
        def file = symbolizer?.fill?.graphicFill?.graphicalSymbols().find{sym->
            sym instanceof org.geotools.styling.ExternalGraphic
        }?.location?.file
        if (file) {
            return new File(file).name
        }
        return null
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
     * Set the graphic fill's mark well known name (shape://vertline)
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
     * Get the mark name
     * @return The mark name
     */
    String getMarkName() {
        if (symbolizer?.fill?.graphicFill) {
            SLD.mark(symbolizer.fill.graphicFill)?.wellKnownName
        }
        else {
            return null
        }
    }

    /**
     * Set the graphic fill's mark's stroke color (#FF0000)
     * @param color The Color (#FF0000)
     */
    void setMarkStrokeColor(String color) {
        createGraphicFillIfNecessary()
        SLD.mark(symbolizer.fill.graphicFill).stroke.color = Style.filterFactory.literal(color)
    }

    /**
     * Get the mark stroke color
     * @return The mark stroke color
     */
    String getMarkStrokeColor() {
        if (symbolizer?.fill?.graphicFill) {
            SLD.mark(symbolizer.fill.graphicFill)?.stroke?.color
        }
        else {
            return null
        }
    }

    /**
     * Set the graphic fill's mark's stroke width (1.5)
     * @param width The width (1.5)
     */
    void setMarkStrokeWidth(float width) {
        createGraphicFillIfNecessary()
        SLD.mark(symbolizer.fill.graphicFill).stroke.width = Style.filterFactory.literal(width)
    }

    /**
     * Gethe mark stroke width
     * @return The mark stroke width
     */
    float getMarkStrokeWidth() {
        if (symbolizer?.fill?.graphicFill) {
            SLD.mark(symbolizer.fill.graphicFill)?.stroke?.width.value
        }
        else {
            return null
        }
    }

}


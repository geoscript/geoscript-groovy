package geoscript.style

import org.geotools.styling.SLD

/**
 * A PointSymbolizer.
 * <p>You can create a PointSymbolizer using a map like syntax:</p>
 * <p><code><pre>
 * def sym = new PointSymbolizer(
 *      shape: "circle",
 *      fillColor: "#FF0000",
 *      size: 6,
 *      strokeOpacity: 0
 * )
 * </pre></code></p>
 * <p>Or you can create a PointSymbolizer using properties:</p>
 * <p><code><pre>
 * def sym = new PointSymbolizer()
 * sym.shape = "circle"
 * sym.fillColor = "#FF0000"
 * sym.size = 6
 * sym.strokeOpacity = 0
 * </pre></code></p>
 * @author Jared Erickson
 */
class PointSymbolizer extends Symbolizer {

    /**
     * Create a new PointSymbolizer
     */
    PointSymbolizer() {
        super(Style.builder.createPointSymbolizer())
    }

    /**
     * Set the well known shape name (circle, square, triangle, star, cross, x, arrow)
     * @param The well known shape name (circle, square, triangle, star, cross, x, arrow)
     */
    void setShape(String shape) {
        SLD.mark(symbolizer).setWellKnownName(Style.filterFactory.literal(shape))
    }

    /**
     * Get the shape
     * @return The shape
     */
    String getShape() {
        SLD.mark(symbolizer)?.wellKnownName
    }

    /**
     * Set the size (12)
     * @param size The size (12)
     */
    void setSize(float size) {
        SLD.graphic(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    /**
     * Get the size
     * @return The size
     */
    float getSize() {
        SLD.graphic(symbolizer)?.size?.value
    }

    /**
     * Set the rotation (45)
     * @param rotation The rotation (45)
     */
    void setRotation(float rotation) {
        SLD.graphic(symbolizer).setRotation(Style.filterFactory.literal(rotation))
    }

    /**
     * Get the rotation
     * @return The rotation
     */
    float getRotation() {
        SLD.graphic(symbolizer)?.rotation?.value
    }

    /**
     * Set the stroke color (#009900)
     * @param color The stroke color (#009900)
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
     * Set the stroke opacity (0 = transparent to 1 = opaque)
     * @param opacity The stroke opacity (0 = transparent to 1 = opaque)
     */
    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * The stroke opacity
     * @return The stroke opacity
     */
    float getStrokeOpacity() {
        SLD.stroke(symbolizer)?.opacity?.value
    }

    /**
     * Set the fill color (#ff0000)
     * @param color The fill color (#ff0000)
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
     * Set the fill opacity (0 = transparent to 1 = opaque)
     * @param opacity The fill opacity (0 = transparent to 1 = opaque)
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
     * Set the external graphic image (images/icon.png, image/png)
     * @param uri The URI of the external graphic image (images/icon.png)
     * @param format The format of the image (image/png)
     */
    void setGraphic(String uri, String format) {
        File file = new File(uri)
        symbolizer.getGraphic().graphicalSymbols().add(Style.builder.createExternalGraphic(file.toURL(),format))
    }

    /**
     * Get the graphic
     * @return The graphic
     */
    String getGraphic() {
        def file = symbolizer?.graphic?.graphicalSymbols().find{sym->
            sym instanceof org.geotools.styling.ExternalGraphic
        }?.location?.file
        if (file) {
            return new File(file).name
        }
        return null
    }

}
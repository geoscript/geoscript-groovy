package geoscript.style

import org.geotools.styling.SLD

/**
 * The PointSymbolizer
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
     * Set the well known shape name (circle, square, triangle)
     * @param The well known shape name (circle, square, triangle)
     */
    void setShape(String shape) {
        SLD.mark(symbolizer).setWellKnownName(Style.filterFactory.literal(shape))
    }

    /**
     * Set the size (12)
     * @param size The size (12)
     */
    void setSize(float size) {
        SLD.graphic(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    /**
     * Set the rotation (45)
     * @param rotation The rotation (45)
     */
    void setRotation(float rotation) {
        SLD.graphic(symbolizer).setRotation(Style.filterFactory.literal(rotation))
    }

    /**
     * Set the stroke color (#009900)
     * @param color The stroke color (#009900)
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
     * Set the stroke opacity (0.2)
     * @param opacity The stroke opacity (0.2)
     */
    void setStrokeOpacity(float opacity) {
        SLD.stroke(symbolizer).setOpacity(Style.filterFactory.literal(opacity))
    }

    /**
     * Set the fill color (#ff0000)
     * @param color The fill color (#ff0000)
     */
    void setFillColor(String color) {
        SLD.fill(symbolizer).setColor(Style.filterFactory.literal(color))
    }

    /**
     * Set the fill opacity (0.75)
     * @param opacity The fill opacity (0.75)
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
        File file = new File(uri)
        symbolizer.getGraphic().graphicalSymbols().add(Style.builder.createExternalGraphic(file.toURL(),format))
    }

}


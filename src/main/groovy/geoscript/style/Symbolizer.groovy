package geoscript.style

import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer
 * @author Jared Erickson
 */
class Symbolizer {

    /**
     * The wrapped GeoTools Symbolizer
     */
    GtSymbolizer symbolizer

    /**
     * The drawing order
     */
    double zIndex = 0;

    /**
     * Create a new Symbolizer wrapping a GeoTools Symbolizer
     */
    Symbolizer(GtSymbolizer gtSymbolizer) {
        this.symbolizer = gtSymbolizer
    }

    /**
     * Set the geometry. Some Layers may have more than one
     * geometry Field.  You can set the name of the field here.  Or you
     * can pass in a geoscript.filter.Function.
     * @param geom The name of the geometry Field or a geoscript.filter.Function
     */
    void setGeometry(def geom) {
        if (geom instanceof geoscript.filter.Function) {
            symbolizer.geometry = geom.function
        } else {
            symbolizer.geometry = Style.filterFactory.property(geom)
        }
    }

    /**
     * Get the geometry value
     * @return The geometry value
     */
    def getGeometry() {
        def geom = symbolizer.geometry
        if (geom == null) {
            return null
        }
        // Try to make it GeoScript friendly
        if (geom instanceof org.opengis.filter.expression.PropertyName) {
            return geom.propertyName
        } else if (geom instanceof org.opengis.filter.expression.Literal) {
            return geom.value
        } else if (geom instanceof org.opengis.filter.expression.Function) {
            return new geoscript.filter.Function(geom)
        } else {
            return geom
        }
    }

    /**
     * The string representation
     * @param The string representation
     */
    String toString() {
        symbolizer.toString()
    }

    /**
     * Get a default Symbolizer for the given geometry type
     * @param geometryType The geometry type
     * @return A Symbolizer
     */
    static Symbolizer getDefaultForGeometryType(String geometryType, def color) {
        java.awt.Color c = (color instanceof String) ? Style.getColor(color as String) : color as java.awt.Color
        def sym;
        if (geometryType.toLowerCase().endsWith("point")) {
            sym = new PointSymbolizer(
                fillColor: Style.convertColorToHex(c),
                strokeColor: Style.convertColorToHex(c.darker())
            )
        }
        else if (geometryType.toLowerCase().endsWith("linestring")
            || geometryType.toLowerCase().endsWith("linearring")
            || geometryType.toLowerCase().endsWith("curve")) {
            sym = new LineSymbolizer(
                strokeColor: Style.convertColorToHex(c)
            )
        }
        else /*if (geometryType.toLowerCase().endsWith("polygon"))*/ {
            sym = new PolygonSymbolizer(
                fillColor: Style.convertColorToHex(c),
                strokeColor: Style.convertColorToHex(c.darker())
            )
        }
        return sym
    }

}


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


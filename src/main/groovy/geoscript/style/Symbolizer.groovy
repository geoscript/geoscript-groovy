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

}


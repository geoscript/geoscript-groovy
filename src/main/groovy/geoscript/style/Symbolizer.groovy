package geoscript.style

import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 *
 * @author Jared Erickson
 */
class Symbolizer {

    GtSymbolizer symbolizer

    Symbolizer(GtSymbolizer gtSymbolizer) {
        this.symbolizer = gtSymbolizer
    }

    String toString() {
        symbolizer.toString()
    }

}


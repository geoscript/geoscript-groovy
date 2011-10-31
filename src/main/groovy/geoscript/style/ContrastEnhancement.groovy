package geoscript.style

import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.ContrastEnhancement as GtContrastEnhancement

import geoscript.filter.Expression
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Rule
import org.opengis.style.ContrastMethod

/**
 * A ConstrastEnhancement Symbolizer
 * @author Jared Erickson
 */
class ContrastEnhancement extends Raster {

    /**
     * The method: Normalize or Histogram
     */
    String method

    /**
     * The gamma value
     */
    Expression gammaValue

    /**
     * Create a new ContrastEnhancement
     * @param method The method
     * @param gammaValue The gamma value
     */
    ContrastEnhancement(String method, def gammaValue = null) {
        this.method = method
        if (gammaValue) this.gammaValue = new Expression(gammaValue)
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, RasterSymbolizer).each {s ->
            apply(s)
        }
    }

    /**
     * Apply this Symbolizer to the GeoTools Symbolizer
     * @param sym The GeoTools Symbolizer
     */
    @Override
    protected void apply(GtSymbolizer sym) {
        super.apply(sym)
        sym.contrastEnhancement = createContrastEnhancement()
    }

    /**
     * Create a GeoTools ContrastEnhancement
     * @return A GeoTools ContrastEnhancement
     */
    protected GtContrastEnhancement createContrastEnhancement() {
        def contrastEnhancement = styleFactory.createContrastEnhancement()
        contrastEnhancement.method = ContrastMethod.valueOf(this.method.toUpperCase())
        if (this.gammaValue) contrastEnhancement.gammaValue = this.gammaValue.expr
        contrastEnhancement
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        def values = ['method': method]
        if (gammaValue) values['gammaValue'] = gammaValue
        buildString("ContrastEnhancement", values)
    }
}

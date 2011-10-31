package geoscript.style

import geoscript.filter.Expression
import org.geotools.styling.Rule
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * Symbolize a Raster with a ShadedRelief
 * @author Jared Erickson
 */
class ShadedRelief extends Raster {

    /**
     * Whether to turn on the brightness only flag
     */
    boolean brightnessOnly = false

    /**
     * The relief factor
     */
    Expression reliefFactor = new Expression(55)

    /**
     * Create a new ShadedRelief Symbolizer
     * @param reliefFactor The relief factor
     * @param brightnessOnly Whether to turn on the brightness only flag
     */
    ShadedRelief(def reliefFactor = 55, boolean brightnessOnly = false) {
        this.reliefFactor = new Expression(reliefFactor)
        this.brightnessOnly = brightnessOnly
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, RasterSymbolizer.class).each{s ->
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
        def shaded = styleFactory.createShadedRelief(reliefFactor.expr)
        shaded.brightnessOnly = brightnessOnly
        sym.shadedRelief = shaded
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("ShadedRelief", ['reliefFactor': reliefFactor, 'brightnessOnly': brightnessOnly])
    }
}
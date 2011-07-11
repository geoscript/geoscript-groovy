package geoscript.style

import geoscript.filter.Function
import org.geotools.styling.Rule
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.TextSymbolizer

/**
 * A Symbolizer that wraps a geoscript.filter.Function generally
 * used for transforming Geometry or String or Date formatting.
 * <p>You can create a Transform from a CQL statement:</p>
 * <p><code>Transform transform = new Transform("centroid(the_geom)")</code></p>
 * <p>Or from a geoscript.filter.Function:</p>
 * <p><code>Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))</code></p>
 * @author Jared Erickson
 */
class Transform extends Symbolizer {

    /**
     * The geoscript.filter.Function
     */
    private Function function

    /**
     * Create a new Transform from a Function.
     * <p><code>Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))</code></p>
     * @param function The geoscript.filter.Function
     */
    Transform(Function function) {
        super()
        this.function = function
    }
    
    /**
     * Create a new Transform from a CQL filter function.
     * <p><code>Transform transform = new Transform("centroid(the_geom)")</code></p>
     * @param cql A CQL string 
     */
    Transform(String cql) {
        this(new Function(cql))
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        rule.symbolizers().each{s ->
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
        if (sym instanceof TextSymbolizer) {
            sym.label = function.function
        } else {
            sym.geometry = function.function
        }
    }

    /**
     * Get the Function
     * @return The Function
     */
    Function getFunction() {
        this.function
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Transform", ['function': function])
    }
}


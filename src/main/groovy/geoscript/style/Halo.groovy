package geoscript.style

import geoscript.filter.Expression
import org.geotools.styling.Rule
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer for label background.
 * You can create a Halo from a Fill and radius:
 * <p><code>def halo = new Halo(new Fill("navy"), 2.5)</code></p>
 * Or with named parameters:
 * <p><code>def halo = new Halo(fill: new Fill("navy"), radius: 2.5)</code></p>
 * @author Jared Erickson
 */
class Halo extends Symbolizer {

    /**
     * The Fill
     */
    Fill fill = new Fill("#ffffff")

    /**
     * The radius
     */
    Expression radius = new Expression(1.0)

    /**
     * Create a new Halo with named parameters.
     * <p><code>def halo = new Halo(fill: new Fill("navy"), radius: 2.5)</code></p>
     * @param map A Map of named parameters.
     */
    Halo(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a new Halo with a Fill and radius.
     * <p><code>def halo = new Halo(new Fill("navy"), 2.5)</code></p>
     * @param fill The Fill
     * @param radius The radius
     */
    Halo(Fill fill, def radius) {
        super()
        this.fill = fill
        this.radius = new Expression(radius)
    }

    /**
     * Set the radius
     * @param radius The radius
     */
    void setRadius(def radius) {
        this.radius = new Expression(radius)
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, TextSymbolizer).each{s ->
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
        sym.halo = styleFactory.createHalo(
            fill.createFill(),
            radius.expr
        )
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Halo", ['fill': fill, 'radius': radius])
    }
}


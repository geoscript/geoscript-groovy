package geoscript.style

import geoscript.filter.Expression
import org.geotools.styling.Rule
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Raster Symbolizer
 * @author Jared Erickson
 */
class Raster extends Symbolizer {

    /**
     * The overall opacity of the Raster Symbolizer
     */
    Expression opacity = new Expression(1.0)

    /**
     * How overalapping rasters are handled (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM)
     */
    Expression overlap = new Expression("RANDOM")

    /**
     * The geometry name
     */
    Expression geometry = new Expression("grid")

    /**
     * Create a new Raster Symbolizer
     * @param opacity The opacity
     * @param overlap The overlap value
     * @param geometry The geometry name
     */
    Raster(def opacity = 1.0, def overlap = null, def geometry = null) {
        this.opacity = new Expression(opacity)
        if (overlap) this.overlap = new Expression(overlap)
        if (geometry) this.geometry = new Expression(geometry)
    }

    /**
     * Create a new Raster Symbolizer from named parameters.
     * @param map The map of named parameters
     */
    Raster(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k as String)){
                this."$k" = v
            }
        }
    }

    /**
     * Set the opacity (0-1)
     * @param opacity The opacity (0-1)
     */
    void setOpacity(def opacity) {
        this.opacity = new Expression(opacity)
    }

    /**
     * Set the overlap value (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM)
     * @param overlap The overlap value (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM)
     */
    void setOverlap(def overlap) {
        if (overlap) {
            this.overlap = new Expression(overlap)
        }
    }

    /**
     * Set the geometry name
     * @param geometry The geometry name
     */
    void setGeometry(def geometry) {
        if (geometry) {
            this.geometry = new Expression(geometry)
        }
    }

    /**
     * Set the opacity (0-1)
     * @param opacity The opacity (0-1)
     * @return This Raster Symbolizer
     */
    Raster opacity(def opacity) {
        setOpacity(opacity)
        this
    }

    /**
     * Set the overlap value (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM)
     * @param overlap The overlap value (LATEST_ON_TOP, EARLIEST_ON_TOP, AVERAGE, RANDOM)
     * @return This Raster Symbolizer
     */
    Raster overlap(def overlap) {
        setOverlap(overlap)
        this
    }

    /**
     * Set the geometry name
     * @param geometry The geometry name
     * @return This Raster Symbolizer
     */
    Raster geometry(def geometry) {
        setGeometry(geometry)
        this
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
        sym.opacity = opacity.expr
        if (overlap) sym.overlap = overlap.expr
        if (geometry) sym.geometry = geometry.expr
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Raster", ['opacity': opacity, 'overlap': overlap, 'geometry': geometry])
    }
}

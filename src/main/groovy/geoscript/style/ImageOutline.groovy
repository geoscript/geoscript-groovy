package geoscript.style

import org.geotools.styling.Rule
import org.geotools.styling.RasterSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * An ImageOutline Symbolizer applies a Stroke or Fill Symbolizer
 * to a Raster.
 * @author Jared Erickson
 */
class ImageOutline extends Symbolizer {

    /**
     * The Stroke
     */
    private Stroke stroke

    /**
     * The Fill
     */
    private Fill fill

    /**
     * Create an ImageOutline for a Raster Symbolizer with a Stroke
     * @param stroke The Stroke
     */
    ImageOutline(Stroke stroke) {
        this.stroke = stroke
    }

    /**
     * Create an ImageOutline for a Raster Symbolizer with a Fill
     * @param fill The Fill
     */
    ImageOutline(Fill fill) {
        this.fill = fill
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
        sym.imageOutline = styleFactory.createPolygonSymbolizer()
        if (stroke) {
            sym.imageOutline.stroke = stroke.createStroke()
        } else {
            sym.imageOutline.fill = fill.createFill()
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        def values = [:]
        if (stroke) {
            values['stroke'] = stroke.toString()
        } else {
            values['fill'] = fill.toString()
        }
        buildString("ImageOutline", values)
    }
}
package geoscript.style

import geoscript.filter.Color
import geoscript.filter.Expression
import geoscript.layer.Raster
import org.geotools.api.style.Rule
import org.geotools.api.style.RasterSymbolizer
import org.geotools.api.style.Symbolizer as GtSymbolizer

/**
 * A ColorMap is a Symbolizer used to style Rasters by mapping pixel values to colors.
 * <p>You can create a ColorMap from a List of Maps with color and quantity keys (opacity and label keys are optional):</p>
 * <p><blockquote><pre>
 * def colorMap = new ColorMap([[color: "#008000", quantity:70], [color:"#663333", quantity:256, opacity: 0, label: "NO DATA"]])
 * </pre></blockquote></p>
 * <p>Or you can create a ColorMap for a Raster from a ColorBrewer color palette:</p>
 * <p><blockquote><pre>
 * def colorMap = new ColorMap(raster,"Greens", 5)
 * </pre></blockquote></p>
 * <p>Or you can create a ColorMap for min and max values from a List of Colors:</p>
 * <p><blockquote><pre>
 * def colorMap = new ColorMap(min, max, Color.getPaletteColors("Greens", 5))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class ColorMap extends geoscript.style.RasterSymbolizer {

    /**
     * The ColorMap type (intervals, values, ramp)
     */
    String type = "ramp"

    /**
     * A List of ColorMap values
     */
    List<Map> values

    /**
     * Whether to use extended colors or not
     */
    boolean extended = false

    /**
     * Create a new ColorMap
     * @param values A List of ColorMap values
     * @param type The type
     * @param extended Whether to use extended colors
     */
    ColorMap(List values, String type = "ramp", boolean extended = false) {
        super()
        this.values = values
        this.type = type
        this.extended = extended
    }

    /**
     * Create a new ColorMap.
     * @param map A Map of named parameters.
     */
    ColorMap(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k as String)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a ColorMap for a Raster with a palette of colors (from Color Brewer) with the given
     * number of categories
     * @param raster The Raster
     * @param palette The color palette name (from Color Brewer)
     * @param numberOfCategories The number of categories
     * @param band The band to use for min and max values
     * @param type The type of interpolation
     * @param extended Whether to use extended colors
     */
    ColorMap (Raster raster, String palette, int numberOfCategories, int band = 0, String type = "ramp", boolean extended = false) {
        this(raster.extrema.min[band], raster.extrema.max[band], palette, numberOfCategories, type, extended)
    }

    /**
     * Create a ColorMap for a min and max values with a palette of colors (from Color Brewer) with the given
     * @param min The min value
     * @param max The max value
     * @param palette The color palette name (from Color Brewer)
     * @param numberOfCategories The number of categories
     * @param type The type of interpolation
     * @param extended Whether to use extended colors
     */
    ColorMap (double min, double max, String palette, int numberOfCategories, String type = "ramp", boolean extended = false) {
        this(min, max, Color.getPaletteColors(palette, numberOfCategories), type, extended)
    }

    /**
     * Create a ColorMap for a Raster from a list of colors
     * @param raster The Raster
     * @param colors The list of colors
     * @param band The band The band
     * @param type The type of interpolation
     * @param extended Whether to use extended colors
     */
    ColorMap(Raster raster, List colors, int band = 0, String type = "ramp", boolean extended = false) {
        this(raster.extrema.min[band], raster.extrema.max[band], colors, type, extended)
    }

    /**
     * Create a ColorMap for min and max values with a List of colors
     * @param min The min value
     * @param max The max value
     * @param colors The list of colors
     * @param type The type of interpolation
     * @param extended Whether to use extended colors
     */
    ColorMap(double min, double max, List colors, String type = "ramp", boolean extended = false) {
        double spread = (max - min) / (colors.size() -1)
        double quantity = min
        this.values = (0..<colors.size()).collect{i ->
            def value = [quantity: quantity, color: colors.get(i)]
            quantity += spread
            value
        }
        this.type = type
        this.extended = extended
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, RasterSymbolizer).each{s ->
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
        sym.colorMap = generateColorMap(values, type, extended)
    }

    /**
     * Generate a GeoTool's ColorMap from A List of Maps with color, opacity, quantity, and label keys
     * @param values A List of Maps
     * @return A GeoTool's ColorMap
     */
    protected org.geotools.api.style.ColorMap generateColorMap(List<Map> values, String type, boolean extended) {
        def colorMap = styleFactory.createColorMap();
        colorMap.extendedColors = extended
        if (type.equalsIgnoreCase("ramp")) {
            colorMap.type = org.geotools.api.style.ColorMap.TYPE_RAMP
        } else if (type.equalsIgnoreCase("intervals")) {
            colorMap.type = org.geotools.api.style.ColorMap.TYPE_INTERVALS
        } else if (type.equalsIgnoreCase("values")) {
            colorMap.type = org.geotools.api.style.ColorMap.TYPE_VALUES
        }
        values.each {value ->
            def entry = styleFactory.createColorMapEntry()
            if (value.containsKey("color")) entry.color = new Expression(value.color).expr
            entry.opacity = new Expression(value.containsKey("opacity") ? value.opacity : 1.0).expr
            if (value.containsKey("quantity")) entry.quantity = new Expression(value.quantity).expr
            if (value.containsKey("label")) entry.label = new Expression(value.label).expr
            colorMap.addColorMapEntry(entry)
        }
        colorMap
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("ColorMap", ['values': values, 'type': type, 'extended': extended])
    }
}

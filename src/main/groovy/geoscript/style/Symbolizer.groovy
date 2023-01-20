package geoscript.style

import geoscript.feature.Field
import geoscript.filter.Filter
import geoscript.style.io.SLDWriter
import groovy.transform.AutoClone
import org.geotools.styling.Style as GtStyle
import org.geotools.styling.Rule
import org.geotools.factory.CommonFactoryFinder
import org.geotools.styling.StyleFactoryImpl
import org.geotools.styling.StyleBuilder
import org.opengis.filter.FilterFactory
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.PointSymbolizer
import org.geotools.styling.LineSymbolizer
import org.geotools.styling.PolygonSymbolizer
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.RasterSymbolizer
import geoscript.filter.Color
import org.geotools.styling.FeatureTypeStyle

/**
 * A Base class for all Symbolizers.   All Symbolizers can have a Filter, min and max scales, and a z-index.
 * <p><blockquote><pre>
 * Symbolizer sym = new Fill("white")
 * sym.where(new Filter("name='Washington'"))
 * sym.range(100, 500)
 * sym.zindex(5)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
@AutoClone
class Symbolizer implements Style, Cloneable {

    /**
     * The Filter
     */
    Filter filter

    /**
     * A List of min and max scales
     */
    Scale scale

    /**
     * The z index
     */
    int z

    /**
     * The title of the Symbolizer
     */
    protected String title

    /**
     * A Map of Symbolizer options
     */ 
    Map options = [:]

    /**
     * A Map of FeatureTypeStyle options
     */
    protected Map styleOptions = [:]

    /**
     * The GeoTools StyleFactory
     */
    protected static StyleFactoryImpl styleFactory = new StyleFactoryImpl()

    /**
     * The GeoTools StyleBuilder
     */
    protected static StyleBuilder styleBuilder = new StyleBuilder()

    /**
     * The GeoTools FilterFactory
     */
    protected static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null)

    /**
     * Create a new Symbolizer
     */
    Symbolizer() {
        filter = Filter.PASS
        scale = new Scale(-1,-1)
        z = 0
    }

    /**
     * Apply a filter to the symbolizer.  The Filter can be a CQL string
     * or a geoScript.filter.Filter
     * @param filter A CQL String or a Filter
     * @return The Symbolizer
     */
    Symbolizer where(def filter) {
        this.filter = this.filter + new Filter(filter)
        this
    }

    /**
     * Apply min/max scale denominator
     * @param min The min scale (defaults to -1)
     * @param max The max scale (defaults to -1)
     * @return The Symbolizer
     */
    Symbolizer range(double min = -1, double max = -1) {
        scale = new Scale(min, max)
        this
    }

    /**
     * Apply min/max scale denominator using keywords.
     * <pre>
     * {@code
     * new Fill('black').range(min: '100', max: '2000')
     * new Stroke('teal').range(min: '1000')
     * }
     * </pre>
     * @param minMax A Map of named parameters (min, max)
     * @return The Symbolizer
     */
    Symbolizer range(Map minMax) {
        scale = new Scale(minMax.get("min",-1), minMax.get("max",-1))
        this
    }
    
    /**
     * Apply a z-index.  Symbolizers with higher z-index are drawn on
     * the top of those with smaller z-index
     * @param z The z-index
     * @return The Symbolizer
     */
    Symbolizer zindex(int z) {
        this.z = z
        this
    }

    /**
     * Set the title
     * @param title The title
     * @return This Symbolizer
     */
    Symbolizer title(String title) {
        this.title = title
        this
    }

    /**
     * Get the title
     * @return The title
     */
    String getTitle() {
        this.title
    }

    /**
     * Set composite (copy, destination, source-over, destination-over, source-in, destination-in,
     * source-out, destination-out, source-atop, destination-atop, xor) or blending (multiply, screen, overlay, darken,
     * lighten, color-dodge, color-burn, hard-light, soft-light, difference, exclusion)
     * @param params The optional named parameters:
     * <ul>
     *     <li>opacity = The opacity value between 0 and 1.  It defaults to 1.</li>
     *     <li>base = The flag that indicates whether this composite should be used as base or not.  Defaults to false.</li>
     *     <li>symbolizer = The flag that indicates whether the composite should be applied to this symbolizer (true)
     *         or all grouped symbolizers / at the featuretype style (false).  Defaults to true.
     *     </li>
     * </ul>
     * @param composite The composite of blending value
     * @return The Symbolizer
     */
    Symbolizer composite(Map params = [:], String composite) {
        double opacity = params.get("opacity", 1.0)
        boolean isBase = params.get("base", false)
        boolean isSymbolizer = params.get("symbolizer", true)
        String compositeValue = composite
        if (opacity != 1.0) {
            compositeValue = "${compositeValue}, ${opacity}"
        }
        if (isSymbolizer) {
            this.options.composite = compositeValue
        } else {
            this.styleOptions.composite = compositeValue
        }
        if (isBase) {
            this.styleOptions["composite-base"] = "true"
        }
        this
    }

    /**
     * Set single layer z-ordering
     * @param fields The List of Fields.  The items in the List an be a Field, a Map with field and direction (A or D)
     * keys, or just String with field name and direction.
     * @return
     */
    Symbolizer sortBy(List fields) {
        this.styleOptions["sortBy"] = fields.collect { Object fld ->
            if (fld instanceof Field) {
                fld.name
            } else if (fld instanceof java.util.Map) {
                String name = fld.field instanceof Field ? fld.field.name : fld.field
                "${name} ${fld.direction}"
            } else {
                fld
            }
        }.join(",")
        this
    }

    /**
     * Set cross layer z-ordering
     * @param group The group name
     * @param fields The List of Fields.  The items in the List an be a Field, a Map with field and direction (A or D)
     * keys, or just String with field name and direction.
     * @return This Symbolizer
     */
    Symbolizer sortBy(String group, List fields) {
        this.styleOptions["sortByGroup"] = group
        sortBy(fields)
    }

    /**
     * Write this Symbolizer to an SLD document
     * @param out The OutputStream
     */
    void asSLD(OutputStream out = System.out) {
        def writer = new SLDWriter()
        writer.write(this, out)
    }

    /**
     * Write this Symbolizer to a SLD File
     * @param file The SLD File
     */
    void asSLD(File file) {
        def writer = new SLDWriter()
        writer.write(this, file)
    }

    /**
     * Get this Symbolizer as an SLD String
     * @return An SLD String
     */
    String getSld() {
        def writer = new SLDWriter()
        writer.write(this)
    }

    /**
     * Combine this Symbolizer with another.
     * @param other The other Symbolizer
     * @return A new Composite Symbolizer
     */
    Composite plus(Symbolizer other) {
        new Composite([this, other])
    }

    /**
     * Combine this Symbolizer with another.
     * @param other The other Symbolizer
     * @return A new Composite Symbolizer
     */
    Composite and(Symbolizer other) {
        plus(other)
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer.
     * @param rule The GeoTools Rule
     */
    protected void prepare(Rule rule) {
        // This is usually override by subclasses
    }

    /**
     * Prepare the GeoTools FeatureTypeStyle and Rule by applying this Symbolizer.
     * @param fts The GeoTools FeatureTypeStyle
     * @param rule The GeoTools Rule
     */
    protected void prepare(FeatureTypeStyle fts, Rule rule) {
        prepare(rule)
    }

    /**
     * Apply this Symbolizer to the GeoTools Symbolizer
     * @param sym The GeoTools Symbolizer
     */
    protected void apply(GtSymbolizer sym) {
        options.each{e ->
            sym.options[e.key] = e.value
        }
    }

    /**
     * Get a List of GeoTools Symbolizers from the Rule of the given Class.  If no Symbolizers
     * of the given Class exist in the Rule, one is created.
     * @param rule The GeoTools Rule
     * @param clazz The GeoTools Symbolizer Class
     * @return A List of GeoTools Symbolizers
     */
    protected static List<GtSymbolizer> getGeoToolsSymbolizers(Rule rule, Class clazz) {
        List<GtSymbolizer> syms = rule.symbolizers().findAll({sym -> clazz.isAssignableFrom(sym.class)})
        if (syms.size() == 0) {
            GtSymbolizer sym = createGeoToolsSymbolizer(clazz)
            rule.symbolizers().add(sym)
            syms.add(sym)
        }
        syms
    }

    /**
     * Create a GeoTools Symbolizer of the given Class
     * @param clazz The GeoTools Symbolizer Class
     * @return A GeoTools Symbolizer or null
     */
    protected static GtSymbolizer createGeoToolsSymbolizer(Class clazz) {
        GtSymbolizer sym = null
        if (PointSymbolizer.isAssignableFrom(clazz)) {
            sym = styleBuilder.createPointSymbolizer()
        } else if (PolygonSymbolizer.isAssignableFrom(clazz)) {
            sym = styleBuilder.createPolygonSymbolizer()
            sym.stroke = null
        } else if (LineSymbolizer.isAssignableFrom(clazz)) {
            sym = styleBuilder.createLineSymbolizer()
        } else if (TextSymbolizer.isAssignableFrom(clazz)) {
            sym = styleBuilder.createTextSymbolizer()
        } else if (RasterSymbolizer.isAssignableFrom(clazz)) {
            sym = styleBuilder.createRasterSymbolizer()
        }
        sym
    }

    /**
     * Get the GeoTools Style from this Symbolizer
     * @return The GeoTools Style
     */
    GtStyle getGtStyle() {

        // First level groups by zindex
        Map ztbl = [:]
        List q = [this]
        while(q.size() > 0) {
            def sym = q[0]
            q.remove(0)
            if (sym instanceof Composite) {
                sym.parts.reverseEach {x ->
                    q.add(0,x)
                }
            } else {
                if (!ztbl.containsKey(sym.z)) {
                    ztbl[sym.z] = new LinkedHashMap()
                }

                // Second level groups by scale
                Map stbl = ztbl[sym.z]
                if (!stbl.containsKey(sym.scale)) {
                    stbl[sym.scale] = new LinkedHashMap()
                }

                // Third level groups by filter
                Map ftbl = stbl[sym.scale]
                if (!ftbl.containsKey(sym.filter)) {
                    ftbl[sym.filter] = []
                }
                ftbl[sym.filter].add(sym)
            }
        }

        GtStyle style = styleFactory.createStyle()
        ztbl.keySet().each {int z ->
            Map stbl = ztbl[z]
            def fts = styleFactory.createFeatureTypeStyle()
            style.addFeatureTypeStyle(fts)
            stbl.keySet().each {scale ->
                Map ftbl = stbl[scale]
                ftbl.keySet().each{Filter fil ->
                    List syms = ftbl[fil]
                    Rule rule = styleFactory.createRule()
                    fts.rules().add(rule)
                    if (scale[0] > -1) {
                        rule.minScaleDenominator = scale.min
                    }
                    if (scale[1] > -1) {
                        rule.maxScaleDenominator = scale.max
                    }
                    rule.filter = fil.filter

                    syms.each {Symbolizer sym ->
                        rule.name = sym.title
                        sym.prepare(fts, rule)
                        // Apply FeatureTypeStyle vendor options
                        if (!sym.styleOptions.isEmpty()) {
                            fts.options.putAll(sym.styleOptions)
                        }
                    }
                }
            }
        }

        return style
    }

    /**
     * Build a string representation of the Symbolizer
     * @param name The name of the Symbolizer (Fill, Hatch, Halo, ect...)
     * @param properties A Map of properties
     * @return A String
     */
    protected String buildString(String name, Map properties) {
        "${name}(${properties.collect{p -> p.key + ' = ' + p.value}.join(', ')})${filter != Filter.PASS ? filter : ''}"
    }

    /**
     * Get a default Symbolizer for the given geometry type
     * @param geometryType The geometry type
     * @return A Symbolizer
     */
    static Symbolizer getDefault(String geometryType, def color = new Color("#f2f2f2")) {
        getDefault([color: color], geometryType)
    }

    /**
     * Get a default Symbolizer for the given geometry type.
     * @param options Optional named parameters:
     * <ul>
     *     <li>color = The Color (#f2f2f2)</li>
     *     <li>opacity = The opacity (1.0)</li>
     *     <li>size = The shape size (6)</li>
     *     <li>type = The shape type (circle)</li>
     * @param geometryType The geometry type
     * @return A Symbolizer
     */
    static Symbolizer getDefault(Map options, String geometryType) {
        if (!geometryType) {
            geometryType = "geometry"
        }
        Object color = options.get("color", new Color("#f2f2f2"))
        double opacity = options.get("opacity", 1.0)
        int size = options.get("size", 6)
        String type = options.get("type","circle")
        def sym;
        Color baseColor = new Color(color)
        Color darkerColor = baseColor.darker()
        if (geometryType.toLowerCase().endsWith("point")) {
            sym = new Shape(baseColor, size, type, opacity).stroke(darkerColor, 0.1)
        }
        else if (geometryType.toLowerCase().endsWith("linestring") 
            || geometryType.toLowerCase().endsWith("linearring")
            || geometryType.toLowerCase().endsWith("curve")) {
            sym = new Stroke(baseColor, 0.5)
        }
        else if (geometryType.toLowerCase().endsWith("polygon")) {
            sym = new Fill(baseColor, opacity) + new Stroke(darkerColor, 0.5)
        }
        else {
            sym = new Shape(baseColor, size, type, opacity).stroke(darkerColor, 0.1) +
            new Fill(baseColor, opacity) +
            new Stroke(darkerColor, 0.2)
        }
        sym
    }

    /**
     * A private static class to represent min and max Scale.
     */
    private static class Scale implements Comparable<Scale> {

        double min = -1

        double max = -1

        Scale(double min, double max) {
            this.min = min;
            this.max = max;
        }

        def getAt(int i) {
            if (i == 0) {
                return min
            } else if (i == 1) {
                return max
            } else {
                return null
            }
        }

        @Override
        boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Scale other = (Scale) obj;
            if (Double.doubleToLongBits(this.min) != Double.doubleToLongBits(other.min)) {
                return false;
            }
            if (Double.doubleToLongBits(this.max) != Double.doubleToLongBits(other.max)) {
                return false;
            }
            return true;
        }

        @Override
        int hashCode() {
            int hash = 7;
            hash = 47 * hash + (int) (Double.doubleToLongBits(this.min) ^ (Double.doubleToLongBits(this.min) >>> 32));
            hash = 47 * hash + (int) (Double.doubleToLongBits(this.max) ^ (Double.doubleToLongBits(this.max) >>> 32));
            return hash;
        }

        @Override
        int compareTo(Scale other) {

            // 100,200 :: 100,200
            if (this.min == other.min && this.max == other.max) {
                return 0
            }
            // 0,200 :: 100,200
            else if (this.min < other.min) {
                return -1
            }
            // 100,200 :: 0, 200
            else if (this.min > other.min) {
                return 1
            }
            else /*if (this.min == other.min)*/ {
                if (this.max < other.max) {
                    return -1
                } else {
                    return 1
                }
            }
        }

        @Override String toString() {
            "${min} - ${max}"
        }

    }

}
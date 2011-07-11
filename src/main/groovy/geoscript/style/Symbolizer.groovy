package geoscript.style

import geoscript.feature.Feature
import geoscript.layer.Layer
import geoscript.layer.Cursor
import geoscript.filter.Filter
import geoscript.style.io.SLDWriter
import java.awt.Color
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

/**
 * A Base class for all Symbolizers
 * @author Jared Erickson
 */
class Symbolizer implements Style {

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
     * A Map of options
     */ 
    Map options = [:]

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
     * Apply a filte to the symbolizer.  The Filter can be a CQL string
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
     * Write this Symbolizer to an SLD document
     * @param out The OutputStream
     */
    void asSLD(OutputStream out = System.out) {
        def writer = new SLDWriter()
        writer.write(this, out)
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
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    protected void prepare(Rule rule) {
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
        }
        sym
    }

    /**
     * Get the GeoTools Style from this Symbolizer
     * @return The GeoTools Style
     */
    GtStyle getStyle() {

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
                    fts.addRule(rule)
                    if (scale[0] > -1) {
                        rule.minScaleDenominator = scale.min
                    }
                    if (scale[1] > -1) {
                        rule.maxScaleDenominator = scale.max
                    }
                    rule.filter = fil.filter

                    syms.each {Symbolizer sym ->
                        sym.prepare(rule)
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
    static Symbolizer getDefault(String geometryType, def color = ColorUtil.getRandomPastel()) {
        def sym;
        Color baseColor = ColorUtil.getColor(color)
        Color darkerColor = baseColor.darker()
        if (geometryType.toLowerCase().endsWith("point")) {
            sym = new Shape(ColorUtil.toHex(baseColor))
        }
        else if (geometryType.toLowerCase().endsWith("linestring") 
            || geometryType.toLowerCase().endsWith("linearring")
            || geometryType.toLowerCase().endsWith("curve")) {
            sym = new Stroke(ColorUtil.toHex(baseColor))
        }
        else if (geometryType.toLowerCase().endsWith("polygon")) {
            sym = new Fill(ColorUtil.toHex(baseColor)) + new Stroke(ColorUtil.toHex(darkerColor))
        }
        else {
            sym = new Shape(ColorUtil.toHex(baseColor)) +
            new Fill(ColorUtil.toHex(baseColor)) +
            new Stroke(ColorUtil.toHex(darkerColor))
        }
        sym
    }

    /**
     * Create a Symbolizer for every unique value of a Layer's Field
     * @param layer The Layer
     * @param field The Field name
     * @param colors A Closure (which takes index based on 0 and a value), a Palette name, or a List of Colors
     * @return A Symbolizer
     */
    static Symbolizer createUniqueValuesSymbolizer(Layer layer, String field, def colors = {index, value -> ColorUtil.getRandomPastel()}) {

        // Collect the unique values
        Set uniqueValueSet = new HashSet()

        Cursor c = layer.cursor
        while(c.hasNext()) {
            Feature f = c.next()
            uniqueValueSet.add(f.get(field))
        }
        c.close()

        List uniqueValues = new ArrayList(uniqueValueSet)
        Collections.sort(uniqueValues)

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = ColorUtil.getPaletteColors(colors)
        }

        // Create the list of Rules
        int i = 0
        List symbolizers = uniqueValues.collect{value ->

            // Get the Color
            def color
            if (colors instanceof Closure) {
                color = colors.call(i, value)
            }
            else {
                // Set our counter back to 0 if
                // it exceeds the number of colors
                // in the List
                if (i >= colors.size()) {
                    i = 0
                }
                color = colors[i]
            }

            // Make sure color is a java.awt.Color
            if (color instanceof String) {
                color = ColorUtil.getColor(color)
            }

            // Increment our counter
            i++

            // Create the Symbolizer
            // name: "${field} = ${value}",
            // title: "${field} = ${value}",
            Symbolizer.getDefault(layer.schema.geom.typ, color).where(new Filter(filterFactory.equals(filterFactory.property(field), filterFactory.literal(value))))
        }

        // Create our Composite Symbolizer with a Symbolizer for each unique value
        new Composite(symbolizers)
    }

    /**
     * Create a graduated Symbolizer
     * @param layer The Layer
     * @param field The Field name
     * @param method The classification method (Quantile or EqualInterval)
     * @param number The number of categories
     * @param colors A Palette name, or a List of Colors
     * @param elseMode The else mode (ignore, min, max)
     * @return The graduated Symbolizer
     */
    static Symbolizer createGraduatedSymbolizer(Layer layer, String field, String method, int number, def colors, String elseMode = "ignore") {

        org.opengis.filter.FilterFactory2 ff = org.geotools.factory.CommonFactoryFinder.getFilterFactory2(null)
        org.opengis.filter.expression.Function function = ff.function(method, ff.property(field), ff.literal(number))
        org.geotools.filter.function.Classifier classifier = (org.geotools.filter.function.Classifier) function.evaluate(layer.fs.features)

        int elseModeInt
        if (elseMode.equalsIgnoreCase("min")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMIN
        } else if (elseMode.equalsIgnoreCase("max")) {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_INCLUDEASMAX
        } else {
            elseModeInt = org.geotools.brewer.color.StyleGenerator.ELSEMODE_IGNORE
        }

        // If the colors argument is a String treat it
        // like a Palette
        if (colors instanceof String) {
            colors = ColorUtil.getPaletteColors(colors) as java.awt.Color[]
        }
        else {
            colors = colors.collect{c ->
                ColorUtil.getColor(c)
            } as java.awt.Color[]
        }

        // Generate the FeatureTypeStyle
        org.geotools.styling.FeatureTypeStyle featureTypeStyle = org.geotools.brewer.color.StyleGenerator.createFeatureTypeStyle(
            classifier,
            (org.geotools.filter.Expression) ff.property(field),
            colors,
                "test",
            layer.fs.schema.geometryDescriptor,
            elseModeInt,
            0.5,
            null);

        String geometryType = layer.schema.geom.typ
        List symbolizers = featureTypeStyle.rules().collect{rule ->
            def sym = rule.symbolizers()[0]
            if (geometryType.equalsIgnoreCase("polygon") || geometryType.equalsIgnoreCase("multipolygon")) {
                return (new Fill(sym.fill.color.value) + new Stroke(sym.stroke.color.value)).where(new Filter(rule.filter))
            } else if (geometryType.equalsIgnoreCase("line") || geometryType.equalsIgnoreCase("multiline")) {
                return new Stroke(sym.stroke.color.value).where(new Filter(rule.filter))
            }  else if (geometryType.equalsIgnoreCase("point") || geometryType.equalsIgnoreCase("multipoint")) {
                return new Shape(sym.graphic.mark.fill.color.value, sym.graphic.size.value, sym.graphic.mark.wellKnownName.vallue).where(new Filter(rule.filter))
            } else {
                null
            }
        }

        new Composite(symbolizers)
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
        public boolean equals(Object obj) {
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
        public int hashCode() {
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





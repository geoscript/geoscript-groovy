package geoscript.style

import org.geotools.styling.Rule
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer for a Label Font.
 * A Font consists of a style (normal, italic, oblique), a weight (normal or bold),
 * a size, and a font family.
 * <p>You can create a Font by specifying the style, weight, size, and family:</p>
 * <p><code>def f = new Font("normal", "bold", 12, "Arial")</code></p>
 * Or using named parameters:
 * <p><code>def f = new Font(weight: "bold", size: 32)</code></p>
 * @author Jared Erickson
 */
class Font extends Symbolizer {

    /**
     * Font weights (normal, bold)
     */
    static final List<String> weights = ["normal", "bold"]

    /**
     * Font styles (normal, italic, oblique)
     */
    static final List<String> styles = ["normal", "italic", "oblique"]

    /**
     * The font style (normal, italic, oblique)
     */
    String style = "normal"

    /**
     * The font weight (normal, bold)
     */
    String weight = "normal"

    /**
     * The font size (8,10,12,24,ect...)
     */
    int size = 10

    /**
     * The font family (serif, Arial, Verdana)
     */
    String family = "serif"

    /**
     * Create a new Font with named parameters.
     * <p><code>def f = new Font(weight: "bold", size: 32)</code></p>
     * @param map A Map of named parameters.
     */
    Font(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a new Font.
     * <p><code>def f = new Font("normal", "bold", 12, "Arial")</code></p>
     * @param style The Font style (normal, italic, oblique)
     * @param weight The Font weight (normal, bold)
     * @param size The Font size (8,10,12,24,ect...)
     * @param family The Font family (serif, Arial, Verdana)
     */
    Font(String style = "normal", String weight = "normal", int size = 10, String family = "serif") {
        super()
        this.style = style
        this.weight = weight
        this.size = size
        this.family = family
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
        def f = styleFactory.createFont(
            filterFactory.literal(family),
            filterFactory.literal(style),
            filterFactory.literal(weight),
            filterFactory.literal(size),
        )
        sym.font = f
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Font", ['style': style, 'weight': weight, 'size': size, 'family': family])
    }
}


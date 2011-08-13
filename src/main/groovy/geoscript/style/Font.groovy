package geoscript.style

import geoscript.filter.Expression
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
    Expression style = new Expression("normal")

    /**
     * The font weight (normal, bold)
     */
    Expression weight = new Expression("normal")

    /**
     * The font size (8,10,12,24,ect...)
     */
    Expression size = new Expression(10)

    /**
     * The font family (serif, Arial, Verdana)
     */
    Expression family = new Expression("serif")

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
    Font(def style = "normal", def weight = "normal", def size = 10, def family = "serif") {
        super()
        this.style = new Expression(style)
        this.weight = new Expression(weight)
        this.size = new Expression(size)
        this.family = new Expression(family)
    }

    /**
     * Set the font style (normal, italic, oblique)
     * @param style The font style
     */
    void setStyle(def style) {
        this.style = new Expression(style)
    }

    /**
     * Set the font weight (normal, bold)
     * @param weight The font weight
     */
    void setWeight(def weight) {
        this.weight = new Expression(weight)
    }

    /**
     * The font size (8,10,12,24,ect...)
     * @param size The font size
     */
    void setSize(def size) {
        this.size = new Expression(size)
    }

    /**
     * The font family (serif, Arial, Verdana)
     * @param family
     */
    void setFamily(def family) {
        this.family = new Expression(family)
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
            family.expr,
            style.expr,
            weight.expr,
            size.expr,
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


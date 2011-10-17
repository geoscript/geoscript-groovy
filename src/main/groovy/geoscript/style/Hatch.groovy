package geoscript.style

import geoscript.filter.Expression
import org.geotools.styling.Rule
import org.geotools.styling.LineSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.Graphic
import org.geotools.styling.Mark

/**
 * A Symbolizer that repeats a pattern. A hatch can be applied to linear and area
 * geometries.
 * <p>You can create a Hatch by specifying the name, Stroke, and size:<p>
 * <p><code>def hatch = new Hatch("times", new Stroke("wheat", 1.2, [5,2], "square", "bevel"), 12.2)</code></p>
 * Or with named parameters:
 * <p><code>def hatch = new Hatch(size: 10, stroke: new Stroke("wheat",1.0), name: "slash")</code></p>
 * @author Jared Erickson
 */
class Hatch extends Symbolizer {

    /**
     * The name of the pattern (vertline, horline, slash, backslash, plus, times)
     */
    Expression name = new Expression("shape://vertline")

    /**
     * The Stroke
     */
    Stroke stroke

    /**
     * The size
     */
    Expression size = new Expression(8)

    /**
     * Create a new Hatch with named parameters.
     * <p><code>def hatch = new Hatch(size: 10, stroke: new Stroke("wheat",1.0), name: "slash")</code></p>
     * @param map A Map of named parameters.
     */
    Hatch(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k as String)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a new Hatch.
     * <p><code>def hatch = new Hatch("times", new Stroke("wheat", 1.2, [5,2], "square", "bevel"), 12.2)</code></p>
     * @param name (vertline, horline, slash, backslash, plus, times)
     * @param stroke A Stroke
     * @param size The size
     */
    Hatch(def name, Stroke stroke = new Stroke(), def size = 8) {
        super()
        setName(name)
        this.stroke = stroke
        this.size = new Expression(size)
    }

    /**
     * Set the name of the pattern (vertline, horline, slash, backslash, plus, times)
     * @param name The name of the pattern (vertline, horline, slash, backslash, plus, times)
     */
    void setName(def name) {
        if (name instanceof Expression && name.value in ['vertline', 'horline', 'slash', 'backslash', 'plus', 'times']) {
            name = "shape://${name.value}".toString()
        }
        if (name instanceof String && name in ['vertline', 'horline', 'slash', 'backslash', 'plus', 'times']) {
            name = "shape://${name}".toString()
        }
        this.name = new Expression(name)
    }

    /**
     * Set the size
     * @param size The size
     */
    void setSize(def size) {
        this.size = new Expression(size)
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, LineSymbolizer).each{s ->
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
        if (!sym.stroke) {
            sym.stroke = styleBuilder.createStroke()
        }
        sym.stroke.graphicStroke = createHatch()
    }

    /**
     * Create a GeoTools Graphic based on this Hatch
     * @return A GeoTools Graphic
     */
    protected Graphic createHatch() {
        Mark mark = styleFactory.createMark()
        mark.wellKnownName = name.expr
        mark.stroke = stroke.createStroke()

        Graphic graphic = styleBuilder.createGraphic()
        graphic.graphicalSymbols().clear()
        graphic.graphicalSymbols().add(mark)
        graphic.size = size.expr

        graphic
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Hatch", ['name': name, 'stroke': stroke, 'size': size])
    }
}


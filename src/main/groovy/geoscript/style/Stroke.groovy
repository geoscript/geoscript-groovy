package geoscript.style

import org.geotools.styling.Rule
import org.geotools.styling.Stroke as GtStroke
import org.geotools.styling.LineSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer for linear geometries that consists of a color and a width.
 * <p>You can create a Stroke with a color and width:</p>
 * <p><code>def shape = new Shape("#ff0000", 0.25)</code></p>
 * Or with named parameters:
 * <p><code>def stroke = new Stroke(width: 1.2, dash: [5,2], color: "#ff00ff", opacity: 0.75)</code></p>
 * @author Jared Erickson
 */
class Stroke extends Symbolizer {

    /**
     * The color in hexadecimal format (#00FF00)
     */
    String color

    /**
     * The width (1, 2, 5, ect...)
     */
    double width

    /**
     * The opacity (0: transparent - 1 opaque)
     */
    double opacity = 1;

    /**
     * The dash pattern. Odd items specify length in pixels of the dash.
     * Even items specify spaces.
     */
    List dash

    /**
     * The line cap (butt, round, square)
     */
    String cap

    /**
     * The line join (miter, round, bevel)
     */
    String join

    /**
     * The Hatch
     */
    Hatch hatch

    /**
     * The Shape for spaced graphics
     */
    Shape shape

    /**
     * Create a new Stroke.
     * <p><code>def shape = new Shape("#ff0000", 0.25)</code></p>
     * @param color The color
     * @param width The width
     * @param dash The dash pattern
     * @param cap The line cap (round, butt, square)
     * @param join The line join (mitre, round, bevel)
     */
    Stroke(def color = "#000000", double width = 1, List dash = null, String cap = null, String join = null, double opacity = 1.0) {
        super()
        this.color = ColorUtil.toHex(color)
        this.width = width
        this.dash = dash
        this.cap = cap
        this.join = join
        this.opacity = opacity
    }

    /**
     * Create a new Stroke with named parameters.
     * <p><code>def stroke = new Stroke(width: 1.2, dash: [5,2], color: "#ff00ff", opacity: 0.75)</code></p>
     * @param map A Map of named parameters.
     */
    Stroke(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Set the color
     * @param color  The color (#ffffff, red)
     */
    void setColor(def color) {
        this.color = ColorUtil.toHex(color)
    }

    /**
     * Add a Hatch pattern to this Stroke
     * @param name The pattern name
     * @param stroke The Stroke
     * @param size The size
     * @return This Stroke
     */
    Stroke hatch(String name, Stroke stroke = new Stroke(), double size = 8) {
        this.hatch = new Hatch(name, stroke, size)
        this
    }

    /**
     * Add a Shape to this Stroke for creating spaced graphic symbols
     * @param shape The Shape
     * @return This Stroke
     */
    Stroke shape(Shape shape) {
        this.shape = shape
        this
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
        sym.stroke = createStroke()
        if (shape) {
            shape.apply(sym);
        }
    }

    /**
     * Create a GeoTools Stroke from this GeoScript Stroke
     * @return A GeoTools Stroke
     */
    protected GtStroke createStroke(GtSymbolizer sym) {
        def ff = filterFactory
        GtStroke stroke = styleFactory.createStroke(ff.literal(color), ff.literal(width))
        if (dash) {
            if (dash instanceof List) {
                if (dash[0] instanceof List) {
                    stroke.dashArray = dash[0]
                    stroke.dashOffset = ff.literal(dash[1])
                } else {
                    stroke.dashArray = dash
                }
            } else {
                stroke.dashArray = dash.split(",")
            }
        }
        if (cap) stroke.lineCap = ff.literal(cap)
        if (join) stroke.lineJoin = ff.literal(join)
        if (hatch) stroke.graphicStroke = hatch.createHatch()
        stroke.opacity = ff.literal(opacity)
        stroke
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Stroke", ['color': color, 'width': width])
    }
}


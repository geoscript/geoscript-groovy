package geoscript.style

import geoscript.filter.Expression
import org.geotools.styling.Rule
import org.geotools.styling.Mark
import org.geotools.styling.PointSymbolizer
import org.geotools.styling.PolygonSymbolizer
import org.geotools.styling.LineSymbolizer
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer
import org.geotools.styling.Graphic
import geoscript.filter.Color

/**
 * A Symbolizer for point geometries that consists of a color and size.
 * <p>You can create a new Shape with a color, size, type, opacity, and rotation angle:</p>
 * <p><blockquote><pre>
 * def shape = new Shape("#ff0000", 8, "circle", 0.55, 0)
 * </pre></blockquote></p>
 * Or with named parameters:
 * <p><blockquote><pre>
 * def shape = new Shape(type: "star", size: 4, color: "#ff00ff")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Shape extends Symbolizer {

    /**
     * The color (#ff000, blue, [255,255,0])
     */
    Expression color

    /**
     * The size (6, 10, 12, ect...)
     */
    Expression size = new Expression(6)

    /**
     * The type (circle, square, triangle, star, cross, or x).
     */
    Expression type = new Expression("circle")

    /**
     * The Stroke
     */
    Stroke stroke

    /**
     * The rotation angle (0-360 or a geoscript.filter.Function)
     */
    Expression rotation

    /**
     * The opacity (0: transparent - 1 opaque)
     */
    Expression opacity

    /**
     * The anchor point
     */
    def anchorPoint

    /**
     * The displacement
     */
    def displacement

    /**
     * Create a new Shape
     */
    Shape() {
        super()
    }

    /**
     * Create a new Shape with named parameters.
     * <p><blockquote><pre>
     * def shape = new Shape(type: "star", size: 4, color: "#ff00ff")
     * </pre></blockquote></p>
     * @param map A Map of named parameters.
     */
    Shape(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a new Shape.
     * <p><blockquote><pre>
     * def shape = new Shape("#ff0000", 8, "circle", 0.55, 0)
     * </pre></blockquote></p>
     * @param color The color
     * @param size The size
     * @param type The type
     * @param opacity The opacity (0-1)
     * @param angle The angle or rotation (0-360)
     */
    Shape(def color, def size = 6, def type = "circle", def opacity = 1.0, def angle = 0) {
        super()
        this.color = color instanceof Expression ? color : new Color(color)
        this.opacity = new Expression(opacity)
        this.size = new Expression(size)
        this.type = new Expression(type)
        this.rotation = new Expression(angle)
    }

    /**
     * Set the color
     * @param color  The color (#ffffff, red)
     */
    void setColor(def color) {
        this.color = color instanceof Expression ? color : new Color(color)
    }

    /**
     * Set the size
     * @param size The size
     */
    void setSize(def size) {
        this.size = new Expression(size)
    }

    /**
     * Set the type (circle, square, triangle, star, cross, or x).
     * @param type The type (circle, square, triangle, star, cross, or x).
     */
    void setType(def type) {
        this.type = new Expression(type)
    }

    /**
     * Set the rotation angle (0-360 or a geoscript.filter.Function)
     * @param rotation The rotation angle (0-360 or a geoscript.filter.Function)
     */
    void setRotation(def rotation) {
        this.rotation = new Expression(rotation)
    }

    /**
     * Set the opacity (0: transparent - 1 opaque)
     * @param opacity The opacity (0: transparent - 1 opaque)
     */
    void setOpacity(def opacity) {
        this.opacity = new Expression(opacity)
    }

    /**
     * Set the anchor points (List of two values between 0 and 1)
     * @param anchorPoints A List of two values between 0 and 1
     */
    void setAnchorPoint(List anchorPoints) {
        this.anchorPoint = styleFactory.createAnchorPoint(
            new Expression(anchorPoints[0]).expr, new Expression(anchorPoints[1]).expr)
    }

    /**
     * Set the displacement (List of two values between 0 and 1)
     * @param displacement A List of two values between 0 and 1
     */
    void setDisplacement(List displacements) {
        this.displacement = styleFactory.createDisplacement(
                new Expression(displacements[0]).expr, new Expression(displacements[1]).expr)
    }

    /**
     * Add a Stroke to this Shape
     * @param color The color
     * @param width The width
     * @param dash The dash pattern
     * @param cap The line cap (round, butt, square)
     * @param join The line join (mitre, round, bevel)
     * @return This Shape
     */
    Shape stroke(def color = "#000000", def width = 1, def dash = null, def cap = null, def join = null) {
        this.stroke = new Stroke(color, width, dash, cap, join)
        this
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, PointSymbolizer).each{s ->
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
        Graphic graphic = createGraphic(sym)
        if (anchorPoint) {
            graphic.anchorPoint = anchorPoint
        }
        if (displacement) {
            graphic.displacement = displacement
        }
        graphic.size = size.expr
        if (rotation != null && rotation.value != null) {
            graphic.rotation = rotation.expr
        }
        graphic.graphicalSymbols().clear()
        graphic.graphicalSymbols().add(createMark())
    }

    /**
     * Create a GeoTools Mark from this Shape
     * @return A GeoTools Mark
     */
    protected Mark createMark() {
        Mark mark = styleFactory.createMark()
        if (color != null && color.value != null) {
            mark.fill = new Fill(color, opacity).createFill()
        } else {
            mark.fill = null
        }
        if (stroke) {
            mark.stroke = stroke.createStroke()
        } else {
            mark.stroke = null
        }
        mark.wellKnownName = type.expr
        return mark
    }

    /**
     * Create a GeoTools Graphic from The GeoTools Symbolizer.
     * @param sym The GeoTools Symbolizer
     * @return A GeoTools Graphic
     */
    protected Graphic createGraphic(GtSymbolizer sym) {
        if (sym instanceof PointSymbolizer || sym instanceof TextSymbolizer) {
            if (!sym.graphic) {
                sym.graphic = styleBuilder.createGraphic()
            }
            return sym.graphic
        } else if (sym instanceof PolygonSymbolizer) {
            if (!sym.fill.graphicFill) {
                sym.fill.graphicFill = styleBuilder.createGraphic()
            }
            return sym.fill.graphicFill
        } else if (sym instanceof LineSymbolizer) {
            if (!sym.stroke.graphicStroke) {
                sym.stroke.graphicStroke = styleBuilder.createGraphic()
            }
            return sym.stroke.graphicStroke
        } else {
            return null;
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        Map options = ['color': color, 'size': size, 'type': type]
        if (stroke) {
            options.put('stroke', stroke)
        }
        buildString("Shape", options)
    }
}


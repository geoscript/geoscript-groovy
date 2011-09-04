package geoscript.style

import geoscript.filter.Function

/**
 * The Symbolizers class is a collection of static methods that can be used
 * to create Symbolizers.
 */
class Symbolizers {

    /**
     * Create a new Fill.
     * <p><code>def f = fill('#ff0000', 0.5)</code></p>
     * <p><code>def f = fill('red', 0.5)</code></p>
     * <p><code>def f = fill([255,0,0], 0.5)</code></p>
     * @param color The Color
     * @param opacity The opacity (1 opaque to 0 transparent)
     * @return A Fill
     */
    static Fill fill(def color, def opacity = 1.0) {
        new Fill(color, opacity)
    }

    /**
     * Create a new Fill.
     * <p><code>def f = fill(color: '#ff0000', opacity: 0.25)</code></p>
     * @param properties A Map of named parameters.
     * @return A Fill
     */
    static Fill fill(Map properties) {
        new Fill(properties)
    }

    /**
     * Create a new Stroke.
     * <p><code>def stroke = stroke("#ff0000", 0.25, [5,2], "round", "bevel")</code></p>
     * @param color The color
     * @param width The width
     * @param dash The dash pattern
     * @param cap The line cap (round, butt, square)
     * @param join The line join (mitre, round, bevel)
     * @return A Stroke
     */
    static Stroke stroke(def color = "#000000", def width = 1, List dash = null, def cap = null, def join = null, def opacity = 1.0) {
        new Stroke(color, width, dash, cap, join, opacity)
    }

    /**
     * Create a new Stroke with named parameters.
     * <p><code>def stroke = stroke(width: 1.2, dash: [5,2], color: "#ff00ff", opacity: 0.75)</code></p>
     * @param properties A Map of named parameters.
     * @return A Stroke
     */
    static Stroke stroke(Map properties) {
        new Stroke(properties)
    }

    /**
     * Create a new Font.
     * <p><code>def f = font("normal", "bold", 12, "Arial")</code></p>
     * @param style The Font style (normal, italic, oblique)
     * @param weight The Font weight (normal, bold)
     * @param size The Font size (8,10,12,24,ect...)
     * @param family The Font family (serif, Arial, Verdana)
     * @return A Font
     */
    static Font font(def style = "normal", def weight = "normal", def size = 10, def family = "serif") {
        new Font(style, weight, size, family)
    }

    /**
     * Create a new Font with named parameters.
     * <p><code>def f = font(weight: "bold", size: 32)</code></p>
     * @param properties A Map of named parameters.
     * @return A Font
     */
    static Font font(Map properties) {
        new Font(properties)
    }

    /**
     * Create a new Halo with a Fill and radius.
     * <p><code>def h = halo(new Fill("navy"), 2.5)</code></p>
     * @param fill The Fill
     * @param radius The radius
     * @return A Halo
     */
    static Halo halo(Fill fill, def radius) {
        new Halo(fill, radius)
    }

    /**
     * Create a new Halo with named parameters.
     * <p><code>def h = halo(fill: new Fill("navy"), radius: 2.5)</code></p>
     * @param properties A Map of named parameters.
     * @return A Halo
     */
    static Halo halo(Map properties) {
        new Halo(properties)
    }

    /**
     * Create a new Hatch.
     * <p><code>def hatch = hatch("times", new Stroke("wheat", 1.2, [5,2], "square", "bevel"), 12.2)</code></p>
     * @param name (vertline, horline, slash, backslash, plus, times)
     * @param stroke A Stroke
     * @param size The size
     * @return A Hatch
     */
    static Hatch hatch(def name, Stroke stroke = new Stroke(), def size = 8) {
        new Hatch(name, stroke, size)
    }

    /**
     * Create a new Hatch with named parameters.
     * <p><code>def hatch = hatch(size: 10, stroke: new Stroke("wheat",1.0), name: "slash")</code></p>
     * @param map A Map of named parameters.
     * @return A Hatch
     */
    static Hatch hatch(Map map) {
        new Hatch(map)
    }

    /**
     * Create a new Icon with named parameters.
     * <p><code>def i = icon(format: "image/png", url: "images/star.png")</code></p>
     * @param map A Map of named parameters.
     * @return An Icon
     */
    static Icon icon(Map map) {
        new Icon(map)
    }

    /**
     * Create a new Icon.
     * <p><code>def i = icon("images/star.png", "image/png")</code></p>
     * @param url The file or url of the icon
     * @param format The image format (image/png)
     * @param size The size of the Icon (default to -1 which means auto-size)
     * @return An Icon
     */
    static Icon icon(def url, String format, def size = -1) {
        new Icon(url, format, size)
    }

    /**
     * Create a new Label with a property which is a field or attribute with which
     * to generate labels form.
     * <p><code>def l = label("STATE_ABBR")</code></p>
     * @param property The field or attribute
     * @return A Label
     */
    static Label label(def property) {
        new Label(property)
    }

    /**
     * Create a new Label with named parameters.
     * <p><code>def l = label(property: "name", font: new Font(weight: "bold")))</code></p>
     * @param map A Map of named parameters.
     * @return A Label
     */
    static Label label(Map map) {
        new Label(map)
    }

    /**
     * Create a new Shape.
     * @return A Shape
     */
    static Shape shape() {
        new Shape()
    }

    /**
     * Create a new Shape with named parameters.
     * <p><code>def s = shape(type: "star", size: 4, color: "#ff00ff")</code></p>
     * @param map A Map of named parameters.
     * @return A Shape
     */
    static Shape shape(Map map) {
        new Shape(map)
    }

    /**
     * Create a new Shape.
     * <p><code>def s = shape("#ff0000", 8, "circle", 0.55, 0)</code></p>
     * @param color The color
     * @param size The size
     * @param type The type
     * @param opacity The opacity (0-1)
     * @param angle The angle or rotation (0-360)
     * @return A Shape
     */
    static Shape shape(def color, def size = 6, def type = "circle", def opacity = 1.0, def angle = 0) {
        new Shape(color, size, type, opacity, angle)
    }

    /**
     * Create a new Transform from a Function.
     * <p><code>Transform t = transform(new Function("myCentroid", {g -> g.centroid}))</code></p>
     * @param function The geoscript.filter.Function
     * @return A Transform
     */
    static Transform transform(Function function) {
        new Transform(function)
    }

    /**
     * Create a new Transform from a CQL filter function.
     * <p><code>Transform t = transform("centroid(the_geom)")</code></p>
     * @param cql A CQL string
     * @return A Transform
     */
    static Transform transform(String cql) {
        new Transform(cql)
    }
}

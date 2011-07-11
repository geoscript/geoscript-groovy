package geoscript.style

import org.geotools.styling.Fill as GtFill
import org.geotools.styling.Rule
import org.geotools.styling.PolygonSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer for area/polygonal geometries.  It consists of a color and an opacity.
 * You can create a Fill from a color and opacity:
 * <p><code>def fill = new Fill('#ff0000', 0.5)</code></p>
 * Or from named parameters:
 * <p><code>def fill = new Fill(color: '#ff0000', opacity: 0.25)</code></p>
 * @author Jared Erickson
 */
class Fill extends Symbolizer {

    /**
     * The Color (#ff0000, red, [0,0.,255])
     */
    String color

    /**
     * The opacity (1.0 = opaque to 0.0 = transparent)
     */
    double opacity

    /**
     * The Icon
     */
    Icon icon

    /**
     * The Hatch
     */
    Hatch hatch

    /**
     * Create a new Fill.
     * <p><code>def f = new Fill(color: '#ff0000', opacity: 0.25)</code></p>
     * @param map A Map of named parameters.
     */
    Fill(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Create a new Fill.
     * <p><code>def f = new Fill('#ff0000', 0.5)</code></p>
     * <p><code>def f = new Fill('red', 0.5)</code></p>
     * <p><code>def f = new Fill([255,0,0], 0.5)</code></p>
     * @param color The Color
     * @param opacity The opacity (1 opaque to 0 transparent)
     */
    Fill(def color, double opacity = 1.0) {
        super()
        this.color = ColorUtil.toHex(color)
        this.opacity = opacity
        this.icon = null
        this.hatch = null
    }

    /**
     * Compose this Fill as an Icon.
     * <p><code>def f = new Fill().icon('icon.png','image/png')</code></p>
     * @param url The URL or File of the image
     * @param format The mime type of the image.
     * @return This Fill
     */
    Fill icon(def url, String format) {
        this.icon = new Icon(url, format)
        this
    }

    /**
     * Compose this Fill with a Hatch pattern.
     * <code>def f = new Fill().hatch('slash')</code>
     * @param name
     * @param stroke
     * @param size
     * @return
     */
    Fill hatch(String name, Stroke stroke = null, double size = 8) {
        this.hatch = new Hatch(name, stroke, size)
        this
    }

    /**
     * Set the color
     * @param color  The color (#ffffff, red)
     */
    void setColor(def color) {
        this.color = ColorUtil.toHex(color)
    }

    /**
     * Create the GeoTools Fill object
     * @return A GeoTools Fill
     */
    protected GtFill createFill() {
        GtFill fill = styleBuilder.createFill()
        if (color) {
            fill.color = filterFactory.literal(color)
        } else {
            fill.color = null
        }
        if (hatch) {
            fill.graphicFill = hatch.createHatch()
        }
        fill.opacity = filterFactory.literal(opacity)
        fill
    }

    /**
     * Prepare the GeoTools Rule by applying this Symbolizer
     * @param rule The GeoTools Rule
     */
    @Override
    protected void prepare(Rule rule) {
        super.prepare(rule)
        getGeoToolsSymbolizers(rule, PolygonSymbolizer).each{s ->
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
        sym.fill = createFill()
        if (icon) {
            icon.apply(sym)
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Fill", ['color': color, 'opacity': opacity])
    }
}


package geoscript.style

import geoscript.filter.Property
import geoscript.filter.Expression
import geoscript.filter.Function
import org.geotools.styling.Rule
import org.geotools.styling.TextSymbolizer
import org.geotools.styling.Symbolizer as GtSymbolizer

/**
 * A Symbolizer for labeling a geometry.
 * <p>You can create a Label with a Field or a Field name:</p>
 * <p><blockquote><pre>
 * def label = new Label("STATE_ABBR")
 * </pre></blockquote></p>
 * Or with named parameters:
 * <p><blockquote><pre>
 * def label = new Label(property: "name", font: new Font(weight: "bold")))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Label extends Symbolizer {
    
    /**
     * The property can be a Field, String, or Function
     */
    Expression property

    /**
     * The Font
     */
    Font font

    /**
     * The Halo
     */
    Halo halo
    
    /**
     * The Fill
     */
    Fill fill

    /**
     * The priority
     */
    Expression priority

    /**
     * The Point or Line Placement
     */
    def placement

    /**
     * The Shape
     */
    Shape shape

    /**
     * Create a new Label with a property which is a field or attribute with which
     * to generate labels form.
     * <p><blockquote><pre>
     * def label = new Label("STATE_ABBR")
     * </pre></blockquote></p>
     * @param property The field or attribute
     */
    Label(def property) {
        super()
        setProperty(property)
    }

    /**
     * Create a new Label with named parameters.
     * <p><blockquote><pre>
     * def label = new Label(property: "name", font: new Font(weight: "bold")))
     * </pre></blockquote></p>
     * @param map A Map of named parameters.
     */
    Label(Map map) {
        super()
        map.each{k,v->
            if(this.hasProperty(k)){
                this."$k" = v
            }
        }
    }

    /**
     * Set the Label's Property which can be a String, a Field, or a Function
     * @param property The Label's Property
     */
    void setProperty(def property) {
        if (property instanceof Property || property instanceof Function || property instanceof Expression) {
            this.property = property
        } else {
            this.property = new Property(property)
        }
    }

    /**
     * Set the Font
     * @param font A GeoScript Font or an object convertable to a Font
     * @return This Label
     */
    Label font(def font) {
        this.font = font instanceof Font ? font : new Font(font)
        this
    }

    /**
     * Add a Halo to this Label
     * @param fill The Fill
     * @param radius The radius
     * @return This Label
     */
    Label halo(Fill fill, def radius) {
        this.halo = new Halo(fill, radius)
        this
    }
    
    /**
     * Set how the Label will be filled.
     * @param fill The Fill
     * @return This Label
     */
    Label fill(Fill fill) {
        this.fill = fill
        this
    }

    /**
     * Set the Shape surrounding the Label
     * @param shape The Shape
     * @return This Label
     */
    Label shape(Shape shape) {
        this.shape = shape
        this
    }

    /**
     * Set the Point Placement for this Label
     * @param anchor The anchor
     * @param displace The displacement
     * @param rotate The rotation
     * @return This Label
     */
    Label point(List anchor = [0.5,0.5], List displace = [0,0], def rotate = 0){
        def anchorPoint = styleFactory.createAnchorPoint(new Expression(anchor[0]).expr, new Expression(anchor[1]).expr)
        def displacement = styleFactory.createDisplacement(new Expression(displace[0]).expr, new Expression(displace[1]).expr)
        this.placement = styleFactory.createPointPlacement(anchorPoint, displacement, new Expression(rotate).expr)
        this
    }

    /**
     * Set the Point Placement for this Label
     * @param parameters A Map of named parameters (anchor, displace, and rotate)
     * @return This Label
     */
    Label point(Map parameters) {
        List anchor = parameters.get("anchor", [0.5,0.5])
        List displace = parameters.get("displace", [0,0])
        double rotate = parameters.get("rotate", 0)
        return this.point(anchor, displace, rotate)
    }

    /**
     * Set the Line Placement for this Label
     * @param offset The perpendicular offset
     * @param gap The gap
     * @param igap The initial gap
     * @param align Whether to align or not
     * @param follow Whether to follow lines or not (true or false)
     * @param group Whether to group or not
     * @param displacement The maximum displacement distance
     * @param repeat The repeat distance
     * @return This Label
     */
    Label linear(def offset = 0, def gap = null, def igap = null, boolean align = false, boolean follow = false,
        boolean group = false, def displacement = null, def repeat = null) {
        placement = styleFactory.createLinePlacement(new Expression(offset).expr)
        placement.aligned = align
        if (gap) placement.gap = new Expression(gap).expr
        if (igap) placement.initialGap = new Expression(igap).expr
        options.followLine = String.valueOf(follow)
        options.group = String.valueOf(group)
        if (displacement) options.maxDisplacement = String.valueOf(displacement)
        if (repeat) options.repeat = String.valueOf(repeat)
        this
    }

    /**
     * Set the Line Placement for this Label
     * @param parameters A Map of named parameters (offset, gap, igap, align, follow,
     * group, displacement, repeat)
     * @return This label
     */
    Label linear(Map parameters) {
        double offset = parameters.get("offset", 0)
        def gap = parameters.get("gap", null)
        def igap = parameters.get("igap", null)
        boolean align = parameters.get("align", false)
        boolean follow = parameters.get("follow", false)
        boolean group = parameters.get("group", false)
        def displacement = parameters.get("displacement", null)
        def repeat = parameters.get("repeat", null)
        return this.linear(offset, gap, igap, align, follow, group, displacement, repeat)
    }

    /**
     * Set the priority
     * @param priority The priority
     * @return This Label
     */
    Label priority(def priority) {
        this.priority = new Expression(priority)
        this
    }

    /**
     * Set the max displacement
     * @param maxDisplacement  The max displacement
     * @return This Label
     */
    Label maxDisplacement(double maxDisplacement) {
        options.maxDisplacement = String.valueOf(maxDisplacement)
        this
    }

    /**
     * Set the auto wrap length.  Labels longer than this value
     * will be wrapped.
     * @param length The auto wrap length
     * @return This Label
     */
    Label autoWrap(int length) {
        options.autoWrap = String.valueOf(length)
        this
    }

    /**
     * Set the space around a Label.  A negative number will allow
     * labels to overlap.  A positive number will create more space
     * between labels
     * @param distance A negative or positive distance between labels
     * @return This label
     */
    Label spaceAround(float distance) {
        options.spaceAround = String.valueOf(distance)
        this
    }

    /**
     * Make all line segments are labeled instead of just the longest
     * @param bool Whether to label all or just the longest
     * @return This label
     */
    Label labelAllGroup(boolean bool) {
        options.labelAllGroup = String.valueOf(bool)
        this
    }

    /**
     * Disable label flipping making labels always follow natural
     * orientation of the line or not.
     * @param bool Whether to disable label flipping
     * @return This Label
     */
    Label forceLeftToRight(boolean bool) {
        options.forceLeftToRight = String.valueOf(bool)
        this
    }

    /**
     * Disable label conflict resolution or not.
     * @param bool Whether disable conflict resolution or not
     * @return This Label
     */
    Label conflictResolution(boolean bool) {
        options.conflictResolution = String.valueOf(bool)
        this
    }

    /**
     * Set the max angle delta
     * @param maxAngleDelta The max angle delta
     * @return This Label
     */
    Label maxAngleDelta(float maxAngleDelta) {
        options.maxAngleDelta = String.valueOf(maxAngleDelta)
        this
    }

    /**
     * Set the goodness of fit parameter (a number between 0 and 1)
     * @param goodness The goodness of fit parameter (a number between 0 and 1)
     * @return This Label
     */
    Label goodnessOfFit(float goodness) {
        options.goodnessOfFit = String.valueOf(goodness)
        this
    }

    /**
     * Set the polygon align option (manual, ortho, mbr)
     * @param value The polygon align option (manual, ortho, mbr)
     * @return This Label
     */
    Label polygonAlign(String value) {
        options.polygonAlign = value
        this
    }

    /**
     * Set the graphic resize mode (none, proportional, stretch) and margin.
     * @param mode The graphic resize mode
     * @param mode The graphic resize mode
     * @return This Label
     */
    Label graphicResize(String mode, int margin = 0) {
        options['graphic-resize'] = mode
        options['graphic-margin'] = String.valueOf(margin)
        this
    }
    
    /**
     * Set whether to use the Label as an obstacle or not
     * @param bol Whether to use the Label as an obstacle 
     * @return This Label
     */
    Label labelObstacle(boolean bol) {
        options.labelObstacle = String.valueOf(bol)
        this
    }

    /**
     * Set whether the Label should underline the text or not
     * @param shouldUnderline Whether the label text should be underlined
     * @return This label
     */
    Label underline(boolean shouldUnderline) {
        options.underlineText = String.valueOf(shouldUnderline)
        this
    }

    /**
     * Set the spacing between words
     * @param spacing The spacing
     * @return This Label
     */
    Label wordSpacing(int spacing) {
        options.wordSpacing = String.valueOf(spacing)
        this
    }

    /**
     * Set the spacing between characters
     * @param spacing The spacing
     * @return This Label
     */
    Label characterSpacing(int spacing) {
        options.charSpacing = String.valueOf(spacing)
        this
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
        sym.label = property.expr
        if (font) font.apply(sym)
        if (halo) halo.apply(sym)
        if (fill) fill.apply(sym)
        if (shape) shape.apply(sym)
        if (placement) sym.labelPlacement = placement
        if (priority != null && priority.value != null) {
            sym.priority = priority.expr
        }
    }

    /**
     * The string representation
     * @return The string representation
     */
    String toString() {
        buildString("Label", ['property': property])
    }
}


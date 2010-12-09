package geoscript.style

import geoscript.feature.Field
import org.geotools.styling.SLD

/**
 * The TextSymbolizer.
 * <p>You can create a TextSymbolizer using a map like syntax:</p>
 * <p><code><pre>
 * def sym = new PolygonSymbolizer(
 *      label: "name",
 *      haloColor: "#FFFFFF",
 *      haloRadius: 3
 * )
 * </pre></code></p>
 * <p>Or you can create a TextSymbolizer using properties:</p>
 * <p><code><pre>
 * def sym = new TextSymbolizer()
 * sym.label = "name"
 * sym.haloColor = "#FFFFFF"
 * sym.haloRadius = 3
 * </pre></code></p>
 * @author Jared Erickson
 */
class TextSymbolizer  extends Symbolizer {

    /**
     * Create a new TextSymbolizer from a GeoTools TextSymbolizer
     * @param symbolizer The GeoTools TextSymbolizer
     */
    TextSymbolizer(org.geotools.styling.TextSymbolizer symbolizer) {
        super(symbolizer)
    }

    /**
     * Create a new TextSymbolizer
     */
    TextSymbolizer() {
        super(Style.builder.createTextSymbolizer())
    }

    /**
     * Set the label
     * @param lbl The label by name or with a Function
     */
    void setLabel(def lbl) {
        if (lbl instanceof Field) {
            symbolizer.label = Style.filterFactory.property(lbl.name)
        } else if (lbl instanceof geoscript.filter.Function) {
            symbolizer.label = lbl.function
        } else {
            symbolizer.label = Style.filterFactory.property(lbl)
        }
    }
  
    /**
     * Get the label name
     * @return The label
     */
    def getLabel() {
        def lbl = symbolizer.label
        if (lbl instanceof org.opengis.filter.expression.PropertyName) {
            return lbl.propertyName
        } else if (lbl instanceof org.opengis.filter.expression.Literal) {
            return lbl.value
        } else if (lbl instanceof org.opengis.filter.expression.Function) {
            return new geoscript.filter.Function(lbl)
        } else {
            return lbl
        }
    }

    /**
     * Set the Color
     * @param color The Color
     */
    void setColor(String color) {
        symbolizer.fill.color = Style.filterFactory.literal(Style.getHexColor(color))
    }

    /**
     * Get the color
     * @return The color
     */
    String getColor() {
        symbolizer?.fill?.color
    }

    /**
     * Set the name of the Font Family
     * @param name The name of the Font Family
     */
    void setFontFamily(String name) {
        SLD.font(symbolizer).setFontFamily(Style.filterFactory.literal(name))
    }

    /**
     * Get the font family
     * @return The font family
     */
    String getFontFamily() {
        SLD.font(symbolizer)?.fontFamily
    }

    /**
     * Set the font size
     * @param size The font size
     */
    void setFontSize(int size) {
        SLD.font(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    /**
     * Get the font size
     * @return The font size
     */
    int getFontSize() {
        SLD.font(symbolizer)?.size.value as int
    }

    /**
     * Set the font style (normal, italic, oblique)
     * @param style The font style (normal, italic, oblique)
     */
    void setFontStyle(String style) {
        SLD.font(symbolizer).setStyle(Style.filterFactory.literal(style))
    }

    /**
     * Get the font style
     * @return The font styles
     */
    String getFontStyle() {
        SLD.font(symbolizer)?.style
    }

    /**
     * Set the font weight (bold,normal)
     * @param weight The font weight (bold, normal)
     */
    void setFontWeight(String weight) {
        SLD.font(symbolizer).setWeight(Style.filterFactory.literal(weight))
    }

    /**
     * Get the font weight
     * @return The font weight
     */
    String getFontWeight() {
        SLD.font(symbolizer)?.weight
    }

    /**
     * Create a Halo but only if necessary
     */
    private void createHaloIfNecessary() {
        if (symbolizer.halo == null) {
            symbolizer.halo = Style.builder.createHalo()
        }
    }

    /**
     * Set the halo color
     * @param color The halo color
     */
    void setHaloColor(String color) {
        createHaloIfNecessary()
        symbolizer.halo.fill.color = Style.filterFactory.literal(Style.getHexColor(color))
    }

    /**
     * Get the halo color
     * @return The halo color
     */
    String getHaloColor() {
        symbolizer?.halo?.fill?.color
    }

    /**
     * Set the halo radius
     * @param radius The halo radius
     */
    void setHaloRadius(float radius) {
        createHaloIfNecessary()
        symbolizer.halo.radius = Style.filterFactory.literal(radius)
    }

    /**
     * Get the halo radius
     * @return The halo radius
     */
    float getHaloRadius() {
        symbolizer?.halo?.radius?.value as float
    }

    /**
     * Create a PointPlacement for the labelPlacement property but
     * only if necessary
     */
    private void createPointPlacementIfNecessary() {
        if (symbolizer.labelPlacement == null ||
            !(symbolizer.labelPlacement instanceof org.geotools.styling.PointPlacement)) {
            symbolizer.labelPlacement = Style.builder.createPointPlacement()
        }
    }

    /**
     * Set the anchor point x
     * @param x The anchor point x
     */
    void setAnchorPointX(float x) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.anchorPoint.anchorPointX = Style.filterFactory.literal(x)
    }

    /**
     * Get the anchor point x
     * @return The anchor point x
     */
    float getAnchorPointX() {
        symbolizer?.labelPlacement?.anchorPoint?.anchorPointX.value as float
    }

    /**
     * Set the anchor point y
     * @param x The anchor point y
     */
    void setAnchorPointY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.anchorPoint.anchorPointY = Style.filterFactory.literal(y)
    }

    /**
     * Get the anchor point y
     * @return The anchor point y
     */
    float getAnchorPointY() {
        symbolizer?.labelPlacement?.anchorPoint?.anchorPointY.value as float
    }

    /**
     * Set the displacement x
     * @param x The displacement x
     */
    void setDisplacementX(float x) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.displacement.displacementX = Style.filterFactory.literal(x)
    }

    /**
     * Get the displacement X
     * @return The displacement X
     */
    float getDisplacementX() {
        symbolizer?.labelPlacement?.displacement?.displacementX.value as float
    }

    /**
     * Set the displacement y
     * @param y The displacement y
     */
    void setDisplacementY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.displacement.displacementY = Style.filterFactory.literal(y)
    }

    /**
     * Get the displacement y
     * @return The diplacement y
     */
    float getDisplacementY() {
        symbolizer?.labelPlacement?.displacement?.displacementY.value as float
    }

    /**
     * Set the rotation
     * @param rotation The rotation
     */
    void setRotation(float rotation) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.rotation = Style.filterFactory.literal(rotation)
    }

    /**
     * Get the rotation
     * @return The rotation
     */
    float getRotation() {
        symbolizer?.labelPlacement?.rotation?.value as float
    }

    /**
     * Create a LinePlacement for the labelPlacement property but only
     * if necessary
     */
    private void createLinePlacementIfNecessary() {
        if (symbolizer.labelPlacement == null ||
            !(symbolizer.labelPlacement instanceof org.geotools.styling.LinePlacement)) {
            symbolizer.labelPlacement = Style.builder.createLinePlacement(0)
        }
    }

    /**
     * Set the follow line property
     * @param followLine Whether to follow lines or not
     */
    void setFollowLine(boolean followLine) {
        createLinePlacementIfNecessary()
        symbolizer.options.put("followLine", String.valueOf(followLine))
    }

    /**
     * Get the follow line property
     * @return The follow line property
     */
    boolean getFollowLine() {
        symbolizer.options["followLine"] ?: false
    }

    /**
     * Set the perpendicular offset
     * @param offset The perpendicular offset
     */
    void setPerpendicularOffset(float offset) {
        createLinePlacementIfNecessary()
        symbolizer.labelPlacement.perpendicularOffset = Style.filterFactory.literal(offset)
    }

    /**
     * Get the perpendicular offset
     * @return The perpendicular offset
     */
    float getPerpendicularOffset() {
        symbolizer?.labelPlacement?.perpendicularOffset ?: 0
    }

    /**
     * Set the auto wrap length
     * @param length The auto wrap length
     */
    void setAutoWrap(float length) {
        symbolizer.options.put("autoWrap", String.valueOf(length))
    }

    /**
     * Get the auto wrap length
     * @return The auto wrap length
     */
    float getAutoWrap() {
        symbolizer.options["autoWrap"] as float ?: 0
    }

    /**
     * Set the maximum displacement distance
     * @param distance The maximum displacement distance
     */
    void setMaxDisplacement(float distance) {
        symbolizer.options.put("maxDisplacement", String.valueOf(distance))
    }

    /**
     * Get the max diplacement distance
     * @return The max diplacement distance
     */
    float getMaxDisplacement() {
        symbolizer.options["maxDisplacement"] as float ?: 0
    }

    /**
     * Set the max angle delta
     * @param maxAngleDelta The max angle delta
     */
    void setMaxAngleDelta(float maxAngleDelta) {
        symbolizer.options.put("maxAngleDelta", String.valueOf(maxAngleDelta))
    }

    /**
     * Get the max angle delta
     * @return The max angle delta
     */
    float getMaxAngleDelta() {
        symbolizer.options["maxAngleDelta"] as float ?: 0
    }

    /**
     * Set the repeat distance
     * @param repeat The repeat distance
     */
    void setRepeat(float repeat) {
        symbolizer.options.put("repeat", String.valueOf(repeat))
    }

    /**
     * Get the repeat distance
     * @return The repeat distance
     */
    float getRepeat() {
        symbolizer.options["repeat"] as float ?: 0
    }
    
    /**
     * Set the goodness of fit parameter (0 to 1)
     * @param goodness A value between 0 and 1
     */
    void setGoodnessOfFit(float goodness) {
        symbolizer.options.put("goodnessOfFit", String.valueOf(goodness))
    }
    
    /** 
     * Get the goodness of fit parameter
     * @return The goodness of fit parameter
     */
    float getGoodnessOfFit() {
        symbolizer.options["goodnessOfFit"] as float ?: 0
    }
    
    /**
     * Set the polygon align option (mbr)
     * @param value The option value (mbr)
     */
    void setPolygonAlign(String value) {
        symbolizer.options.put("polygonAlign", value)
    }

    /**
     * Get the polygon align option
     * @return The option value or null
     */
    String getPolygonAlign() {
        symbolizer.options["polygonAlign"]
    }

}


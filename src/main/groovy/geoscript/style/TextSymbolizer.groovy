package geoscript.style

import geoscript.feature.Field
import org.geotools.styling.SLD

/**
 * The TextSymbolizer
 * @author Jared Erickson
 */
class TextSymbolizer  extends Symbolizer {

    /**
     * Create a new TextSymbolizer
     */
    TextSymbolizer() {
        super(Style.builder.createTextSymbolizer())
    }

    /**
     * Set the Field
     * @param field The Field
     */
    void setField(Field field) {
        symbolizer.label = Style.filterFactory.property(field.name)
    }

    /**
     * Set the Color
     * @param color The Color
     */
    void setColor(String color) {
        symbolizer.fill.color = Style.filterFactory.literal(color)
    }

    /**
     * Set the name of the Font Family
     * @param name The name of the Font Family
     */
    void setFontFamily(String name) {
        SLD.font(symbolizer).setFontFamily(Style.filterFactory.literal(name))
    }

    /**
     * Set the font size
     * @param size The font size
     */
    void setFontSize(int size) {
        SLD.font(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    /**
     * Set the font style
     * @param style The font style
     */
    void setFontStyle(String style) {
        SLD.font(symbolizer).setStyle(Style.filterFactory.literal(style))
    }

    /**
     * Set the font weight (bold,normal)
     * @param weight The font weight (bold, normal)
     */
    void setFontWeight(String weight) {
        SLD.font(symbolizer).setWeight(Style.filterFactory.literal(weight))
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
        symbolizer.halo.fill.color = Style.filterFactory.literal(color)
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
     * Set the anchor point y
     * @param x The anchor point y
     */
    void setAnchorPointY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.anchorPoint.anchorPointY = Style.filterFactory.literal(y)
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
     * Set the displacement y
     * @param y The displacement y
     */
    void setDisplacementY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.displacement.displacementY = Style.filterFactory.literal(y)
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
     * Set the perpendicular offset
     * @param offset The perpendicular offset
     */
    void setPerpendicularOffset(float offset) {
        createLinePlacementIfNecessary()
        symbolizer.labelPlacement.perpendicularOffset = Style.filterFactory.literal(offset)
    }

    /**
     * Set the auto wrap length
     * @param length The auto wrap length
     */
    void setAutoWrap(float length) {
        symbolizer.options.put("autoWrap", String.valueOf(length))
    }

    /**
     * Set the maximum displacement distance
     * @param distance The maximum displacement distance
     */
    void setMaxDisplacement(float distance) {
        symbolizer.options.put("maxDisplacement", String.valueOf(distance))
    }

    /**
     * Set the max angle delta
     * @param maxAngleDelta The max angle delta
     */
    void setMaxAngleDelta(float maxAngleDelta) {
        symbolizer.options.put("maxAngleDelta", String.valueOf(maxAngleDelta))
    }

    /**
     * Set the repeat distance
     * @param repeat The repeat distance
     */
    void setRepeat(float repeat) {
        symbolizer.options.put("repeat", String.valueOf(repeat))
    }
}


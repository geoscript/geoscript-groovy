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

    void setField(Field field) {
        symbolizer.label = Style.filterFactory.property(field.name)
    }

    void setColor(String color) {
        symbolizer.fill.color = Style.filterFactory.literal(color)
    }

    void setFontFamily(String name) {
        SLD.font(symbolizer).setFontFamily(Style.filterFactory.literal(name))
    }

    void setFontSize(int size) {
        SLD.font(symbolizer).setSize(Style.filterFactory.literal(size))
    }

    void setFontStyle(String style) {
        SLD.font(symbolizer).setStyle(Style.filterFactory.literal(style))
    }

    void setFontWeight(String weight) {
        SLD.font(symbolizer).setWeight(Style.filterFactory.literal(weight))
    }

    private void createHaloIfNecessary() {
        if (symbolizer.halo == null) {
            symbolizer.halo = Style.builder.createHalo()
        }
    }

    void setHaloColor(String color) {
        createHaloIfNecessary()
        symbolizer.halo.fill.color = Style.filterFactory.literal(color)
    }

    void setHaloRadius(float radius) {
        createHaloIfNecessary()
        symbolizer.halo.radius = Style.filterFactory.literal(radius)
    }

    private void createPointPlacementIfNecessary() {
        if (symbolizer.labelPlacement == null ||
            !(symbolizer.labelPlacement instanceof org.geotools.styling.PointPlacement)) {
            symbolizer.labelPlacement = Style.builder.createPointPlacement()
        }
    }

    void setAnchorPointX(float x) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.anchorPoint.anchorPointX = Style.filterFactory.literal(x)
    }

    void setAnchorPointY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.anchorPoint.anchorPointY = Style.filterFactory.literal(y)
    }

    void setDisplacementX(float x) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.displacement.displacementX = Style.filterFactory.literal(x)
    }

    void setDisplacementY(float y) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.displacement.displacementY = Style.filterFactory.literal(y)
    }

    void setRotation(float rotation) {
        createPointPlacementIfNecessary()
        symbolizer.labelPlacement.rotation = Style.filterFactory.literal(rotation)
    }

    private void createLinePlacementIfNecessary() {
        if (symbolizer.labelPlacement == null ||
            !(symbolizer.labelPlacement instanceof org.geotools.styling.LinePlacement)) {
            symbolizer.labelPlacement = Style.builder.createLinePlacement(0)
        }
    }

    void setFollowLine(boolean followLine) {
        createLinePlacementIfNecessary()
        symbolizer.options.put("followLine", String.valueOf(followLine))
    }

    void setPerpendicularOffset(float offset) {
        createLinePlacementIfNecessary()
        symbolizer.labelPlacement.perpendicularOffset = Style.filterFactory.literal(offset)
    }

    void setAutoWrap(float length) {
        symbolizer.options.put("autoWrap", String.valueOf(length))
    }

    void setMaxDisplacement(float distance) {
        symbolizer.options.put("maxDisplacement", String.valueOf(distance))
    }

    void setMaxAngleDelta(float maxAngleDelta) {
        symbolizer.options.put("maxAngleDelta", String.valueOf(maxAngleDelta))
    }

    void setRepeat(float repeat) {
        symbolizer.options.put("repeat", String.valueOf(repeat))
    }
}


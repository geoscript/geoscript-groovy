package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Property
import geoscript.filter.Expression

/**
 * The Label Unit Test
 */
class LabelTestCase {

    @Test void construct() {

        // Create a Label
        Label label = new Label("name")
        assertEquals "name", label.property.value
        assertEquals "Label(property = name)", label.toString()

        // Add a Font
        assertTrue label.font(new Font()) instanceof Label
        assertEquals "normal", label.font.style.value
        assertEquals "normal", label.font.weight.value
        assertEquals 10, label.font.size.value
        assertEquals "serif", label.font.family.value

        // Add a Halo
        assertTrue label.halo(new Fill("#999999"), 2.5) instanceof Label
        assertEquals "#999999", label.halo.fill.color.value
        assertEquals 2.5, label.halo.radius.value, 0.1

        // Add point placement
        assertTrue label.point([0.75,0.25], [5,2], 0.25) instanceof Label
        assertTrue label.placement instanceof org.geotools.styling.PointPlacement
        assertEquals 0.75, label.placement.anchorPoint.anchorPointX.value, 0.1
        assertEquals 0.25, label.placement.anchorPoint.anchorPointY.value, 0.1
        assertEquals 5, label.placement.displacement.displacementX.value, 0.1
        assertEquals 2, label.placement.displacement.displacementY.value, 0.1
        assertEquals 0.25, label.placement.rotation.value, 0.1

        // Switch to line placement
        assertTrue label.linear() instanceof Label
        assertTrue label.placement instanceof org.geotools.styling.LinePlacement
        assertEquals 0, label.placement.perpendicularOffset.value, 0.1
        assertNull label.placement.gap
        assertNull label.placement.initialGap
        assertFalse label.placement.aligned
        assertEquals "false", label.options.followLine
        assertEquals "false", label.options.group
        assertFalse label.options.containsKey("maxDisplacement")
        assertFalse label.options.containsKey("repeat")

        // Switch back to point placement using named parameters
        assertTrue label.point(displace: [1,2], anchor: [1,0], rotate: 45) instanceof Label
        assertTrue label.placement instanceof org.geotools.styling.PointPlacement
        assertEquals 1, label.placement.anchorPoint.anchorPointX.value, 0.1
        assertEquals 0, label.placement.anchorPoint.anchorPointY.value, 0.1
        assertEquals 1, label.placement.displacement.displacementX.value, 0.1
        assertEquals 2, label.placement.displacement.displacementY.value, 0.1
        assertEquals 45, label.placement.rotation.value, 0.1

        // Switch to line placement using named parameters
        assertTrue label.linear(follow: true, repeat: 15, offset: 2) instanceof Label
        assertTrue label.placement instanceof org.geotools.styling.LinePlacement
        assertEquals 2, label.placement.perpendicularOffset.value, 0.1
        assertNull label.placement.gap
        assertNull label.placement.initialGap
        assertFalse label.placement.aligned
        assertEquals "true", label.options.followLine
        assertEquals "false", label.options.group
        assertFalse label.options.containsKey("maxDisplacement")
        assertEquals 15, label.options.repeat as double, 0.1

        // Switch to a more complicate line placement
        assertTrue label.linear(3.4, 1.2, 1, true, true, true, 1.4, 5.1) instanceof Label
        assertTrue label.placement instanceof org.geotools.styling.LinePlacement
        assertEquals 3.4, label.placement.perpendicularOffset.value, 0.1
        assertEquals 1.2, label.placement.gap.value, 0.1
        assertEquals 1, label.placement.initialGap.value, 0.1
        assertTrue label.placement.aligned
        assertEquals "true", label.options.followLine
        assertEquals "true", label.options.group
        assertEquals 1.4, label.options.maxDisplacement as double, 0.1
        assertEquals 5.1, label.options.repeat as double, 0.1

        // Autowrap
        assertTrue label.autoWrap(10) instanceof Label
        assertEquals 10, label.options.autoWrap as int

        // Max angle delta
        assertTrue label.maxAngleDelta(15.5) instanceof Label
        assertEquals 15.5, label.options.maxAngleDelta as double, 0.1

        // Goodness of fit
        assertTrue label.goodnessOfFit(0.95) instanceof Label
        assertEquals 0.95, label.options.goodnessOfFit as double, 0.1

        // Polygon Align
        assertTrue label.polygonAlign("mbr") instanceof Label
        assertEquals "mbr", label.options.polygonAlign

        // Priority
        assertTrue label.priority("POP_SIZE") instanceof Label
        assertEquals label.priority.value, "POP_SIZE"

        // Named parameters
        label = new Label(property: "name", font: new Font(size:16))
        assertEquals "name", label.property.value
        assertEquals "Label(property = name)", label.toString()
        assertEquals "normal", label.font.style.value
        assertEquals "normal", label.font.weight.value
        assertEquals 16, label.font.size.value
        assertEquals "serif", label.font.family.value
    }

    @Test void constructorWithExpressions() {

        Label label = new Label(new Property("NAME"))
        assertTrue label.property instanceof Property
        assertTrue label.property.expr instanceof org.opengis.filter.expression.PropertyName
        assertEquals "NAME", label.property.value

        label = new Label(new Expression("&#x2192;"))
        assertTrue label.property instanceof Expression
        assertTrue label.property.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "&#x2192;", label.property.value

        label = new Label("NAME")
        assertTrue label.property instanceof Expression
        assertTrue label.property.expr instanceof org.opengis.filter.expression.PropertyName
        assertEquals "NAME", label.property.value
    }

    @Test void apply() {
        def text = Symbolizer.styleFactory.createTextSymbolizer()
        Label label = new Label("name")
        label.apply(text)
        assertEquals "name", text.label.propertyName
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())
        Label label = new Label("name")
        label.prepare(rule)
        def text= rule.symbolizers()[0]
        assertEquals "name", text.label.propertyName
    }

}

package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Color
import geoscript.filter.Expression

/**
 * The Shape Unit Test
 * @author Jared Erickson
 */
class ShapeTestCase {

    @Test void constructors() {

        Shape shape = new Shape("#999999")
        assertEquals "#999999", shape.color.value
        assertEquals 6, shape.size.value, 0.1
        assertEquals "circle", shape.type.value
        assertEquals "Shape(color = #999999, size = 6, type = circle)", shape.toString()

        def mark = shape.createMark()
        assertEquals "#999999", mark.fill.color.value
        assertEquals "circle", mark.wellKnownName.value

        // Create a Shape from Expressions
        shape = new Shape(new Color("#999999"), new Expression(6), new Expression("circle"))
        assertEquals "#999999", shape.color.value
        assertEquals 6, shape.size.value as double, 0.1
        assertEquals "circle", shape.type.value
        assertEquals "Shape(color = #999999, size = 6, type = circle)", shape.toString()

        mark = shape.createMark()
        assertEquals "#999999", mark.fill.color.value
        assertEquals "circle", mark.wellKnownName.value

        shape = new Shape([255,105,43], 5.5, "square")
        assertEquals "#ff692b", shape.color.value
        assertEquals 5.5, shape.size.value, 0.1
        assertEquals "square", shape.type.value
        assertEquals "Shape(color = #ff692b, size = 5.5, type = square)", shape.toString()

        mark = shape.createMark()
        assertEquals "#ff692b", mark.fill.color.value
        assertEquals "square", mark.wellKnownName.value

        shape = new Shape("#999999").stroke("yellow")
        assertEquals "#999999", shape.color.value
        assertEquals 6, shape.size.value, 0.1
        assertEquals "circle", shape.type.value
        assertEquals "#ffff00", shape.stroke.color.value
        assertEquals "Shape(color = #999999, size = 6, type = circle)", shape.toString()

        mark = shape.createMark()
        assertEquals "#999999", mark.fill.color.value
        assertEquals "circle", mark.wellKnownName.value
        assertEquals "#ffff00", mark.stroke.color.value

        // Named parameters
        shape = new Shape(type: "star", color: "blue", opacity: 0.5)
        assertEquals "#0000ff", shape.color.value
        assertEquals 6, shape.size.value, 0.1
        assertEquals "star", shape.type.value
        assertEquals 0.5, shape.opacity.value, 0.1
        assertEquals "Shape(color = #0000ff, size = 6, type = star)", shape.toString()
    }

    @Test void apply() {
        def sym = Symbolizer.styleFactory.createPointSymbolizer()
        Shape shape = new Shape("#999999")
        shape.apply(sym)
        assertNotNull sym.graphic
        assertEquals 6.0, sym.graphic.size.value, 0.1
        assertEquals "#999999", sym.graphic.graphicalSymbols()[0].fill.color.value
        assertEquals "circle", sym.graphic.graphicalSymbols()[0].wellKnownName.value
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPointSymbolizer())
        Shape shape = new Shape("#999999")
        shape.prepare(rule)
        def sym = rule.symbolizers[0]
        assertNotNull sym.graphic
        assertEquals 6.0, sym.graphic.size.value, 0.1
        assertEquals "#999999", sym.graphic.graphicalSymbols()[0].fill.color.value
        assertEquals "circle", sym.graphic.graphicalSymbols()[0].wellKnownName.value
    }

    @Test void createGraphic() {
        Stroke stroke = new Stroke(null, 0, [4,6])
        Shape shape = new Shape("#666666", 4, "circle").stroke("#333333",1)
        stroke.shape(shape)
        //assertNull line.stroke.graphicStroke
        //shape.apply(line)
        //assertNotNull line.stroke.graphicStroke
        //stroke.asSLD()
    }

    @Test void anchorPointAndDisplacement() {
        Shape shape = new Shape(color:  "blue",  size: 6, type: "square", anchorPoint: [0.2, 0.7], displacement: [0.45, 0.55])
        assertEquals "#0000ff", shape.color.value
        assertEquals 6, shape.size.value, 0.1
        assertEquals "square", shape.type.value
        assertEquals shape.anchorPoint.anchorPointX.value, 0.2, 0.1
        assertEquals shape.anchorPoint.anchorPointY.value, 0.7, 0.1
        assertEquals shape.displacement.displacementX.value, 0.45, 0.1
        assertEquals shape.displacement.displacementY.value, 0.55, 0.1
        def sym = Symbolizer.styleFactory.createPointSymbolizer()
        shape.apply(sym)
        assertEquals sym.graphic.anchorPoint.anchorPointX.value, 0.2, 0.1
        assertEquals sym.graphic.anchorPoint.anchorPointY.value, 0.7, 0.1
        assertEquals sym.graphic.displacement.displacementX.value, 0.45, 0.1
        assertEquals sym.graphic.displacement.displacementY.value, 0.55, 0.1
    }
}

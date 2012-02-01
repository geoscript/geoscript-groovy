package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.*

/**
 * The Transform Unit Test
 * @author Jared Erickson
 */
class TransformTestCase {

    @Test void constructors() {

        Transform transform = new Transform("centroid(the_geom)")
        assertNotNull transform.function
        assertEquals "Transform(function = centroid([the_geom]))", transform.toString()

        Transform transform1 = new Transform(new Function("myCentroid(the_geom)", {g -> g.centroid}))
        assertNotNull transform1.function
        assertEquals "Transform(function = myCentroid([the_geom]))", transform1.toString()

        Transform transform3 = new Transform(new Function("centroid(the_geom)"))
        assertNotNull transform3.function
        assertEquals "Transform(function = centroid([the_geom]))", transform3.toString()
    }

    @Test void appy() {
        def pointSym = Symbolizer.styleFactory.createPointSymbolizer();
        Transform centroidTransform = new Transform(new Function("myCentroid(the_geom)", {g -> g.centroid}))
        centroidTransform.apply(pointSym)
        assertTrue pointSym.geometry instanceof org.opengis.filter.expression.Function

        def textSym = Symbolizer.styleFactory.createTextSymbolizer()
        Transform upperCaseTransform = new Transform(new Function("myUpperCase(NAME)", {str -> str.toUpperCase()}))
        upperCaseTransform.apply(textSym)
        assertTrue textSym.label instanceof org.opengis.filter.expression.Function
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPointSymbolizer())
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())

        Transform centroidTransform = new Transform(new Function("myCentroid(the_geom)", {g -> g.centroid}))
        Transform upperCaseTransform = new Transform(new Function("myUpperCase(NAME)", {str -> str.toUpperCase()}))
        centroidTransform.prepare(rule)
        upperCaseTransform.prepare(rule)

        assertTrue rule.symbolizers()[0].geometry instanceof org.opengis.filter.expression.Function
        assertTrue rule.symbolizers()[1].label instanceof org.opengis.filter.expression.Function
    }

}

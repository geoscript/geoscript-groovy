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

        Transform transform1 = new Transform(new Function("myCentroid", {g -> g.centroid}))
        assertNotNull transform1.function
        assertEquals "Transform(function = myCentroid())", transform1.toString()

        Transform transform3 = new Transform(new Function("centroid(the_geom)"))
        assertNotNull transform3.function
        assertEquals "Transform(function = centroid([the_geom]))", transform3.toString()
    }

    @Test void appy() {
        def pointSym = Symbolizer.styleFactory.createPointSymbolizer();
        Transform centroidTransform = new Transform(new Function("myCentroid", {g -> g.centroid}))
        centroidTransform.apply(pointSym)
        assertTrue pointSym.geometry instanceof org.geotools.filter.FunctionImpl

        def textSym = Symbolizer.styleFactory.createTextSymbolizer()
        Transform upperCaseTransform = new Transform(new Function("myUpperCase", {str -> str.toUpperCase()}))
        upperCaseTransform.apply(textSym)
        assertTrue textSym.label instanceof org.geotools.filter.FunctionImpl
    }

    @Test void prepare() {
        def rule = Symbolizer.styleFactory.createRule()
        rule.symbolizers().add(Symbolizer.styleFactory.createPointSymbolizer())
        rule.symbolizers().add(Symbolizer.styleFactory.createTextSymbolizer())

        Transform centroidTransform = new Transform(new Function("myCentroid", {g -> g.centroid}))
        Transform upperCaseTransform = new Transform(new Function("myUpperCase", {str -> str.toUpperCase()}))
        centroidTransform.prepare(rule)
        upperCaseTransform.prepare(rule)

        assertTrue rule.symbolizers()[0].geometry instanceof org.geotools.filter.FunctionImpl
        assertTrue rule.symbolizers()[1].label instanceof org.geotools.filter.FunctionImpl
    }

}

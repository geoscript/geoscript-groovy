package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.*
import geoscript.process.Process
import geoscript.geom.GeometryCollection
import geoscript.layer.Layer

/**
 * The Transform Unit Test
 * @author Jared Erickson
 */
class TransformTestCase {

    @Test void constructors() {

        Transform transform = new Transform("centroid(the_geom)")
        assertNotNull transform.function
        assertEquals "Transform(function = centroid([the_geom]))", transform.toString()
        assertEquals transform.type, Transform.Type.NORMAL

        Transform transform1 = new Transform(new Function("myCentroid(the_geom)", {g -> g.centroid}))
        assertNotNull transform1.function
        assertEquals "Transform(function = myCentroid([the_geom]))", transform1.toString()
        assertEquals transform1.type, Transform.Type.NORMAL

        Transform transform3 = new Transform(new Function("centroid(the_geom)"))
        assertNotNull transform3.function
        assertEquals "Transform(function = centroid([the_geom]))", transform3.toString()
        assertEquals transform3.type, Transform.Type.NORMAL

        Process p = new Process("convexhull",
                "Create a convexhull around the features",
                [features: geoscript.layer.Cursor],
                [result: geoscript.layer.Cursor],
                { inputs ->
                    def geoms = new GeometryCollection(inputs.features.collect{f -> f.geom})
                    def output = new Layer()
                    output.add([geoms.convexHull])
                    [result: output]
                }
        )
        Function f = new Function(p, new Function("parameter", new Expression("features")))
        Transform transform4 = new Transform(f, Transform.RENDERING)
        assertEquals transform4.type, Transform.Type.RENDERING
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

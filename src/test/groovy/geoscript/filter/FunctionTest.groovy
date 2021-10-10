package geoscript.filter

import geoscript.feature.Feature
import geoscript.geom.Point
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Shape
import geoscript.style.Transform
import geoscript.style.Label
import geoscript.style.Font
import geoscript.process.Process
import geoscript.geom.GeometryCollection
import geoscript.layer.Layer

/**
 * The Function UnitTest
 * @author Jared Erickson
 */
class FunctionTest {

    @TempDir
    private File folder

    @Test void createFromGeoToolsFunction() {
        def f = new Function(Function.ff.function("centroid", Function.ff.property("the_geom")))
        assertNotNull f
        assertNotNull f.function
        assertEquals "centroid", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "the_geom", f.function.parameters[0].toString()
        assertEquals "centroid([the_geom])", f.toString()
    }

    @Test void createFromCQL() {
        def f = new Function("centroid(the_geom)")
        assertNotNull f
        assertNotNull f.function
        assertEquals "centroid", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "the_geom", f.function.parameters[0].toString()
        assertEquals "centroid([the_geom])", f.toString()
    }

    @Test void createFromNameAndExpressions() {
        def f = new Function("centroid", new Property("the_geom"))
        assertNotNull f
        assertNotNull f.function
        assertEquals "centroid", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "the_geom", f.function.parameters[0].toString()
        assertEquals "centroid([the_geom])", f.toString()
    }

    @Test void createFromNameClosureAndExpressions() {
        def f = new Function("my_centroid", {g-> g.centroid}, new Property("the_geom"))
        assertNotNull f
        assertNotNull f.function
        assertEquals "my_centroid", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "the_geom", f.function.parameters[0].toString()
        assertEquals "my_centroid([the_geom])", f.toString()
    }

    @Test void createFromCqlAndClosure() {
        def f = new Function("my_centroid(the_geom)", {g-> g.centroid})
        assertNotNull f
        assertNotNull f.function
        assertEquals "my_centroid", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "the_geom", f.function.parameters[0].toString()
        assertEquals "my_centroid([the_geom])", f.toString()
    }

    @Test void evalulate() {
        // Geometry
        Function func = new Function("centroid(the_geom)")
        Feature feature = new Feature([the_geom: new Point(100,100).buffer(2)],"poly1")
        def value = func.evaluate(feature)
        assertTrue value instanceof Point
        assertEquals 100, (value as Point).x, 0.1
        assertEquals 100, (value as Point).y, 0.1
        // String
        func = new Function("strToLowerCase(NAME)")
        feature = new Feature([NAME:"Test"],"test1")
        value = func.evaluate(feature)
        assertEquals "test", value
    }

    @Test void registerFunction() {
        Function.registerFunction("lcase", {str -> str.toLowerCase()})
        def f = new Function("lcase(STATE_ABBR)")
        assertNotNull f
        assertNotNull f.function
        assertEquals "lcase", f.function.name
        assertEquals 1, f.function.parameters.size()
        assertEquals "STATE_ABBR", f.function.parameters[0].toString()
        assertEquals "lcase([STATE_ABBR])", f.toString()
    }

    @Test void rendering() {

        // Register custom Functions
        Function.registerFunction("my_centroid", {g -> g.centroid})
        Function.registerFunction("lcase", {str -> str.toLowerCase()})

        File imgFile = new File(folder, "states_function.png")
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        def statesShp = new Shapefile(file)
        statesShp.style = (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)) +
                (new Shape("#66CCff", 6, "circle").stroke("#004080") + new Transform("my_centroid(the_geom)")).zindex(1) +
                (new Label("STATE_ABBR").font(new Font("normal", "bold", 10, "serif")).fill(new Fill("#004080")) + new Transform("lcase(STATE_ABBR)")).zindex(2)

        def map = new geoscript.render.Map(width: 600, height: 400, fixAspectRatio: true)
        map.proj = "EPSG:4326"
        map.addLayer(statesShp)
        map.bounds = statesShp.bounds
        map.render(imgFile)
    }
    
    @Test void processFunction() {
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

        File imgFile = new File(folder, "states_function.png")
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        def statesShp = new Shapefile(file)

        def sym = (new Stroke("red",0.4) + new Transform(f, Transform.RENDERING)).zindex(1) + (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)).zindex(2)
        assertTrue sym.sld.contains("<ogc:Function name=\"geoscript:convexhull\">")
        statesShp.style = sym

        def map = new geoscript.render.Map(width: 600, height: 400, fixAspectRatio: true)
        map.proj = "EPSG:4326"
        map.addLayer(statesShp)
        map.bounds = statesShp.bounds
        map.render(imgFile)
    }

    @Test void getFunctionNames() {
        List names = Function.functionNames
        assertTrue names.size() > 0
        assertTrue names.contains("Area")
        assertTrue names.contains("cos")
        assertTrue names.contains("within")
    }
}

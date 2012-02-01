package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Shape
import geoscript.style.Transform
import geoscript.style.Label
import geoscript.style.Font

/**
 * The Function UnitTest
 * @author Jared Erickson
 */
class FunctionTestCase {

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

        File imgFile = File.createTempFile("states_function", ".png")
        println "Rendering map with Functions: ${imgFile}"
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
}

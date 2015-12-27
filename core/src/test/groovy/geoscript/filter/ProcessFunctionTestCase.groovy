package geoscript.filter

import geoscript.geom.GeometryCollection
import geoscript.layer.Layer
import geoscript.process.Process
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.style.Transform
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertTrue

/**
 * The ProcessFunction Unit Test.
 * @author Jared Erickson
 */
class ProcessFunctionTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void function() {
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

        File imgFile = folder.newFile("states_function.png")
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer layer = w.get("states")

        def sym = (new Stroke("red",0.4) + new Transform(f, Transform.RENDERING)).zindex(1) + (new Fill("#E6E6E6") + new Stroke("#4C4C4C",0.5)).zindex(2)
        assertTrue sym.sld.contains("<ogc:Function name=\"geoscript:convexhull\">")
        layer.style = sym

        def map = new geoscript.render.Map(width: 600, height: 400, fixAspectRatio: true)
        map.proj = "EPSG:4326"
        map.addLayer(layer)
        map.bounds = layer.bounds
        map.render(imgFile)
        assertTrue imgFile.length() > 0
    }
}

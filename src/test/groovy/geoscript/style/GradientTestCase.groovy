package geoscript.style

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

import geoscript.filter.Expression
import geoscript.render.Map
import geoscript.layer.Shapefile

/**
 * The Gradient UnitTest
 * @author Jared Erickson
 */
class GradientTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void gradientBetweenStyles() {

        Gradient gradient = new Gradient(
            new Expression("PERSONS / LAND_KM"),
            [0,200],
            [new Fill("#000066") + new Stroke("black",0.1), new Fill("red") + new Stroke("black",0.1)],
            10,
            "exponential"
        )
        assertEquals 11, gradient.parts.size()

        Composite composite = gradient + ((new Fill("red")  + new Stroke("black",0.1)).where("PERSONS / LAND_KM > 200"))

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        shp.style = composite

        Map map = new Map()
        map.addLayer(shp)

        File imgFile = folder.newFile("states.png")
        println imgFile
        map.render(imgFile)
    }

    @Test void gradientWithColorBrewerForPoints() {
        Schema s = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", s)
        layer.add(new Feature([new Point(111,-47), "House 1", 12.5], "house1", s))
        layer.add(new Feature([new Point(112,-46), "House 2", 13.5], "house2", s))
        layer.add(new Feature([new Point(113,-45), "House 3", 14.5], "house3", s))

        Gradient gradient = new Gradient(layer, "price", "Quantile", 3, "Greens")
        layer.style = gradient

        Map map = new Map()
        map.addLayer(layer)

        File imgFile = folder.newFile("facilities.png")
        println imgFile
        map.render(imgFile)
    }

    @Test void gradientForLayerAndField() {

        // Get states shapefile
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)

        Map map = new Map()
        map.addLayer(shapefile)

        Gradient sym1 = new Gradient(shapefile, "WORKERS", "Quantile", 5, "Greens")
        assertNotNull(sym1)
        assertEquals(5, sym1.parts.size())

        File imgFile1 = folder.newFile("states.png")
        println imgFile1
        shapefile.style = sym1
        map.render(imgFile1)

        Gradient sym2 = new Gradient(shapefile, "WORKERS", "EqualInterval", 7, ["#eee8aa","#98fb98","#afeeee","#d87093","#ffefd5","#ffdab9","#cd853f"])
        assertNotNull(sym2)
        assertEquals(7, sym2.parts.size())

        File imgFile2 = folder.newFile("states.png")
        println imgFile2
        shapefile.style = sym2
        map.render(imgFile2)
    }

}

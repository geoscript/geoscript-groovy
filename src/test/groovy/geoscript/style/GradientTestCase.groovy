package geoscript.style

import org.junit.Test
import static org.junit.Assert.*
import geoscript.filter.Color
import geoscript.filter.Expression
import geoscript.map.Map
import geoscript.layer.Shapefile

/**
 * The Gradient UnitTest
 * @author Jared Erickson
 */
class GradientTestCase {

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

        File imgFile = File.createTempFile("states",".png")
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

        File imgFile1 = File.createTempFile("states",".png")
        println imgFile1
        shapefile.style = sym1
        map.render(imgFile1)

        Gradient sym2 = new Gradient(shapefile, "WORKERS", "EqualInterval", 7, ["#eee8aa","#98fb98","#afeeee","#d87093","#ffefd5","#ffdab9","#cd853f"])
        assertNotNull(sym2)
        assertEquals(7, sym2.parts.size())

        File imgFile2 = File.createTempFile("states",".png")
        println imgFile2
        shapefile.style = sym2
        map.render(imgFile2)
    }

}

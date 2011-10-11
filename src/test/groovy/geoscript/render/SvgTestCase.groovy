package geoscript.render

import geoscript.layer.Layer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Test
import static org.junit.Assert.assertNotNull

/**
 * The Svg UnitTest
 * @author Jared Erickson
 */
class SvgTestCase {

    @Test void renderToDocument() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Svg svg = new Svg()
        def doc = svg.render(map)
        assertNotNull(doc)
    }

    @Test void renderToOutputStream() {
        File shpFile = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Layer layer = new Shapefile(shpFile)
        layer.style = new Stroke('black', 0.1) + new Fill('gray', 0.75)
        Map map = new Map(layers: [layer])
        Svg svg = new Svg()
        File file = File.createTempFile("map_",".svg")
        println file
        OutputStream out = new FileOutputStream(file)
        svg.render(map, out)
        out.close()
    }
}

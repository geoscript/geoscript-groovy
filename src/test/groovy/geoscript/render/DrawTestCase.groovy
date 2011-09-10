package geoscript.render

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*
import geoscript.layer.Shapefile
import geoscript.style.*


/**
 * The Draw UnitTest
 * @author Jared Erickson
 */
class DrawTestCase {

    @Test void drawGeometry() {
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Geometry geom = new Point(0,0).buffer(0.2)
        Draw.draw([geom], sym, geom.bounds, [250,250], "png")
    }

    @Test void drawLayer() {
        // Get states shapefile
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shapefile = new Shapefile(file)
        Symbolizer sym = new Stroke('black', 2) + new Fill('gray',0.75)
        Draw.draw(shapefile, sym, shapefile.bounds, [250,250], "png")
    }

}

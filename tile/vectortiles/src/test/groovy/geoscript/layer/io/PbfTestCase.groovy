package geoscript.layer.io

import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Pyramid
import geoscript.layer.Shapefile
import geoscript.layer.Tile
import org.junit.Test

import static org.junit.Assert.assertTrue

/**
 * The Pbf Unit Test
 * @author Jared Erickson
 */
class PbfTestCase {

    @Test void writeRead() {
        URL url = getClass().getClassLoader().getResource("states.shp")
        Layer layer = new Shapefile(new File(url.toURI()))

        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(5, 5, 20))

        byte[] bytes = Pbf.write([layer], bounds)
        assertTrue bytes.length > 0

        Layer pbfLayer = Pbf.read(bytes, bounds)
        assertTrue pbfLayer.count > 0
    }

}

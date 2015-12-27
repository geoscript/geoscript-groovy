package geoscript.layer

import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.layer.io.Pbf
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The PbfVectorTileRenderer Unit Test
 * @author Jared Erickson
 */
class PbfVectorTileRendererTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void render() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(5, 5, 20))
        PbfVectorTileRenderer renderer = new PbfVectorTileRenderer(shp, [shp.schema.get("STATE_NAME")])
        byte[] data = renderer.render(bounds)
        assertNotNull data
        assertTrue data.length > 0
        List layers = Pbf.read(data, bounds)
        assertEquals 1, layers.size()
        assertEquals 9, layers[0].count
        layers.each { Layer layer ->
            layer.eachFeature { Feature f ->
                assertNotNull f.geom
            }
        }
    }

}

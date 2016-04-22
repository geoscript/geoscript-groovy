package geoscript.layer

import geoscript.layer.io.MvtWriter
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull

/**
 * The GeneratingTileLayer unit test
 * @author Jared Erickson
 */
class GeneratingTileLayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private void generateOnDemand(TileLayer tileLayer, TileRenderer tileRenderer) {
        GeneratingTileLayer generatingTileLayer = new GeneratingTileLayer(tileLayer, tileRenderer)
        [
                [z: 0, x: 0, y: 0],
                [z: 1, x: 0, y: 1],
                [z: 9, x: 8, y: 10]
        ].each { Map tile ->
            assertNull tileLayer.get(tile.z, tile.x, tile.y).data
            assertNotNull generatingTileLayer.get(tile.z, tile.x, tile.y).data
            assertNotNull tileLayer.get(tile.z, tile.x, tile.y).data
        }
        generatingTileLayer.close()
    }

    @Test void generateMvt() {
        File dir = folder.newFolder("states")
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        TileLayer tileLayer = new VectorTiles("states", dir, pyramid, "mvt")
        geoscript.layer.io.Writer writer = new MvtWriter()
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [layer.schema.get("STATE_NAME")]
        TileRenderer tileRenderer = new VectorTileRenderer(writer, layer, fields)
        generateOnDemand(tileLayer, tileRenderer)
    }

}

package geoscript.layer

import geoscript.FileUtil
import geoscript.layer.io.MvtWriter
import geoscript.style.Fill
import geoscript.style.Stroke
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The GeneratingTileLayer unit test
 * @author Jared Erickson
 */
class GeneratingTileLayerTest {

    @TempDir
    private File folder

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

    @Test void generateMbtiles() {
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        layer.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = new File(folder, "states.mbtiles")
        TileLayer tileLayer = new MBTiles(file, "states", "A map of the united states")
        ImageTileRenderer tileRenderer = new ImageTileRenderer(tileLayer, layer)
        generateOnDemand(tileLayer, tileRenderer)
    }

    @Test void generateGeoPackage() {
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        layer.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = new File(folder, "states.gpkg")
        TileLayer tileLayer = new GeoPackage(file, "states", Pyramid.createGlobalMercatorPyramid())
        ImageTileRenderer tileRenderer = new ImageTileRenderer(tileLayer, layer)
        generateOnDemand(tileLayer, tileRenderer)
    }

    @Test void generateMvt() {
        File dir = FileUtil.createDir(folder, "states")
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        TileLayer tileLayer = new VectorTiles("states", dir, pyramid, "mvt")
        geoscript.layer.io.Writer writer = new MvtWriter()
        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        List fields = [layer.schema.get("STATE_NAME")]
        TileRenderer tileRenderer = new VectorTileRenderer(writer, layer, fields)
        generateOnDemand(tileLayer, tileRenderer)
    }

}

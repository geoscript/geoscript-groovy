package geoscript.layer

import geoscript.layer.io.GeoJSONWriter
import geoscript.proj.Projection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The VectorTiles Unit Test
 * @author Jared Erickson
 */
class VectorTilesTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void generate() {
        // Setup
        File dir = folder.newFolder("states")
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        VectorTiles vectorTiles = new VectorTiles(
                "States",
                dir,
                pyramid,
                "json",
                proj: new Projection("EPSG:4326")
        )
        // Generate
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        VectorTileRenderer renderer = new VectorTileRenderer(new GeoJSONWriter(), shp, [shp.schema.get("STATE_NAME")])
        TileGenerator generator = new TileGenerator()
        generator.generate(vectorTiles, renderer, 0, 2)
        // Check tiles
        [
                new File(dir, "0/0/0.json"),
                new File(dir, "1/0/0.json"),
                new File(dir, "1/1/0.json"),
                new File(dir, "1/1/1.json"),
                new File(dir, "1/0/1.json"),
                new File(dir, "2/0/0.json"),
                new File(dir, "2/0/1.json"),
                new File(dir, "2/0/2.json"),
                new File(dir, "2/0/3.json"),
                new File(dir, "2/1/0.json"),
                new File(dir, "2/1/1.json"),
                new File(dir, "2/1/2.json"),
                new File(dir, "2/1/3.json"),
                new File(dir, "2/2/0.json"),
                new File(dir, "2/2/1.json"),
                new File(dir, "2/2/2.json"),
                new File(dir, "2/2/3.json"),
                new File(dir, "2/3/0.json"),
                new File(dir, "2/3/1.json"),
                new File(dir, "2/3/2.json"),
                new File(dir, "2/3/3.json")
        ].each { File f ->
            assertTrue f.exists()
        }
        // Read
        (0..2).each { int z ->
            vectorTiles.tiles(z).each { Tile t ->
                assertEquals z, t.z
                assertTrue t.x in [0l, 1l, 2l, 3l]
                assertTrue t.y in [0l, 1l, 2l, 3l]
                assertNotNull t.data
            }
        }
        // Get Layers
        List layers = vectorTiles.getLayers(vectorTiles.tiles(2, 0, 1, 1, 1))
        assertEquals 1, layers.size()
        assertTrue layers[0].count > 0
    }

}

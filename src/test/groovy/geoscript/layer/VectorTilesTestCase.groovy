package geoscript.layer

import com.xebialabs.restito.server.StubServer
import geoscript.layer.io.GeoJSONWriter
import geoscript.proj.Projection
import org.glassfish.grizzly.http.Method
import org.glassfish.grizzly.http.util.HttpStatus
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.resourceContent
import static com.xebialabs.restito.semantics.Action.status
import static com.xebialabs.restito.semantics.Condition.get
import static com.xebialabs.restito.semantics.Condition.method
import static com.xebialabs.restito.semantics.Condition.uri
import static org.junit.Assert.*

/**
 * The VectorTiles Unit Test
 * @author Jared Erickson
 */
class VectorTilesTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    protected StubServer server

    @Before
    void start() {
        server = new StubServer(8888).run()
    }

    @After
    void stop() {
        server.stop()
        // Hack for Windows, which isn't shutting dow the server
        // fast enough
        try {
            Thread.sleep(200)
        } catch (InterruptedException e) {
            e.printStackTrace()
        }
    }

    @Test
    void getGeoJsonTilesFromUrl() {
        whenHttp(server).match(get("/1/0/0.json")).then(resourceContent("tile.json"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/0.json")).then(resourceContent("tile.json"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/0/1.json")).then(resourceContent("tile.json"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/1.json")).then(resourceContent("tile.json"), status(HttpStatus.OK_200))


        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        VectorTiles vectorTiles = new VectorTiles(
                "States",
                new URL("http://00.0.0.0:8888"),
                pyramid,
                "json",
                proj: new Projection("EPSG:4326")
        )
        List<Layer> layers = vectorTiles.getLayers(new TileCursor<Tile>(vectorTiles, 1))
        assertEquals(1, layers.size())

        verifyHttp(server).once(method(Method.GET), uri("/1/0/0.json"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/0.json"))
        verifyHttp(server).once(method(Method.GET), uri("/1/0/1.json"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/1.json"))
    }

    @Test
    void getPbfTilesFromUrl() {
        whenHttp(server).match(get("/1/0/0.pbf")).then(resourceContent("pbf/1/0/0.pbf"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/0.pbf")).then(resourceContent("pbf/1/1/0.pbf"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/0/1.pbf")).then(resourceContent("pbf/1/0/1.pbf"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/1.pbf")).then(resourceContent("pbf/1/1/1.pbf"), status(HttpStatus.OK_200))

        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        VectorTiles vectorTiles = new VectorTiles(
                "World",
                new URL("http://00.0.0.0:8888"),
                pyramid,
                "pbf"
        )
        List<Layer> layers = vectorTiles.getLayers(new TileCursor<Tile>(vectorTiles, 1))
        assertEquals(2, layers.size())

        verifyHttp(server).once(method(Method.GET), uri("/1/0/0.pbf"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/0.pbf"))
        verifyHttp(server).once(method(Method.GET), uri("/1/0/1.pbf"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/1.pbf"))
    }

    @Test
    void generateDirectoryOfPbfTiles() {
        // Setup
        File dir = folder.newFolder("states")
        dir.mkdir()
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        VectorTiles vectorTiles = new VectorTiles(
                "States",
                dir,
                pyramid,
                "pbf",
                proj: new Projection("EPSG:4326")
        )
        // Generate
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        PbfVectorTileRenderer renderer = new PbfVectorTileRenderer(shp, [shp.schema.get("STATE_NAME")])
        TileGenerator generator = new TileGenerator()
        generator.generate(vectorTiles, renderer, 0, 2)
        // Check tiles
        [
                new File(dir, "0/0/0.pbf"),
                new File(dir, "1/0/0.pbf"),
                new File(dir, "1/1/0.pbf"),
                new File(dir, "1/1/1.pbf"),
                new File(dir, "1/0/1.pbf"),
                new File(dir, "2/0/0.pbf"),
                new File(dir, "2/0/1.pbf"),
                new File(dir, "2/0/2.pbf"),
                new File(dir, "2/0/3.pbf"),
                new File(dir, "2/1/0.pbf"),
                new File(dir, "2/1/1.pbf"),
                new File(dir, "2/1/2.pbf"),
                new File(dir, "2/1/3.pbf"),
                new File(dir, "2/2/0.pbf"),
                new File(dir, "2/2/1.pbf"),
                new File(dir, "2/2/2.pbf"),
                new File(dir, "2/2/3.pbf"),
                new File(dir, "2/3/0.pbf"),
                new File(dir, "2/3/1.pbf"),
                new File(dir, "2/3/2.pbf"),
                new File(dir, "2/3/3.pbf")
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
        // Delete
        Tile tile = vectorTiles.get(0,0,0)
        assertNotNull tile.data
        vectorTiles.delete(tile)
        tile = vectorTiles.get(0,0,0)
        assertNull tile.data
        // Get Layers
        List layers = vectorTiles.getLayers(vectorTiles.tiles(2, 0, 1, 1, 1))
        assertEquals 1, layers.size()
        assertTrue layers[0].count > 0
    }

    @Test
    void generateDirectoryOfGeoJsonTiles() {
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
        // Delete
        Tile tile = vectorTiles.get(0,0,0)
        assertNotNull tile.data
        vectorTiles.delete(tile)
        tile = vectorTiles.get(0,0,0)
        assertNull tile.data
        // Get Layers
        List layers = vectorTiles.getLayers(vectorTiles.tiles(2, 0, 1, 1, 1))
        assertEquals 1, layers.size()
        assertTrue layers[0].count > 0
    }

    @Test
    void generateMBTileFileOfPbfTiles() {
        // Setup
        File file = folder.newFile("states.mbtiles")
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        VectorTiles vectorTiles = new VectorTiles(
                "States",
                file,
                pyramid,
                "pbf"
        )
        // Generate
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        PbfVectorTileRenderer renderer = new PbfVectorTileRenderer(shp, [shp.schema.get("STATE_NAME")])
        TileGenerator generator = new TileGenerator()
        generator.generate(vectorTiles, renderer, 0, 2)
        // Read
        (0..2).each { int z ->
            vectorTiles.tiles(z).each { Tile t ->
                assertEquals z, t.z
                assertTrue t.x in [0l, 1l, 2l, 3l]
                assertTrue t.y in [0l, 1l, 2l, 3l]
                assertNotNull t.data
            }
        }
        // Delete
        Tile tile = vectorTiles.get(0,0,0)
        assertNotNull tile.data
        vectorTiles.delete(tile)
        tile = vectorTiles.get(0,0,0)
        assertNull tile.data
        // Get Layers
        List layers = vectorTiles.getLayers(vectorTiles.tiles(2, 0, 1, 1, 1))
        assertEquals 1, layers.size()
        assertTrue layers[0].count > 0
    }

}

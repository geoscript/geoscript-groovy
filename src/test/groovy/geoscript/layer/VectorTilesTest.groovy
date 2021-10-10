package geoscript.layer

import geoscript.FileUtil
import geoscript.ServerTestUtil
import geoscript.layer.io.GeoJSONWriter
import geoscript.proj.Projection
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The VectorTiles Unit Test
 * @author Jared Erickson
 */
class VectorTilesTest {

    @TempDir
    File folder

    @Test
    void getGeoJsonTilesFromUrl() {
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("tile.json"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("tile.json"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("tile.json"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("tile.json"))))
            server.start()
            HttpUrl url = server.url("")

            Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
            pyramid.origin = Pyramid.Origin.TOP_LEFT
            VectorTiles vectorTiles = new VectorTiles(
                    "States",
                    url.url(),
                    pyramid,
                    "json",
                    proj: new Projection("EPSG:4326")
            )
            List<Layer> layers = vectorTiles.getLayers(new TileCursor<Tile>(vectorTiles, 1))
            assertEquals(1, layers.size())

            assertEquals("/1/0/0.json", server.takeRequest().getPath())
            assertEquals("/1/1/0.json", server.takeRequest().getPath())
            assertEquals("/1/0/1.json", server.takeRequest().getPath())
            assertEquals("/1/1/1.json", server.takeRequest().getPath())
        }
    }

    @Test
    void getPbfTilesFromUrl() {
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("pbf/1/0/0.pbf"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("pbf/1/1/0.pbf"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("pbf/1/0/1.pbf"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("pbf/1/1/1.pbf"))))
            server.start()
            HttpUrl url = server.url("")

            Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
            pyramid.origin = Pyramid.Origin.TOP_LEFT
            VectorTiles vectorTiles = new VectorTiles(
                    "World",
                    url.url(),
                    pyramid,
                    "pbf"
            )
            List<Layer> layers = vectorTiles.getLayers(new TileCursor<Tile>(vectorTiles, 1))
            assertEquals(2, layers.size())

            assertEquals("/1/0/0.pbf", server.takeRequest().getPath())
            assertEquals("/1/1/0.pbf", server.takeRequest().getPath())
            assertEquals("/1/0/1.pbf", server.takeRequest().getPath())
            assertEquals("/1/1/1.pbf", server.takeRequest().getPath())
        }
    }

    @Test
    void generateDirectoryOfPbfTiles() {
        // Setup
        File dir = FileUtil.createDir(folder, "states")
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
        File dir = FileUtil.createDir(folder, "states")
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
        File file = new File(folder,"states.mbtiles")
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

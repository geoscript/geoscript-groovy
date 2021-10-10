package geoscript.layer

import geoscript.FileUtil
import geoscript.ServerTestUtil
import geoscript.style.Fill
import geoscript.style.Stroke
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

/**
 * The TMS Unit Test
 * @author Jared Erickson
 */
class TMSTest {

    @TempDir
    File folder

    @Test
    void generate() {
        // Generate
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = FileUtil.createDir(folder, "cache")
        TMS tms = new TMS("States", "png", file, Pyramid.createGlobalMercatorPyramid())
        TileRenderer renderer = new ImageTileRenderer(tms, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(tms, renderer, 0, 2)
        [
                new File(file, "0/0/0.png"),
                new File(file, "1/0/0.png"),
                new File(file, "1/1/0.png"),
                new File(file, "1/1/1.png"),
                new File(file, "1/0/1.png"),
                new File(file, "2/0/0.png"),
                new File(file, "2/0/1.png"),
                new File(file, "2/0/2.png"),
                new File(file, "2/0/3.png"),
                new File(file, "2/1/0.png"),
                new File(file, "2/1/1.png"),
                new File(file, "2/1/2.png"),
                new File(file, "2/1/3.png"),
                new File(file, "2/2/0.png"),
                new File(file, "2/2/1.png"),
                new File(file, "2/2/2.png"),
                new File(file, "2/2/3.png"),
                new File(file, "2/3/0.png"),
                new File(file, "2/3/1.png"),
                new File(file, "2/3/2.png"),
                new File(file, "2/3/3.png")
        ].each { File f ->
            assertTrue f.exists()
        }
        // Read
        (0..2).each { int z ->
            tms.tiles(z).each { Tile t ->
                assertEquals z, t.z
                assertTrue t.x in [0l, 1l, 2l, 3l]
                assertTrue t.y in [0l, 1l, 2l, 3l]
                assertNotNull t.data
            }
        }
    }

    @Test
    void getTile() {
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.start()
            HttpUrl url = server.url("")

            TMS tms = new TMS("World", "png", url.url(), Pyramid.createGlobalMercatorPyramid())
            Tile tile = tms.get(1, 2, 3)
            assertEquals 1, tile.z
            assertEquals 2, tile.x
            assertEquals 3, tile.y
            assertNotNull tile.data

            assertEquals("/1/2/3.png", server.takeRequest().getPath())
        }
    }



    @Test
    void getTiles() {
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.start()
            HttpUrl url = server.url("")

            TMS tms = new TMS("World", "png", url.url(), Pyramid.createGlobalMercatorPyramid())
            TileCursor c = tms.tiles(1)
            c.each { Tile t ->
                assertTrue t.z == 1
                assertTrue t.x in [0l, 1l]
                assertTrue t.y in [0l, 1l]
                assertNotNull t.data
            }

            assertEquals("/1/0/0.png", server.takeRequest().getPath())
            assertEquals("/1/1/0.png", server.takeRequest().getPath())
            assertEquals("/1/0/1.png", server.takeRequest().getPath())
            assertEquals("/1/1/1.png", server.takeRequest().getPath())
        }
    }

    @Test
    void delete() {
        File file = new File(getClass().getClassLoader().getResource("0.png").toURI())
        File dir =  FileUtil.createDir(folder, "tiles")
        File tileDir = new File(new File(dir,"0"),"0")
        tileDir.mkdirs()
        File tileFile = new File(tileDir, file.name)
        file.withInputStream { input ->
            tileFile.withOutputStream { out ->
                out << input
            }
        }
        TMS tms = new TMS("Tiles", "png", dir, Pyramid.createGlobalMercatorPyramid())
        Tile t = tms.get(0,0,0)
        assertNotNull t.data
        tms.delete(t)
        t = tms.get(0,0,0)
        assertNull t.data
    }

    @Test
    void deleteTiles() {
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = FileUtil.createDir(folder, "cache")
        TMS tms = new TMS("States", "png", file, Pyramid.createGlobalMercatorPyramid())
        TileRenderer renderer = new ImageTileRenderer(tms, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(tms, renderer, 0, 2)
        tms.tiles(1).each { Tile tile ->
            assertNotNull tile
            assertNotNull tile.data
        }
        tms.delete(tms.tiles(1))
        tms.tiles(1).each { Tile tile ->
            assertNotNull tile
            assertNull tile.data
        }
        tms.tiles(2).each { Tile tile ->
            assertNotNull tile
            assertNotNull tile.data
        }
        tms.close()
    }

}

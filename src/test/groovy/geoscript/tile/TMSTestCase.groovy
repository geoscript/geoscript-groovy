package geoscript.tile

import com.xebialabs.restito.server.StubServer
import geoscript.layer.Shapefile
import geoscript.style.Fill
import geoscript.style.Stroke
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
import static com.xebialabs.restito.semantics.Condition.*
import static org.junit.Assert.*

/**
 * The TMS Unit Test
 * @author Jared Erickson
 */
class TMSTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    protected StubServer server

    @Before
    public void start() {
        server = new StubServer(8888).run()
    }

    @After
    public void stop() {
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
    void generate() {
        // Generate
        Shapefile shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFolder("cache")
        TMS tms = new TMS("States", "png", file, Pyramid.createGlobalMercatorPyramid())
        TileGenerator generator = new TileGenerator()
        generator.generate(tms, shp, 0, 2)
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
        whenHttp(server).match(get("/1/2/3.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))

        TMS tms = new TMS("World", "png", "http://00.0.0.0:8888", Pyramid.createGlobalMercatorPyramid())
        Tile tile = tms.get(1, 2, 3)
        assertEquals 1, tile.z
        assertEquals 2, tile.x
        assertEquals 3, tile.y
        assertNotNull tile.data

        verifyHttp(server).once(method(Method.GET), uri("/1/2/3.png"))
    }

    @Test
    void getTiles() {
        whenHttp(server).match(get("/1/0/0.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/0/1.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/0.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))
        whenHttp(server).match(get("/1/1/1.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))

        TMS tms = new TMS("World", "png", "http://00.0.0.0:8888", Pyramid.createGlobalMercatorPyramid())
        TileCursor c = tms.tiles(1)
        c.each { Tile t ->
            assertTrue t.z == 1
            assertTrue t.x in [0l, 1l]
            assertTrue t.y in [0l, 1l]
            assertNotNull t.data
        }

        verifyHttp(server).once(method(Method.GET), uri("/1/0/0.png"))
        verifyHttp(server).once(method(Method.GET), uri("/1/0/1.png"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/0.png"))
        verifyHttp(server).once(method(Method.GET), uri("/1/1/1.png"))
    }

}

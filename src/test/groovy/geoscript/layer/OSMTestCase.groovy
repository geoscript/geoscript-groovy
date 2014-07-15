package geoscript.layer

import com.xebialabs.restito.server.StubServer
import org.glassfish.grizzly.http.Method
import org.glassfish.grizzly.http.util.HttpStatus
import org.junit.After
import org.junit.Before
import org.junit.Test

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import static com.xebialabs.restito.builder.verify.VerifyHttp.verifyHttp
import static com.xebialabs.restito.semantics.Action.resourceContent
import static com.xebialabs.restito.semantics.Action.status
import static com.xebialabs.restito.semantics.Condition.*
import static org.junit.Assert.*

/**
 * The OSM Unit Test
 * @author Jared Erickson
 */
class OSMTestCase {

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
    void getTile() {
        whenHttp(server).match(get("/1/2/3.png")).then(resourceContent("0.png"), status(HttpStatus.OK_200))

        OSM osm = new OSM("OSM", "http://00.0.0.0:8888")
        Tile tile = osm.get(1, 2, 3)
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

        OSM osm = new OSM("OSM", "http://00.0.0.0:8888")
        TileCursor c = osm.tiles(1)
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

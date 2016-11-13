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
    void getName() {
        OSM osm = new OSM()
        assertEquals "OSM", osm.name
    }

    @Test
    void getWellKnownOSM() {
        OSM osm = OSM.getWellKnownOSM("osm")
        assertEquals "OSM", osm.name
        osm = OSM.getWellKnownOSM("stamen-toner")
        assertEquals "Stamen Toner", osm.name
        osm = OSM.getWellKnownOSM("stamen-toner")
        assertEquals "Stamen Toner", osm.name
        osm = OSM.getWellKnownOSM("stamen-toner-lite")
        assertEquals "Stamen Toner Lite", osm.name
        osm = OSM.getWellKnownOSM("stamen-watercolor")
        assertEquals "Stamen Watercolor", osm.name
        osm = OSM.getWellKnownOSM("stamen-terrain")
        assertEquals "Stamen Terrain", osm.name
        osm = OSM.getWellKnownOSM("mapquest-street")
        assertEquals "MapQuest Street", osm.name
        osm = OSM.getWellKnownOSM("mapquest-satellite")
        assertEquals "MapQuest Satellite", osm.name
        assertNull OSM.getWellKnownOSM("N/A")
        assertNull OSM.getWellKnownOSM("")
        assertNull OSM.getWellKnownOSM(null)
    }

    @Test
    void getTileLayerFromMap() {
        OSM osm = TileLayer.getTileLayer([type: 'osm', name: 'osm'])
        assertEquals 'OSM', osm.name
        osm = TileLayer.getTileLayer([type: 'osm'])
        assertEquals 'OSM', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-toner'])
        assertEquals 'Stamen Toner', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-toner-lite'])
        assertEquals 'Stamen Toner Lite', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-watercolor'])
        assertEquals 'Stamen Watercolor', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'mapquest-satellite'])
        assertEquals 'MapQuest Satellite', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-terrain'])
        assertEquals 'Stamen Terrain', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'mapquest-street'])
        assertEquals 'MapQuest Street', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', url: 'http://a.tile.stamen.com/toner-lite'])
        assertEquals "OSM", osm.name
        osm = TileLayer.getTileLayer([type: 'osm', urls: 'http://a.tile.stamen.com/toner-lite,http://b.tile.stamen.com/toner-lite'])
        assertEquals "OSM", osm.name
    }

    @Test
    void getTileLayerFromString() {
        OSM osm = TileLayer.getTileLayer("type=osm name=osm")
        assertEquals 'OSM', osm.name
        osm = TileLayer.getTileLayer("type=osm")
        assertEquals 'OSM', osm.name
        osm = TileLayer.getTileLayer("osm")
        assertEquals 'OSM', osm.name
        osm = TileLayer.getTileLayer("type=osm name=stamen-toner")
        assertEquals 'Stamen Toner', osm.name
        osm = TileLayer.getTileLayer("stamen-toner")
        assertEquals 'Stamen Toner', osm.name
        osm = TileLayer.getTileLayer("type=osm name=stamen-toner-lite")
        assertEquals 'Stamen Toner Lite', osm.name
        osm = TileLayer.getTileLayer("stamen-toner-lite")
        assertEquals 'Stamen Toner Lite', osm.name
        osm = TileLayer.getTileLayer("type=osm name=stamen-watercolor")
        assertEquals 'Stamen Watercolor', osm.name
        osm = TileLayer.getTileLayer("stamen-watercolor")
        assertEquals 'Stamen Watercolor', osm.name
        osm = TileLayer.getTileLayer("type=osm name=stamen-terrain")
        assertEquals 'Stamen Terrain', osm.name
        osm = TileLayer.getTileLayer("type=osm name=mapquest-satellite")
        assertEquals 'MapQuest Satellite', osm.name
        osm = TileLayer.getTileLayer("mapquest-satellite")
        assertEquals 'MapQuest Satellite', osm.name
        osm = TileLayer.getTileLayer("type=osm name=mapquest-street")
        assertEquals 'MapQuest Street', osm.name
        osm = TileLayer.getTileLayer("mapquest-street")
        assertEquals 'MapQuest Street', osm.name
        osm = TileLayer.getTileLayer("type=osm url=http://a.tile.stamen.com/toner-lite")
        assertEquals "OSM", osm.name
        osm = TileLayer.getTileLayer("type=osm urls=http://a.tile.stamen.com/toner-lite,http://b.tile.stamen.com/toner-lite")
        assertEquals "OSM", osm.name
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
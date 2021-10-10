package geoscript.layer

import geoscript.ServerTestUtil
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The OSM Unit Test
 * @author Jared Erickson
 */
class OSMTest {

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
        osm = OSM.getWellKnownOSM("wikimedia")
        assertEquals "WikiMedia", osm.name
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
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-terrain'])
        assertEquals 'Stamen Terrain', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'mapquest-satellite'])
        assertEquals 'MapQuest Satellite', osm.name
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
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-watercolor'])
        assertEquals 'Stamen Watercolor', osm.name
        osm = TileLayer.getTileLayer([type: 'osm', name: 'stamen-terrain'])
        assertEquals 'Stamen Terrain', osm.name
        osm = TileLayer.getTileLayer("stamen-watercolor")
        assertEquals 'Stamen Watercolor', osm.name
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
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.start()
            HttpUrl url = server.url("")

            OSM osm = new OSM("OSM", url.url().toString())
            Tile tile = osm.get(1, 2, 3)
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

            OSM osm = new OSM("OSM", url.url().toString())
            TileCursor c = osm.tiles(1)
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

}

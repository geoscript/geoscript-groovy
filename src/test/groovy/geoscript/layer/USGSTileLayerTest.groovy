package geoscript.layer

import geoscript.ServerTestUtil
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The USGSTileLayer Unit Test
 * @author Jared Erickson
 */
class USGSTileLayerTest {

    @Test
    void getName() {
        USGSTileLayer usgs = USGSTileLayer.createImagery()
        assertEquals "USGSImagery", usgs.name
    }

    @Test
    void getWellKnown() {
        USGSTileLayer usgs = USGSTileLayer.getWellKnown("usgs-topo")
        assertEquals "USGSTopo", usgs.name
        usgs = USGSTileLayer.getWellKnown("usgs-shadedrelief")
        assertEquals "USGSShadedRelief", usgs.name
        usgs = USGSTileLayer.getWellKnown("usgs-imagery")
        assertEquals "USGSImagery", usgs.name
        usgs = USGSTileLayer.getWellKnown("usgs-imagerytopo")
        assertEquals "USGSImageryTopo", usgs.name
        usgs = USGSTileLayer.getWellKnown("usgs-hydro")
        assertEquals "USGSHydro", usgs.name
        assertNull USGSTileLayer.getWellKnown("N/A")
        assertNull USGSTileLayer.getWellKnown("")
        assertNull USGSTileLayer.getWellKnown(null)
    }

    @Test
    void getTileLayerFromMap() {
        USGSTileLayer usgs = TileLayer.getTileLayer([type: 'usgs', name: 'usgs-topo'])
        assertEquals 'USGSTopo', usgs.name
        usgs = TileLayer.getTileLayer([type: 'usgs', name: 'usgs-shadedrelief'])
        assertEquals 'USGSShadedRelief', usgs.name
        usgs = TileLayer.getTileLayer([type: 'usgs', name: 'usgs-imagery'])
        assertEquals 'USGSImagery', usgs.name
        usgs = TileLayer.getTileLayer([type: 'usgs', name: 'usgs-imagerytopo'])
        assertEquals 'USGSImageryTopo', usgs.name
        usgs = TileLayer.getTileLayer([type: 'usgs', name: 'usgs-hydro'])
        assertEquals 'USGSHydro', usgs.name
        usgs = TileLayer.getTileLayer([type: 'usgs', url: 'https://basemap.nationalmap.gov/arcgis/rest/services/USGSTopo/MapServer/tile'])
        assertEquals "USGS", usgs.name
    }

    @Test
    void getTileLayerFromString() {
        USGSTileLayer usgs = TileLayer.getTileLayer("type=usgs name=usgs-topo")
        assertEquals 'USGSTopo', usgs.name
        usgs = TileLayer.getTileLayer("type=usgs name=usgs-shadedrelief")
        assertEquals 'USGSShadedRelief', usgs.name
        usgs = TileLayer.getTileLayer("type=usgs name=usgs-imagery")
        assertEquals 'USGSImagery', usgs.name
        usgs = TileLayer.getTileLayer("type=usgs name=usgs-imagerytopo")
        assertEquals 'USGSImageryTopo', usgs.name
        usgs = TileLayer.getTileLayer("type=usgs name=usgs-hydro")
        assertEquals 'USGSHydro', usgs.name
        usgs = TileLayer.getTileLayer("type=usgs url=http://a.tile.stamen.com/toner-lite")
        assertEquals "USGS", usgs.name
    }

    @Test
    void getTile() {
        ServerTestUtil.withServer { MockWebServer server ->
            server.enqueue(new MockResponse().setBody(ServerTestUtil.fileToBytes(ServerTestUtil.getResource("0.png"))))
            server.start()
            HttpUrl url = server.url("")

            USGSTileLayer usgs = new USGSTileLayer("USGSTileLayer", url.url().toString())
            Tile tile = usgs.get(1, 2, 3)
            assertEquals 1, tile.z
            assertEquals 2, tile.x
            assertEquals 3, tile.y
            assertNotNull tile.data

            assertEquals("/1/3/2", server.takeRequest().getPath())
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

            USGSTileLayer usgs = new USGSTileLayer("USGSTileLayer", url.url().toString())
            TileCursor c = usgs.tiles(1)
            c.each { Tile t ->
                assertTrue t.z == 1
                assertTrue t.x in [0l, 1l]
                assertTrue t.y in [0l, 1l]
                assertNotNull t.data
            }
            assertEquals("/1/0/0", server.takeRequest().getPath())
            assertEquals("/1/0/1", server.takeRequest().getPath())
            assertEquals("/1/1/0", server.takeRequest().getPath())
            assertEquals("/1/1/1", server.takeRequest().getPath())
        }
    }

}

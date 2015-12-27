package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.*

class TileLayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void getTileLayerFromString() {
        // OSM with URL
        TileLayer tileLayer = TileLayer.getTileLayer("type=osm url=http://a.tile.openstreetmap.org")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof OSM)
        assertTrue((tileLayer as OSM).baseUrls.contains("http://a.tile.openstreetmap.org"))
        assertFalse((tileLayer as OSM).baseUrls.contains("http://b.tile.openstreetmap.org"))
        assertFalse((tileLayer as OSM).baseUrls.contains("http://c.tile.openstreetmap.org"))
        // OSM with URLs
        tileLayer = TileLayer.getTileLayer("type=osm urls=http://a.tile.openstreetmap.org,http://c.tile.openstreetmap.org")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof OSM)
        assertTrue((tileLayer as OSM).baseUrls.contains("http://a.tile.openstreetmap.org"))
        assertFalse((tileLayer as OSM).baseUrls.contains("http://b.tile.openstreetmap.org"))
        assertTrue((tileLayer as OSM).baseUrls.contains("http://c.tile.openstreetmap.org"))
        // OSM with no URLs
        tileLayer = TileLayer.getTileLayer("type=osm")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof OSM)
        assertTrue((tileLayer as OSM).baseUrls.contains("http://a.tile.openstreetmap.org"))
        assertTrue((tileLayer as OSM).baseUrls.contains("http://b.tile.openstreetmap.org"))
        assertTrue((tileLayer as OSM).baseUrls.contains("http://c.tile.openstreetmap.org"))
        // OSM
        tileLayer = TileLayer.getTileLayer("osm")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof OSM)
        assertTrue((tileLayer as OSM).baseUrls.contains("http://a.tile.openstreetmap.org"))
        assertTrue((tileLayer as OSM).baseUrls.contains("http://b.tile.openstreetmap.org"))
        assertTrue((tileLayer as OSM).baseUrls.contains("http://c.tile.openstreetmap.org"))
    }

    @Test
    void getTileLayerFromParams() {
        // OSM
        TileLayer tileLayer = TileLayer.getTileLayer([type: 'osm', url: 'http://a.tile.openstreetmap.org'])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof OSM)
        assertTrue((tileLayer as OSM).baseUrls.contains("http://a.tile.openstreetmap.org"))
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        File file
        // OSM
        tileLayer = TileLayer.getTileLayer("type=osm url=http://a.tile.openstreetmap.org")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNull(tileRenderer)
    }

}

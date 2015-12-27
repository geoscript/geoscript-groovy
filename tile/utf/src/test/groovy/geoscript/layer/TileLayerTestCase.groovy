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
        File file = folder.newFolder("utfgrid")
        TileLayer tileLayer = TileLayer.getTileLayer("type=utfgrid file=${file.absolutePath}")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof UTFGrid)
    }

    @Test
    void getTileLayerFromParams() {
        File file = folder.newFolder("utfgrid")
        TileLayer tileLayer = TileLayer.getTileLayer([type: 'utfgrid', file: file])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof UTFGrid)
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        File file
        // UTFGrid
        file = folder.newFolder("utfgrid")
        tileLayer = TileLayer.getTileLayer("type=utfgrid file=${file.absolutePath}")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof UTFGridTileRenderer)
    }

}

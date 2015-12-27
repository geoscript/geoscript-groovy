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
        File file = folder.newFolder("tms")
        TileLayer tileLayer = TileLayer.getTileLayer("type=tms file=${file.absolutePath} format=jpeg")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof TMS)
        assertEquals(file.absolutePath, (tileLayer as TMS).dir.absolutePath)
        assertEquals("jpeg", (tileLayer as TMS).imageType)

    }

    @Test
    void getTileLayerFromParams() {
        File file = folder.newFolder("tms")
        TileLayer tileLayer = TileLayer.getTileLayer([type: 'tms', file: file, format: 'jpeg'])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof TMS)
        assertEquals(file.absolutePath, (tileLayer as TMS).dir.absolutePath)
        assertEquals("jpeg", (tileLayer as TMS).imageType)
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        File file
        // TMS
        file = folder.newFolder("tms")
        tileLayer = TileLayer.getTileLayer("type=tms file=${file.absolutePath} format=jpeg")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof ImageTileRenderer)
    }

}

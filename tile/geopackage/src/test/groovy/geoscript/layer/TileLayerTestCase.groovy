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
        // GeoPackage params
        File file = folder.newFile('test.gpkg')
        file.delete()
        TileLayer tileLayer = TileLayer.getTileLayer("type=geopackage file=${file.absolutePath}")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof GeoPackage)
        // GeoPackage file
        file = folder.newFile('test.gpkg')
        file.delete()
        tileLayer = TileLayer.getTileLayer(file.absolutePath)
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof GeoPackage)
    }

    @Test
    void getTileLayerFromParams() {
        // GeoPackage (file doesn't exist)
        File file = folder.newFile('test.gpkg')
        file.delete()
        TileLayer tileLayer = TileLayer.getTileLayer([type: 'geopackage', file: file])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof GeoPackage)
        // GeoPackage (empty file)
        file = folder.newFile('test.gpkg')
        file.delete()
        tileLayer = TileLayer.getTileLayer([type: 'geopackage', file: file])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof GeoPackage)
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        File file
        // GeoPackage params
        file = folder.newFile('test.gpkg')
        file.delete()
        tileLayer = TileLayer.getTileLayer("type=geopackage file=${file.absolutePath}")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof ImageTileRenderer)
        // GeoPackage file
        file = folder.newFile('test.gpkg')
        file.delete()
        tileLayer = TileLayer.getTileLayer(file.absolutePath)
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof ImageTileRenderer)
    }

}

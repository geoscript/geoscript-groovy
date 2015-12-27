package geoscript.layer

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertEquals
import static junit.framework.Assert.assertNotNull
import static junit.framework.Assert.assertTrue

class TileLayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test
    void getTileLayerFromString() {
        // VectorTiles Directory
        File file = folder.newFolder("vectortiles")
        TileLayer tileLayer = TileLayer.getTileLayer("type=vectortiles file=${file.absolutePath} format=mvt pyramid=GlobalMercator")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof VectorTiles)
        assertEquals("mvt", (tileLayer as VectorTiles).type)
        assertEquals(file.absolutePath, (tileLayer as VectorTiles).dir.absolutePath)
        assertEquals(Pyramid.createGlobalMercatorPyramid(), (tileLayer as VectorTiles).pyramid)
        // VectorTiles URL
        tileLayer = TileLayer.getTileLayer("type=vectortiles url=http://vectortiles.org format=pbf pyramid=GlobalGeodetic")
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof VectorTiles)
        assertEquals("pbf", (tileLayer as VectorTiles).type)
        assertEquals("http://vectortiles.org", (tileLayer as VectorTiles).url.toString())
        assertEquals(Pyramid.createGlobalGeodeticPyramid(), (tileLayer as VectorTiles).pyramid)
    }

    @Test
    void getTileLayerFromParams() {
        // VectorTiles Directory
        File file = folder.newFolder("vectortiles")
        TileLayer tileLayer = TileLayer.getTileLayer([type: 'vectortiles', file: file, format: 'mvt', pyramid: 'GlobalMercator'])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof VectorTiles)
        assertEquals("mvt", (tileLayer as VectorTiles).type)
        assertEquals(file.absolutePath, (tileLayer as VectorTiles).dir.absolutePath)
        assertEquals(Pyramid.createGlobalMercatorPyramid(), (tileLayer as VectorTiles).pyramid)
        // VectorTiles URL
        tileLayer = TileLayer.getTileLayer([type:'vectortiles', url: 'http://vectortiles.org', format: 'pbf', pyramid: Pyramid.createGlobalGeodeticPyramid()])
        assertNotNull(tileLayer)
        assertTrue(tileLayer instanceof VectorTiles)
        assertEquals("pbf", (tileLayer as VectorTiles).type)
        assertEquals("http://vectortiles.org", (tileLayer as VectorTiles).url.toString())
        assertEquals(Pyramid.createGlobalGeodeticPyramid(), (tileLayer as VectorTiles).pyramid)
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        File file
        // VectorTiles Directory
        file = folder.newFolder("vectortiles")
        tileLayer = TileLayer.getTileLayer("type=vectortiles file=${file.absolutePath} format=mvt pyramid=GlobalMercator")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof VectorTileRenderer)
        // VectorTiles URL
        tileLayer = TileLayer.getTileLayer("type=vectortiles url=http://vectortiles.org format=pbf pyramid=GlobalGeodetic")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNotNull(tileRenderer)
        assertTrue(tileRenderer instanceof PbfVectorTileRenderer)
    }

}

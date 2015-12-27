package geoscript.layer

import geoscript.GeoScript
import geoscript.feature.Feature
import geoscript.geom.Bounds
import geoscript.proj.Projection
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.geotools.image.test.ImageAssert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import javax.imageio.ImageIO

import static junit.framework.Assert.*

/**
 * The TileLayer Unit Test
 * @author Jared Erickson
 */
class TileLayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private TileLayer getTileLayer(boolean withTiles) {
        File file = folder.newFolder("states")
        file.mkdir()
        XYZ tiles = new XYZ("states","png",file,Pyramid.createGlobalMercatorPyramid())
        if (withTiles) {
            GeoScript.unzip(new File(getClass().getClassLoader().getResource("tiles.zip").toURI()), file)
        }
        tiles
    }

    @Test
    void create() {
        TileLayer layer = getTileLayer(false)
        Bounds b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
        assertEquals "states", layer.name
        assertEquals new Projection("EPSG:3857"), layer.proj
        assertEquals b, layer.bounds
        Pyramid pyramid = layer.pyramid
        assertEquals "EPSG:3857", pyramid.proj.id
        assertEquals b, pyramid.bounds
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        assertEquals 20, pyramid.grids.size()
        pyramid.grids.eachWithIndex { Grid g, int z ->
            assertEquals z, g.z
            int n = Math.pow(2, z)
            assertEquals n, g.width
            assertEquals n, g.height
            assertEquals 156412.0 / n, g.xResolution, 0.01
            assertEquals 156412.0 / n, g.yResolution, 0.01
        }
        layer.close()
    }

    @Test
    void get() {
        TileLayer layer = getTileLayer(true)
        Tile tile = layer.get(4, 2, 3)
        assertNotNull tile
        assertEquals 4, tile.z
        assertEquals 2, tile.x
        assertEquals 3, tile.y
        assertNotNull tile.data
        layer.close()
    }

    @Test
    void put() {
        TileLayer layer = getTileLayer(false)

        // Make sure Tile doesn't exist in database
        Tile tile = layer.get(10, 0, 0)
        assertNotNull tile
        assertEquals 10, tile.z
        assertEquals 0, tile.x
        assertEquals 0, tile.y
        assertNull tile.data

        // Load a tile image
        File f = new File(getClass().getClassLoader().getResource("0.png").toURI())
        tile.data = f.bytes

        // Save Tile and make sure it saved correctly by getting it again
        layer.put(tile)
        tile = layer.get(10, 0, 0)
        assertNotNull tile
        assertEquals 10, tile.z
        assertEquals 0, tile.x
        assertEquals 0, tile.y
        assertNotNull tile.data
        layer.close()
    }

    @Test
    void tilesByZoomLevel() {
        TileLayer layer = getTileLayer(true)
        TileCursor cursor = layer.tiles(1)
        assertEquals 1, cursor.z
        assertEquals 0, cursor.minX
        assertEquals 0, cursor.minY
        assertEquals 1, cursor.maxX
        assertEquals 1, cursor.maxY
        assertEquals 2, cursor.width
        assertEquals 2, cursor.height
        assertEquals 4, cursor.size
        int c = 0
        cursor.each { Tile t ->
            assertEquals 1, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 4, c
        layer.close()
    }

    @Test
    void tilesByTileCoordinates() {
        TileLayer layer = getTileLayer(true)
        TileCursor cursor = layer.tiles(2, 1, 2, 3, 3)
        assertEquals 2, cursor.z
        assertEquals 1, cursor.minX
        assertEquals 2, cursor.minY
        assertEquals 3, cursor.maxX
        assertEquals 3, cursor.maxY
        assertEquals 3, cursor.width
        assertEquals 2, cursor.height
        assertEquals 6, cursor.size
        int c = 0
        cursor.each { Tile t ->
            assertEquals 2, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 6, c
        layer.close()
    }

    @Test
    void tilesByBoundsAndZoomLevel() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, 3)
        assertEquals 3, cursor.z
        assertEquals 1, cursor.minX
        assertEquals 5, cursor.minY
        assertEquals 1, cursor.maxX
        assertEquals 5, cursor.maxY
        assertEquals 1, cursor.width
        assertEquals 1, cursor.height
        assertEquals 1, cursor.size
        int c = 0
        cursor.each { Tile t ->
            assertEquals 3, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 1, c
        layer.close()
    }

    @Test
    void tilesByBoundsAndResolutions() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, b.width / 400, b.height / 300)
        assertEquals 4, cursor.z
        assertEquals 2, cursor.minX
        assertEquals 9, cursor.minY
        assertEquals 5, cursor.maxX
        assertEquals 10, cursor.maxY
        assertEquals 4, cursor.width
        assertEquals 2, cursor.height
        assertEquals 8, cursor.size
        int c = 0
        cursor.each { Tile t ->
            assertEquals 4, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 8, c
        layer.close()
    }

    @Test
    void tilesByBoundsAndImageSize() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, 400, 300)
        assertEquals 4, cursor.z
        assertEquals 2, cursor.minX
        assertEquals 9, cursor.minY
        assertEquals 5, cursor.maxX
        assertEquals 10, cursor.maxY
        assertEquals 4, cursor.width
        assertEquals 2, cursor.height
        assertEquals 8, cursor.size
        int c = 0
        cursor.each { Tile t ->
            assertEquals 4, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 8, c
        layer.close()
    }

    @Test
    void getTileCoordinates() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Map coords = layer.getTileCoordinates(b, layer.pyramid.grid(4))
        assertEquals 2, coords.minX
        assertEquals 9, coords.minY
        assertEquals 5, coords.maxX
        assertEquals 10, coords.maxY
        layer.close()
    }

    @Test
    void getRaster() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Raster raster = layer.getRaster(layer.tiles(b, 4))
        assertNotNull raster
        ImageAssert.assertEquals(new File(getClass().getClassLoader().getResource("geoscript/layer/tilelayer_raster.png").toURI()), raster.image, 100)
        layer.close()
    }

    @Test
    void getRasterCropped() {
        TileLayer layer = getTileLayer(true)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Raster raster = layer.getRaster(b, 400, 300)
        assertNotNull raster
        ImageAssert.assertEquals(new File(getClass().getClassLoader().getResource("geoscript/layer/tilelayer_raster_cropped.png").toURI()), raster.image, 100)
        layer.close()
    }

    @Test
    void getLayer() {
        TileLayer layer = getTileLayer(false)
        Layer vlayer = layer.getLayer(layer.tiles(1))
        assertNotNull vlayer
        assertTrue vlayer.schema.has("the_geom")
        assertTrue vlayer.schema.has("id")
        assertTrue vlayer.schema.has("z")
        assertTrue vlayer.schema.has("x")
        assertTrue vlayer.schema.has("y")
        assertEquals 4, vlayer.count
        vlayer.eachFeature { Feature f ->
            assertTrue f['id'] in [0, 1, 2, 3]
            assertTrue f['z'] == 1
            assertTrue f['x'] in [0, 1]
            assertTrue f['y'] in [0, 1]
            assertNotNull f.geom
        }
    }

    @Test
    void withTileLayer() {
        TileLayer.withTileLayer(getTileLayer(true)) { TileLayer layer ->
            Tile tile = layer.get(4, 2, 3)
            assertNotNull tile
            assertEquals 4, tile.z
            assertEquals 2, tile.x
            assertEquals 3, tile.y
            assertNotNull tile.data
        }
    }

    @Test
    void getTileLayerFromString() {
        TileLayer tileLayer = TileLayer.getTileLayer("type=asdfasd")
        assertNull(tileLayer)
    }

    @Test
    void getTileLayerFromMap() {
        TileLayer tileLayer = TileLayer.getTileLayer([type:'asdf'])
        assertNull(tileLayer)
    }

    @Test void getTileRenderer() {
        TileRenderer tileRenderer
        TileLayer tileLayer
        // Null
        tileLayer = TileLayer.getTileLayer("type=asdfasd")
        tileRenderer = TileLayer.getTileRenderer(tileLayer, new Layer())
        assertNull(tileRenderer)
    }

}

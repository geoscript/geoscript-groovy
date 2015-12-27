package geoscript.layer

import geoscript.geom.Bounds
import geoscript.style.Fill
import geoscript.style.Stroke
import geoscript.workspace.Workspace
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The TileCursor Unit Test
 * @author Jared Erickson
 */
class TileCursorTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    private TileLayer getTileLayer() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        shp.style = new Fill("wheat") + new Stroke("navy", 0.1)
        File file = folder.newFolder("states")
        XYZ tiles = new XYZ("states","png",file,Pyramid.createGlobalMercatorPyramid())
        ImageTileRenderer renderer = new ImageTileRenderer(tiles, shp)
        TileGenerator generator = new TileGenerator()
        generator.generate(tiles, renderer, 0, 4)
        tiles.close()
        tiles
    }

    @Test
    void zoomLevel() {
        TileLayer layer = getTileLayer()
        TileCursor cursor = new TileCursor(layer, 1)
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
    }

    @Test
    void tileCoordinates() {
        TileLayer layer = getTileLayer()
        TileCursor cursor = new TileCursor(layer, 2, 1, 2, 3, 3)
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
    }

    @Test
    void zoomAndBounds() {
        TileLayer layer = getTileLayer()
        Bounds b = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = new TileCursor(layer, b, 3)
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
    }

    @Test
    void boundsAndResolution() {
        TileLayer layer = getTileLayer()
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = new TileCursor(layer, b, b.width / 400, b.height / 300)
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
    }

    @Test
    void boundsAndSize() {
        TileLayer layer = getTileLayer()
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = new TileCursor(layer, b, 400, 300)
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
    }

    @Test
    void getBounds() {
        TileLayer layer = getTileLayer()
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = new TileCursor(layer, b, 400, 300)
        Bounds cursorBounds = cursor.bounds
        assertTrue cursorBounds.contains(b)
        assertNotNull cursorBounds.proj
    }

    @Test
    void resetAfterHasNextFalse() {
        TileLayer layer = getTileLayer()
        TileCursor cursor = new TileCursor(layer, 1)
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
        c = 0
        cursor.each { Tile t ->
            assertEquals 1, t.z
            assertNotNull t.data
            c++
        }
        assertEquals 4, c
    }

}

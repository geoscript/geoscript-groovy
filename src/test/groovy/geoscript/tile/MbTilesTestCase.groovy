package geoscript.tile

import geoscript.geom.Bounds
import geoscript.layer.Raster
import geoscript.layer.WorldImage
import geoscript.proj.Projection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.*

/**
 * The MbTiles Unit Test
 * @author Jared Erickson
 */
class MbTilesTestCase {

    @Rule public TemporaryFolder folder = new TemporaryFolder()

    @Test void create() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
        junit.framework.Assert.assertEquals "states", layer.name
        org.junit.Assert.assertEquals new Projection("EPSG:3857"), layer.proj
        org.junit.Assert.assertEquals b, layer.bounds
        Pyramid pyramid = layer.pyramid
        junit.framework.Assert.assertEquals "EPSG:3857", pyramid.proj.id
        org.junit.Assert.assertEquals b, pyramid.bounds
        junit.framework.Assert.assertEquals 256, pyramid.tileWidth
        junit.framework.Assert.assertEquals 256, pyramid.tileHeight
        org.junit.Assert.assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        junit.framework.Assert.assertEquals 20, pyramid.grids.size()
        pyramid.grids.eachWithIndex { Grid g, int z ->
            org.junit.Assert.assertEquals z, g.z
            int n = Math.pow(2, z)
            junit.framework.Assert.assertEquals n, g.width
            junit.framework.Assert.assertEquals n, g.height
            org.junit.Assert.assertEquals 156412.0 / n, g.xResolution, 0.01
            org.junit.Assert.assertEquals 156412.0 / n, g.yResolution, 0.01
        }
        layer.close()
    }

    @Test void get() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Tile tile = layer.get(4, 2, 3)
        org.junit.Assert.assertNotNull tile
        org.junit.Assert.assertEquals 4, tile.z
        org.junit.Assert.assertEquals 2, tile.x
        org.junit.Assert.assertEquals 3, tile.y
        org.junit.Assert.assertNotNull tile.data
        layer.close()
    }

    @Test void put() {
        // Since we are modifying the mbtiles file copy it to a temp file
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        File newFile = folder.newFile("states_temp.mbtiles")
        newFile.withOutputStream {out ->
            file.withInputStream {inp ->
                out << inp
            }
        }
        MBTiles layer = new MBTiles(newFile)

        // Make sure Tile doesn't exist in database
        Tile tile = layer.get(10, 0, 0)
        org.junit.Assert.assertNotNull tile
        org.junit.Assert.assertEquals 10, tile.z
        org.junit.Assert.assertEquals 0, tile.x
        org.junit.Assert.assertEquals 0, tile.y
        junit.framework.Assert.assertNull tile.data

        // Load a tile image
        File f = new File(getClass().getClassLoader().getResource("0.png").toURI())
        tile.data = f.bytes

        // Save Tile and make sure it saved correctly by getting it again
        layer.put(tile)
        tile = layer.get(10, 0, 0)
        org.junit.Assert.assertNotNull tile
        org.junit.Assert.assertEquals 10, tile.z
        org.junit.Assert.assertEquals 0, tile.x
        org.junit.Assert.assertEquals 0, tile.y
        org.junit.Assert.assertNotNull tile.data
        layer.close()
    }

    @Test void tilesByZoomLevel() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        TileCursor cursor = layer.tiles(1)
        org.junit.Assert.assertEquals 1, cursor.z
        org.junit.Assert.assertEquals 0, cursor.minX
        org.junit.Assert.assertEquals 0, cursor.minY
        org.junit.Assert.assertEquals 1, cursor.maxX
        org.junit.Assert.assertEquals 1, cursor.maxY
        org.junit.Assert.assertEquals 2, cursor.width
        org.junit.Assert.assertEquals 2, cursor.height
        org.junit.Assert.assertEquals 4, cursor.size
        int c = 0
        cursor.each{ Tile t ->
            org.junit.Assert.assertEquals 1, t.z
            org.junit.Assert.assertNotNull t.data
            c++
        }
        junit.framework.Assert.assertEquals 4, c
        layer.close()
    }

    @Test void tilesByTileCoordinates() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        TileCursor cursor = layer.tiles(2, 1, 2, 3, 3)
        org.junit.Assert.assertEquals 2, cursor.z
        org.junit.Assert.assertEquals 1, cursor.minX
        org.junit.Assert.assertEquals 2, cursor.minY
        org.junit.Assert.assertEquals 3, cursor.maxX
        org.junit.Assert.assertEquals 3, cursor.maxY
        org.junit.Assert.assertEquals 3, cursor.width
        org.junit.Assert.assertEquals 2, cursor.height
        org.junit.Assert.assertEquals 6, cursor.size
        int c = 0
        cursor.each{ Tile t ->
            org.junit.Assert.assertEquals 2, t.z
            org.junit.Assert.assertNotNull t.data
            c++
        }
        junit.framework.Assert.assertEquals 6, c
        layer.close()
    }

    @Test void tilesByBoundsAndZoomLevel() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, 3)
        org.junit.Assert.assertEquals 3, cursor.z
        org.junit.Assert.assertEquals 1, cursor.minX
        org.junit.Assert.assertEquals 5, cursor.minY
        org.junit.Assert.assertEquals 1, cursor.maxX
        org.junit.Assert.assertEquals 5, cursor.maxY
        org.junit.Assert.assertEquals 1, cursor.width
        org.junit.Assert.assertEquals 1, cursor.height
        org.junit.Assert.assertEquals 1, cursor.size
        int c = 0
        cursor.each{ Tile t ->
            org.junit.Assert.assertEquals 3, t.z
            org.junit.Assert.assertNotNull t.data
            c++
        }
        junit.framework.Assert.assertEquals 1, c
        layer.close()
    }

    @Test void tilesByBoundsAndResolutions() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, b.width / 400, b.height / 300)
        org.junit.Assert.assertEquals 4, cursor.z
        org.junit.Assert.assertEquals 2, cursor.minX
        org.junit.Assert.assertEquals 9, cursor.minY
        org.junit.Assert.assertEquals 5, cursor.maxX
        org.junit.Assert.assertEquals 10, cursor.maxY
        org.junit.Assert.assertEquals 4, cursor.width
        org.junit.Assert.assertEquals 2, cursor.height
        org.junit.Assert.assertEquals 8, cursor.size
        int c = 0
        cursor.each{ Tile t ->
            org.junit.Assert.assertEquals 4, t.z
            org.junit.Assert.assertNotNull t.data
            c++
        }
        junit.framework.Assert.assertEquals 8, c
        layer.close()
    }

    @Test void tilesByBoundsAndImageSize() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        TileCursor cursor = layer.tiles(b, 400, 300)
        org.junit.Assert.assertEquals 4, cursor.z
        org.junit.Assert.assertEquals 2, cursor.minX
        org.junit.Assert.assertEquals 9, cursor.minY
        org.junit.Assert.assertEquals 5, cursor.maxX
        org.junit.Assert.assertEquals 10, cursor.maxY
        org.junit.Assert.assertEquals 4, cursor.width
        org.junit.Assert.assertEquals 2, cursor.height
        org.junit.Assert.assertEquals 8, cursor.size
        int c = 0
        cursor.each{ Tile t ->
            org.junit.Assert.assertEquals 4, t.z
            org.junit.Assert.assertNotNull t.data
            c++
        }
        junit.framework.Assert.assertEquals 8, c
        layer.close()
    }

    @Test void getTileCoordinates() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Map coords = layer.getTileCoordinates(b, layer.pyramid.grid(4))
        junit.framework.Assert.assertEquals 2, coords.minX
        junit.framework.Assert.assertEquals 9, coords.minY
        junit.framework.Assert.assertEquals 5, coords.maxX
        junit.framework.Assert.assertEquals 10, coords.maxY
        layer.close()
    }

    @Test void getRaster() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Raster raster = layer.getRaster(layer.tiles(b, 4))
        org.junit.Assert.assertNotNull raster
        File out = folder.newFile("raster.png")
        WorldImage format = new WorldImage(out)
        format.write(raster)
        junit.framework.Assert.assertTrue out.exists()
        junit.framework.Assert.assertTrue out.length() > 0
        layer.close()
    }

    @Test void getRasterCropped() {
        File file = new File(getClass().getClassLoader().getResource("states.mbtiles").toURI())
        MBTiles layer = new MBTiles(file)
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Raster raster = layer.getRaster(b, 400, 300)
        org.junit.Assert.assertNotNull raster
        File out = folder.newFile("raster.png")
        WorldImage format = new WorldImage(out)
        format.write(raster)
        junit.framework.Assert.assertTrue out.exists()
        junit.framework.Assert.assertTrue out.length() > 0
        layer.close()
    }

}

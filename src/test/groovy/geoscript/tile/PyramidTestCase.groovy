package geoscript.tile

import geoscript.geom.Bounds
import geoscript.proj.Projection

import static junit.framework.Assert.*
import org.junit.Test

/**
 * The Tile Pyramid Unit Test
 * @author Jared Erickson
 */
class PyramidTestCase {

    @Test void create() {
        Pyramid pyramid = new Pyramid(
                proj: new Projection("EPSG:3857"),
                bounds: new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857"),
                origin: Pyramid.Origin.TOP_LEFT,
                tileWidth: 512,
                tileHeight: 512,
                grids: [
                        new Grid(0, 1, 1, 156412, 156412),
                        new Grid(1, 2, 2, 78206, 78206)
                ]

        )
        assertEquals "EPSG:3857", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
        assertEquals b, pyramid.bounds
        assertEquals 512, pyramid.tileWidth
        assertEquals 512, pyramid.tileHeight
        assertEquals Pyramid.Origin.TOP_LEFT, pyramid.origin
        assertEquals 2, pyramid.grids.size()
    }

    @Test void gridByZoomLevel() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Grid grid = pyramid.grid(4)
        assertNotNull grid
        assertEquals 4, grid.z
    }

    @Test void gridByBoundsAndResolutions() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        Grid grid = pyramid.grid(bounds, bounds.width / 400.0, bounds.height / 200.0)
        assertNotNull grid
        assertEquals 8, grid.z
    }

    @Test void gridByBoundsAndSize() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        Grid grid = pyramid.grid(bounds, 400, 200)
        assertNotNull grid
        assertEquals 8, grid.z
    }

    @Test void tileBounds() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(0,0,0))
        assertEquals bounds, new Bounds(-2.0036395147881314E7,-2.0037471205137067E7,2.0036395147881314E7,2.003747120513706E7,'EPSG:3857')
        bounds = pyramid.bounds(new Tile(1,0,0))
        assertEquals bounds, new Bounds(-2.0036395147881314E7,-2.0037471205137067E7,0.0,-3.725290298461914E-9,'EPSG:3857')
    }

    @Test void osmTileBounds() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        Tile tile = new Tile(7,28,45)
        Bounds b1 = new Bounds(-1.127047227068324E7,5635538.776444796,-1.0957403596497595E7,5948624.264025062,"EPSG:3857")
        Bounds b2 = pyramid.bounds(tile)
        assertEquals(b1, b2)
    }

    @Test void createGlobalMercatorPyramid() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        assertEquals "EPSG:3857", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
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
    }

}

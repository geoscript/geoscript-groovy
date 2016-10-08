package geoscript.layer

import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.proj.Projection
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static junit.framework.Assert.*

/**
 * The Tile Pyramid Unit Test
 * @author Jared Erickson
 */
class PyramidTestCase {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder()

    @Test
    void create() {
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
        assertEquals 0, pyramid.minGrid.z
        assertEquals 1, pyramid.maxGrid.z
    }

    @Test
    void createDefault() {
        Pyramid pyramid = new Pyramid()
        assertEquals "EPSG:4326", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -90, 179.99, 90, "EPSG:4326")
        assertEquals b, pyramid.bounds
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        assertEquals 0, pyramid.grids.size()
    }

    @Test
    void gridByZoomLevel() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Grid grid = pyramid.grid(4)
        assertNotNull grid
        assertEquals 4, grid.z
    }

    @Test
    void gridByBoundsAndResolutions() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        Grid grid = pyramid.grid(bounds, bounds.width / 400.0, bounds.height / 200.0)
        assertNotNull grid
        assertEquals 8, grid.z
    }

    @Test
    void gridByBoundsAndSize() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = new Bounds(-123.09, 46.66, -121.13, 47.48, "EPSG:4326").reproject("EPSG:3857")
        Grid grid = pyramid.grid(bounds, 400, 200)
        assertNotNull grid
        assertEquals 8, grid.z
    }

    @Test
    void tileBounds() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        Bounds bounds = pyramid.bounds(new Tile(0, 0, 0))
        assertEquals bounds, new Bounds(-2.0036395147881314E7, -2.0037471205137067E7, 2.0036395147881314E7, 2.003747120513706E7, 'EPSG:3857')
        bounds = pyramid.bounds(new Tile(1, 0, 0))
        assertEquals bounds, new Bounds(-2.0036395147881314E7, -2.0037471205137067E7, 0.0, -3.725290298461914E-9, 'EPSG:3857')
    }

    @Test
    void osmTileBounds() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        Tile tile = new Tile(7, 28, 45)
        Bounds b1 = new Bounds(-1.127047227068324E7, 5635538.776444796, -1.0957403596497595E7, 5948624.264025062, "EPSG:3857")
        Bounds b2 = pyramid.bounds(tile)
        assertEquals(b1, b2)
    }

    @Test
    void osmBoundsAroundPoint() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        pyramid.origin = Pyramid.Origin.TOP_LEFT
        Bounds bounds = pyramid.bounds(Projection.transform(new Point(-100.81,46.81),"EPSG:4326","EPSG:3857"), 8, 400, 300)
        assertEquals(new Bounds(-1.1344314741869908E7,5819468.535791107,-1.1099920991869908E7,6002763.848291107, "EPSG:3857"), bounds)
    }

    @Test
    void getTileCoordinates() {
        Pyramid pyramid = Pyramid.createGlobalMercatorPyramid()
        // Grid
        Bounds b = new Bounds(-124.73142200000001, 24.955967, -66.969849, 49.371735, "EPSG:4326").reproject("EPSG:3857")
        Map coords = pyramid.getTileCoordinates(b, pyramid.grid(4))
        assertEquals 2, coords.minX
        assertEquals 9, coords.minY
        assertEquals 5, coords.maxX
        assertEquals 10, coords.maxY
        // Zoom Level
        b = new Bounds(20.798492,36.402494,22.765045,37.223768, "EPSG:4326").reproject("EPSG:3857")
        coords = pyramid.getTileCoordinates(b, 10)
        assertEquals 571, coords.minX
        assertEquals 623, coords.minY
        assertEquals 576, coords.maxX
        assertEquals 626, coords.maxY
    }

    @Test
    void createGlobalMercatorPyramid() {
        // Default Origin.BOTTOM_LEFT
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
        // Origin.TOP_LEFT
        pyramid = Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.TOP_LEFT)
        assertEquals "EPSG:3857", pyramid.proj.id
        b = new Bounds(-179.99, -85.0511, 179.99, 85.0511, "EPSG:4326").reproject("EPSG:3857")
        assertEquals b, pyramid.bounds
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.TOP_LEFT, pyramid.origin
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

    @Test
    void createGlobalGeodeticPyramid() {
        Pyramid pyramid = Pyramid.createGlobalGeodeticPyramid()
        assertEquals "EPSG:4326", pyramid.proj.id
        Bounds b = new Bounds(-179.99, -89.99, 179.99, 89.99, "EPSG:4326")
        assertEquals b, pyramid.bounds
        assertEquals 256, pyramid.tileWidth
        assertEquals 256, pyramid.tileHeight
        assertEquals Pyramid.Origin.BOTTOM_LEFT, pyramid.origin
        assertEquals 20, pyramid.grids.size()
        pyramid.grids.eachWithIndex { Grid g, int z ->
            assertEquals z, g.z
            int w = Math.pow(2, z + 1)
            int h = Math.pow(2, z)
            double res = 0.703125 / Math.pow(2, z)
            assertEquals w, g.width
            assertEquals h, g.height
            assertEquals res, g.xResolution, 0.01
            assertEquals res, g.yResolution, 0.01
        }
    }

    @Test void getFromXml() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String xml = p1.xml
        Pyramid p2 = Pyramid.fromXml(xml)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void getFromJson() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String json = p1.json
        Pyramid p2 = Pyramid.fromJson(json)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void getFromCsv() {
        Pyramid p1 = Pyramid.createGlobalMercatorPyramid()
        String csv = p1.csv
        Pyramid p2 = Pyramid.fromCsv(csv)
        assertEquals p1.proj, p2.proj
        assertEquals p1.bounds, p2.bounds
        assertEquals p1.origin, p2.origin
        assertEquals p1.tileWidth, p2.tileWidth
        assertEquals p1.tileHeight, p2.tileHeight
        assertEquals p1.grids, p2.grids
    }

    @Test void fromString() {
        // Well known names
        Pyramid p = Pyramid.fromString("GlobalMercator")
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        p = Pyramid.fromString("mercator")
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        p = Pyramid.fromString("GlobalMercatorBottomLeft")
        assertEquals Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.BOTTOM_LEFT), p
        p = Pyramid.fromString("GlobalGeodetic")
        assertEquals Pyramid.createGlobalGeodeticPyramid(), p
        p = Pyramid.fromString("geodetic")
        assertEquals Pyramid.createGlobalGeodeticPyramid(), p
        // JSON
        String json = Pyramid.createGlobalMercatorPyramid().json
        p = Pyramid.fromString(json)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        File f = temporaryFolder.newFile("pyramid.json")
        f.text = json
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        // XML
        String xml = Pyramid.createGlobalMercatorPyramid().xml
        p = Pyramid.fromString(xml)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        f = temporaryFolder.newFile("pyramid.xml")
        f.text = xml
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        // CSV
        String csv = Pyramid.createGlobalMercatorPyramid().csv
        p = Pyramid.fromString(csv)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
        f = temporaryFolder.newFile("pyramid.csv")
        f.text = csv
        p = Pyramid.fromString(f.absolutePath)
        assertEquals Pyramid.createGlobalMercatorPyramid(), p
    }

}

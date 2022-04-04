package geoscript.layer

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * A Tile Grid.
 * @author Jared Erickson
 */
@EqualsAndHashCode
@ToString(includePackage = false, includeNames = true)
class Grid {

    /**
     * The zoom level
     */
    long z

    /**
     * The width or number of columns
     */
    long width

    /**
     * The height or number of rows
     */
    long height

    /**
     * The number of tiles
     */
    long size

    /**
     * The x resolution
     */
    double xResolution

    /**
     * The y resolution
     */
    double yResolution

    /**
     * Create a new Grid
     * @param z The zoom level
     * @param width The width or number of columns
     * @param height The height or number of rows
     * @param xResolution The x resolution
     * @param yResolution The y resolution
     */
    Grid(long z, long width, long height, double xResolution, double yResolution) {
        this.z = z
        this.width = width
        this.height = height
        this.xResolution = xResolution
        this.yResolution = yResolution
        this.size = this.width * this.height
    }

    /**
     * Create Grids for a Global Geodetic Pyramid as defined by
     * http://wiki.osgeo.org/wiki/Tile_Map_Service_Specification#global-geodetic
     * @param maxZoomLevel The max zoom level
     * @return A List of Grids
     */
    static createGlobalGeodeticGrids(int maxZoomLevel) {
        (0..maxZoomLevel).collect { int z ->
            int col = Math.pow(2, z + 1)
            int row = Math.pow(2, z)
            double res = 0.703125 / Math.pow(2, z)
            new Grid(z, col, row, res, res)
        }
    }

    /**
     * Create Grids for a Global Mercator Pyramid as defined by
     * http://wiki.openstreetmap.org/wiki/Zoom_levels
     * @param maxZoomLevel The max zoom level
     * @return A List of Grids
     */
    static createGlobalMercatorGrids(int maxZoomLevel) {
        (0..maxZoomLevel).collect { int z ->
            int n = Math.pow(2, z)
            double res = 156412.0 / n
            new Grid(z, n, n, res, res)
        }
    }
}

package geoscript.tile

import geoscript.geom.Bounds
import geoscript.proj.Projection
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * The Tile Pyramid
 * @author Jared Erickson
 */
@EqualsAndHashCode
@ToString(includeNames = true, excludes = "grids")
class Pyramid {

    /**
     * The Projection
     */
    Projection proj = new Projection("EPSG:4326")

    /**
     * The Bounds
     */
    Bounds bounds = new Bounds(-179.99, 179.99, -90, 90, "EPSG:4326")

    /**
     * The Origin
     */
    Origin origin = Origin.BOTTOM_LEFT

    /**
     * The tile width
     */
    int tileWidth = 256

    /**
     * The tile height
     */
    int tileHeight = 256

    /**
     * The List of Grids
     */
    List<Grid> grids = []

    /**
     * The Origin enumeration
     */
    static enum Origin {
        BOTTOM_LEFT,
        TOP_LEFT,
        BOTTOM_RIGHT,
        TOP_RIGHT
    }

    /**
     * Find the Grid for the given zoom level
     * @param z The zoom level
     * @return The Grid or null
     */
    Grid grid(long z) {
        grids.find { Grid m ->
            m.z == z
        }
    }

    /**
     * Find the best Grid for the given Bounds and XY resolutions
     * @param b The Bounds
     * @param resX The x resolution
     * @param resY The y resolution
     * @return A best Grid or null
     */
    Grid grid(Bounds b, double resX, double resY) {
        Grid best = null
        double score = Double.MAX_VALUE
        grids.each { Grid m ->
            double res = Math.abs(resX - m.xResolution) + Math.abs(resY - m.yResolution)
            if (res < score) {
                score = res
                best = m
            }
        }
        best
    }

    /**
     * Find the best Grid for the given Bounds and image width and height
     * @param b The Bounds
     * @param w The image width
     * @param h The image height
     * @return The best Grid or null
     */
    Grid grid(Bounds b, int w, int h) {
        double resX = b.width / (w as double)
        double resY = b.height / (h as double)
        grid(b, resX, resY)
    }

    /**
     * Calculate the Bounds for the given Tile
     * @param t The Tile
     * @return A Bounds
     */
    Bounds bounds(Tile t) {
        Grid m = grid(t.z)
        if (m == null) {
            throw new IllegalArgumentException("No grid for zoom level ${t.z}")
        }
        int w = m.width
        int h = m.height
        double dx = bounds.width / (w as double)
        double dy = bounds.height / (h as double)

        double x
        if (origin == Origin.BOTTOM_LEFT || origin == Origin.TOP_LEFT) {
            x = bounds.minX + dx * t.x
        } else {
            x = bounds.minX + dx * (w - t.x)
        }

        double y
        if (origin == Origin.BOTTOM_LEFT || origin == Origin.BOTTOM_RIGHT) {
            y = bounds.minY + dy * t.y
        } else {
            y = bounds.minY + dy * (h - t.y)
        }

        new Bounds(x, y, x + dx, y + dy, this.proj)
    }

    /**
     * Create a Pyramid with Grids for common global web mercator tile sets
     * @return A Pyramid
     */
    static Pyramid createGlobalMercatorPyramid() {
        Projection latLonProj = new Projection("EPSG:4326")
        Projection mercatorProj = new Projection("EPSG:3857")
        Bounds latLonBounds = new Bounds(-179.99, -85.0511, 179.99, 85.0511, latLonProj)
        Bounds mercatorBounds = latLonBounds.reproject(mercatorProj)
        Pyramid p = new Pyramid(
            proj: mercatorProj,
            bounds: mercatorBounds,
            origin: Pyramid.Origin.BOTTOM_LEFT,
            tileWidth: 256,
            tileHeight: 256
        )
        int maxZoom = 19
        p.grids = (0..maxZoom).collect { int z ->
            int n = Math.pow(2, z)
            // http://wiki.openstreetmap.org/wiki/Zoom_levels
            double res = 156412.0 / n
            new Grid(z, n, n, res, res)
        }
        p
    }

}

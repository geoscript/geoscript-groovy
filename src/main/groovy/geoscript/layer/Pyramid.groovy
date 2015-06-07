package geoscript.layer

import geoscript.geom.Bounds
import geoscript.layer.io.CsvPyramidReader
import geoscript.layer.io.CsvPyramidWriter
import geoscript.layer.io.JsonPyramidReader
import geoscript.layer.io.JsonPyramidWriter
import geoscript.layer.io.XmlPyramidReader
import geoscript.layer.io.XmlPyramidWriter
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
    Bounds bounds = new Bounds(-179.99, -90, 179.99, 90, "EPSG:4326")

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
     * Get the max Grid by zoom level
     * @return THe max Grid by zoom level
     */
    Grid getMaxGrid() {
        grids.max { Grid g -> g.z }
    }

    /**
     * Get the min Grid by zoom level
     * @return The min Grid by zoom level
     */
    Grid getMinGrid() {
        grids.min { Grid g -> g.z }
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
            x = bounds.minX + (dx * (w - t.x)) - dx
        }

        double y
        if (origin == Origin.BOTTOM_LEFT || origin == Origin.BOTTOM_RIGHT) {
            y = bounds.minY + dy * t.y
        } else {
            y = bounds.minY + (dy * (h - t.y)) - dy
        }

        new Bounds(x, y, x + dx, y + dy, this.proj)
    }

    /**
     * Get this Pyramid as a CSV String
     * @return A CSV String
     */
    String getCsv() {
        new CsvPyramidWriter().write(this)
    }

    /**
     * Get this Pyramid as a XML String
     * @return A XML String
     */
    String getXml() {
        new XmlPyramidWriter().write(this)
    }

    /**
     * Get this JSON as a JSON String
     * @return A CSV String
     */
    String getJson() {
        new JsonPyramidWriter().write(this)
    }

    /**
     * Create a Pyramid from a String.  The String can be a well known name (GlobalMercator or GlobalMercatorBottomLeft),
     * a JSON String or File, an XML String or File, or a CSV String or File
     * @param str A Pyramid String or File
     * @return A Pyramid or null
     */
    static Pyramid fromString(String str) {
        // Well known names
        if (str.equalsIgnoreCase("GlobalMercator")) {
            Pyramid.createGlobalMercatorPyramid()
        } else if (str.equalsIgnoreCase("GlobalMercatorBottomLeft")) {
            Pyramid.createGlobalMercatorPyramid(origin: Pyramid.Origin.BOTTOM_LEFT)
        }
        // JSON
        else if (str.startsWith("{")) {
            fromJson(str)
        } else if (str.endsWith(".json")) {
            fromJson(new File(str).text)
        }
        // XML
        else if (str.startsWith("<")) {
            fromXml(str)
        } else if (str.endsWith(".xml")) {
            fromXml(new File(str).text)
        }
        // Text
        else if (str.endsWith(".txt") || str.endsWith(".csv")) {
            fromCsv(new File(str).text)
        } else {
            fromCsv(str)
        }
    }

    /**
     * Create a Pyramid from a CSV String
     * @param csv The CSV String
     * @return A Pyramid
     */
    static Pyramid fromCsv(String csv) {
        new CsvPyramidReader().read(csv)
    }

    /**
     * Create a Pyramid from an XML String
     * @param xml The XML String
     * @return A Pyramid
     */
    static Pyramid fromXml(String xml) {
        new XmlPyramidReader().read(xml)
    }

    /**
     * Create a Pyramid from a JSON String
     * @param json The JSON String
     * @return A Pyramid
     */
    static Pyramid fromJson(String json) {
        new JsonPyramidReader().read(json)
    }

    /**
     * Create a Pyramid with Grids for common global web mercator tile sets
     * @return A Pyramid
     */
    static Pyramid createGlobalMercatorPyramid(Map options = [:]) {
        Projection latLonProj = new Projection("EPSG:4326")
        Projection mercatorProj = new Projection("EPSG:3857")
        Bounds latLonBounds = new Bounds(-179.99, -85.0511, 179.99, 85.0511, latLonProj)
        Bounds mercatorBounds = latLonBounds.reproject(mercatorProj)
        Pyramid p = new Pyramid(
            proj: mercatorProj,
            bounds: mercatorBounds,
            origin: options.get("origin", Pyramid.Origin.BOTTOM_LEFT),
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

    /**
     * Create a Pyramid with Grids for common global geodetic tile sets.
     * http://wiki.osgeo.org/wiki/Tile_Map_Service_Specification#global-geodetic
     * @return A Pyramid
     */
    static Pyramid createGlobalGeodeticPyramid(Map options = [:]) {
        Projection latLonProj = new Projection("EPSG:4326")
        Bounds latLonBounds = new Bounds(-179.99, -89.99, 179.99, 89.99, latLonProj)
        Pyramid p = new Pyramid(
                proj: latLonProj,
                bounds: latLonBounds,
                origin: Pyramid.Origin.BOTTOM_LEFT,
                tileWidth: 256,
                tileHeight: 256
        )
        int maxZoom = options.get("maxZoom", 19)
        p.grids = (0..maxZoom).collect { int z ->
            int col = Math.pow(2, z + 1)
            int row = Math.pow(2, z)
            double res = 0.703125 / Math.pow(2, z)
            new Grid(z, col, row, res, res)
        }
        p
    }
}

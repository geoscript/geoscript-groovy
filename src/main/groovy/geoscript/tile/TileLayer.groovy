package geoscript.tile

import geoscript.feature.Field
import geoscript.geom.Bounds
import geoscript.layer.Layer
import geoscript.layer.Raster
import geoscript.proj.Projection
import geoscript.workspace.Memory
import geoscript.workspace.Workspace

import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * A TileLayer
 * @author Jared Erickson
 */
abstract class TileLayer implements Closeable {

    /**
     * The name
     */
    String name

    /**
     * The Bounds
     */
    Bounds bounds

    /**
     * The Projection
     */
    Projection proj

    /**
     * Get the Pyramid
     * @return The Pyramid
     */
    abstract Pyramid getPyramid()

    /**
     * Get a Tile
     * @param z The zoom level
     * @param x The column
     * @param y The row
     * @return A Tile
     */
    abstract Tile get(long z, long x, long y)

    /**
     * Add a Tile
     * @param t The Tile
     */
    abstract void put(Tile t)

    /**
     * Close the TileLayer
     */
    abstract void close() throws IOException

    /**
     * Get a TileCursor for all the Tiles in the zoom level
     * @param z The zoom level
     * @return A TileCursor
     */
    TileCursor tiles(long z) {
        new TileCursor(this, z)
    }

    /**
     * Get a TileCursor for all the Tiles in the zoom level for the given columns and rows
     * @param z The zoom level
     * @param minX The min x or column
     * @param minY The min y or row
     * @param maxX The max x or column
     * @param maxY The max y or row
     * @return A TileCursor
     */
    TileCursor tiles(long z, long minX, long minY, long maxX, long maxY) {
        new TileCursor(this, z, minX, minY, maxX, maxY)
    }

    /**
     * Get a TileCursor for all the Tiles within the given Bounds
     * @param z The zoom level
     * @param b The Bounds
     * @return A TileCursor
     */
    TileCursor tiles(Bounds b, long z) {
        new TileCursor(this, b, z)
    }

    /**
     * Get a TileCursor for all the Tiles within the given Bounds and resolutions
     * @param b The Bounds
     * @param resX The x resolution
     * @param resY The y resolution
     * @return A TileCursor
     */
    TileCursor tiles(Bounds b, double resX, double resY) {
        new TileCursor(this, b, resX, resY)
    }

    /**
     * Get a TileCursor for all the Tiles withing the given Bounds and image size
     * @param b The Bounds
     * @param w The image width
     * @param h The image height
     * @return A TileCursor
     */
    TileCursor tiles(Bounds b, int w, int h) {
        new TileCursor(this, b, w, h)
    }

    /**
     * Get Tile coordinates (minX, minY, maxX, maxY) for the given Bounds and Grid
     * @param b The Bounds
     * @param g The Grid
     * @return A Map with tile coordinates (minX, minY, maxX, maxY)
     */
    Map getTileCoordinates(Bounds b, Grid g) {
        int minX = Math.floor((((b.minX - bounds.minX) / bounds.width) * g.width))
        int maxX = Math.ceil(((b.maxX - bounds.minX) / bounds.width) * g.width) - 1
        if (pyramid.origin == Pyramid.Origin.TOP_RIGHT || pyramid.origin == Pyramid.Origin.BOTTOM_RIGHT) {
            int invertedMinX = g.width - maxX
            int invertedMaxX = g.width - minX
            minX = invertedMinX - 1
            maxX = invertedMaxX - 1
        }
        int minY = Math.floor(((b.minY - bounds.minY) / bounds.height) * g.height)
        int maxY = Math.ceil(((b.maxY - bounds.minY) / bounds.height) * g.height) - 1
        if (pyramid.origin == Pyramid.Origin.TOP_LEFT || pyramid.origin == Pyramid.Origin.TOP_RIGHT) {
            int invertedMinY = g.height - maxY
            int invertedMaxY = g.height - minY
            minY = invertedMinY - 1
            maxY = invertedMaxY - 1
        }
        [minX: minX, minY: minY, maxX: maxX, maxY: maxY]
    }

    /**
     * Get a Raster using Tiles from the TileCursor
     * @param cursor The TileCursor
     * @return A Raster
     */
    Raster getRaster(TileCursor cursor) {
        Bounds tileBounds = cursor.bounds
        int imageWidth = cursor.width * pyramid.tileWidth
        int imageHeight =  cursor.height * pyramid.tileHeight
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        cursor.each{ Tile tile ->
            int x
            if (pyramid.origin == Pyramid.Origin.TOP_LEFT || pyramid.origin == Pyramid.Origin.BOTTOM_LEFT) {
                x = (tile.x - cursor.minX) * pyramid.tileWidth
            } else {
                x = (cursor.maxX - tile.x) * pyramid.tileWidth
            }
            int y
            if (pyramid.origin == Pyramid.Origin.TOP_LEFT || pyramid.origin == Pyramid.Origin.TOP_RIGHT) {
                y = (tile.y - cursor.minY) * pyramid.tileHeight
            } else {
                y = (cursor.maxY - tile.y) * pyramid.tileHeight
            }
            g2d.drawImage(tile.image, x, y, pyramid.tileWidth, pyramid.tileHeight, null)
        }
        g2d.dispose()
        new Raster(image, tileBounds)
    }

    /**
     * Get a Layer of the Tiles in a TileCursor
     * @param options Optional named parameters
     * <ul>
     *     <li>outLayer = The name of the Layer</li>
     *     <li>outWorkspace = The Workspace</li>
     *     <li>geomFieldName = The name of the geometry Field</li>
     *     <li>idFieldName = The name of the ID Field</li>
     *     <li>zFieldName = The name of the Z Field</li>
     *     <li>xFieldName = The name of the X Field</li>
     *     <li>yFieldName = The name of the Y Field</li>
     * </ul>
     * @param cursor
     * @return
     */
    Layer getLayer(Map options = [:], TileCursor cursor) {
        String outLayerName = options.get("outLayer", "${this.name}_tiles")
        Workspace outWorkspace = options.get("outWorkspace", new Memory())
        String geomFieldName = options.get("geomFieldName","the_geom")
        String idFieldName = options.get("idFieldName","id")
        String zFieldName = options.get("zFieldName","z")
        String xFieldName = options.get("xFieldName","x")
        String yFieldName = options.get("yFieldName","y")
        Layer outLayer = outWorkspace.create(outLayerName, [
                new Field(idFieldName, "int"),
                new Field(zFieldName, "int"),
                new Field(xFieldName, "int"),
                new Field(yFieldName, "int"),
                new Field(geomFieldName, "Polygon", this.proj)
        ])
        outLayer.withWriter{ geoscript.layer.Writer w ->
            cursor.eachWithIndex { Tile tile, int i ->
                w.add(outLayer.schema.feature([
                        (idFieldName): i,
                        (zFieldName): tile.z,
                        (xFieldName): tile.x,
                        (yFieldName): tile.y,
                        (geomFieldName): this.pyramid.bounds(tile).geometry
                ]))
            }
        }
        outLayer
    }

    /**
     * Get a Raster using Tiles for the Bounds and image size.  The Raster is cropped
     * to exactly the given Bounds
     * @param b Bounds
     * @param w The image width
     * @param h The image height
     * @return A Raster
     */
    Raster getRaster(Bounds b, int w, int h) {
        getRaster(tiles(b,w,h)).crop(b)
    }

    @Override
    String toString() {
        this.name
    }

}

package geoscript.layer

import geoscript.geom.Bounds
import geoscript.geom.Point
import geoscript.style.RasterSymbolizer
import org.geotools.map.GridCoverageLayer

import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * A TileLayer that is made up of ImageTiles.
 * @author Jared Erickson
 */
abstract class ImageTileLayer extends TileLayer<ImageTile> implements Renderable {

    /**
     * Get a Raster using Tiles from the TileCursor.
     * If the TileCursor is empty a null Raster will be returned
     * @param cursor The TileCursor
     * @return A Raster
     */
    Raster getRaster(TileCursor<ImageTile> cursor) {
        // Make sure the TileCursor is not empty
        if (!cursor.empty) {
            Bounds tileBounds = cursor.bounds
            int imageWidth = cursor.width * pyramid.tileWidth
            int imageHeight = cursor.height * pyramid.tileHeight
            BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
            Graphics2D g2d = image.createGraphics()
            cursor.each { ImageTile tile ->
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
            return new Raster(image, tileBounds)
        } else {
            return null
        }
    }

    /**
     * Get a Raster using Tiles for the Bounds and image size.  The Raster is cropped
     * to exactly the given Bounds.
     * @param b Bounds
     * @param w The image width
     * @param h The image height
     * @return A Raster
     */
    Raster getRaster(Bounds b, int w, int h) {
        // The Bounds and the Pyramid Bounds must match,
        // reproject if necessary.
        if (b.proj && !b.proj.equals(this.pyramid.proj)) {
            b = b.reproject(this.pyramid.proj)
        }
        TileCursor c = tiles(b,w,h)
        if (!c.empty) {
            getRaster(c).crop(b)
        } else {
            new Raster(new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB), b)
        }
    }

    /**
     * Get a Raster around a Point at a given zoom level
     * @param p The Point (in the TileLayer's projection)
     * @param z The zoom level
     * @param w The image width
     * @param h The image height
     * @return A Raster
     */
    Raster getRaster(Point p, long z, int w, int h) {
        getRaster(this.pyramid.bounds(p, z, w, h), w, h)
    }

    @Override
    List<org.geotools.map.Layer> getMapLayers(Bounds bounds, List size) {
        def raster = this.getRaster(bounds.reproject(this.proj), size[0], size[1])
        [new GridCoverageLayer(raster.coverage, new RasterSymbolizer().gtStyle)]
    }
}

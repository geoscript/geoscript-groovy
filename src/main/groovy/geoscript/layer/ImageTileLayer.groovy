package geoscript.layer

import geoscript.geom.Bounds

import java.awt.Graphics2D
import java.awt.image.BufferedImage

/**
 * A TileLayer that is made up of ImageTiles.
 * @author Jared Erickson
 */
abstract class ImageTileLayer extends TileLayer<ImageTile> {

    /**
     * Get a Raster using Tiles from the TileCursor
     * @param cursor The TileCursor
     * @return A Raster
     */
    Raster getRaster(TileCursor<ImageTile> cursor) {
        Bounds tileBounds = cursor.bounds
        int imageWidth = cursor.width * pyramid.tileWidth
        int imageHeight =  cursor.height * pyramid.tileHeight
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB)
        Graphics2D g2d = image.createGraphics()
        cursor.each{ ImageTile tile ->
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
}

package geoscript.layer

import geoscript.geom.Bounds
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

/**
 * A TileGenerator
 * @author Jared Erickson
 */
class TileGenerator {

    /**
     * Whether to verbosely print status or not
     */
    boolean verbose = false

    /**
     * Generate Tiles for a TileLayer using a TileRenderer
     * @param options The optional named parameters:
     * <ul>
     *     <li>bounds = The Bounds of the Tiles to generate</li>
     *     <li>missingOnly = Whether to only generate missing tiles (true) or all tiles (false)</li>
     * </ul>
     * @param tileLayer The TileLayer
     * @param renderer The TileRenderer
     * @param startZoom The start zoom level
     * @param endZoom The end zoom level
     */
    void generate(Map options = [:], TileLayer tileLayer, TileRenderer renderer, int startZoom, int endZoom) {
        boolean missingOnly = options.get("missingOnly", false)
        Map metatile = options.get("metatile", [:])
        boolean doMetatiling = metatile && renderer instanceof ImageTileRenderer
        (startZoom..endZoom).each {zoom ->
            if (verbose) println "Zoom Level ${zoom}"
            long startTime = System.nanoTime()
            TileCursor tileCursor = options.bounds ? tileLayer.tiles(options.bounds, zoom) : tileLayer.tiles(zoom)
            if (!doMetatiling) {
                tileCursor.eachWithIndex { Tile t, int i ->
                    if (verbose) println "   ${i}). ${t}"
                    Bounds b = tileLayer.pyramid.bounds(t)
                    if (verbose) println "          Bounds${b}"
                    if (!missingOnly || (missingOnly && !t.data)) {
                        t.data = renderer.render(b)
                        tileLayer.put(t)
                    } else {
                        if (verbose) println "          Already generated!"
                    }
                }
            } else {

                ImageTileRenderer imageTileRenderer = renderer as ImageTileRenderer

                int metaTileWidth = metatile.width
                int metaTileHeight = metatile.height
                String imageType = metatile.imageType ?: "png"

                int metaTileColumns = (int) (Math.ceil(((float) tileCursor.width) / ((float) metaTileWidth)))
                int metaTileRows = (int) (Math.ceil(((float) tileCursor.height) / ((float) metaTileHeight)))

                int tileWidth = tileLayer.pyramid.tileWidth
                int tileHeight = tileLayer.pyramid.tileHeight

                Pyramid.Origin origin = tileLayer.pyramid.origin

                int tileCounter = 0

                (0..<metaTileColumns).each { int c ->

                    int startX = Math.min(tileCursor.maxX, tileCursor.minX + (c * metaTileWidth))
                    int endX =  Math.min(tileCursor.maxX, startX + metaTileWidth)

                    (0..<metaTileRows).each { int r ->

                        int startY = Math.min(tileCursor.maxY, tileCursor.minY + (r * metaTileHeight))
                        int endY =  Math.min(tileCursor.maxY, startY + metaTileHeight)

                        TileCursor metaTileCursor = new TileCursor(tileLayer, zoom, startX, startY, endX, endY)
                        Bounds bounds = metaTileCursor.bounds
                        if (verbose) println "   Metatile ${c} ${r} = ${startX},${startY} - ${endX},${endY} @ ${bounds}"

                        byte[] imageData = imageTileRenderer.render(size: [
                            width: tileLayer.pyramid.tileWidth * metaTileCursor.width,
                            height: tileLayer.pyramid.tileHeight * metaTileCursor.height],
                            bounds
                        )
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData))
                        int imageWidth = image.width
                        int imageHeight = image.height

                        (0..<(imageWidth / tileLayer.pyramid.tileWidth)).each { int imgX ->
                            (0..<(imageHeight / tileLayer.pyramid.tileHeight)).each { int imgY ->
                                int imgMinX = (tileWidth * imgX)
                                // Right
                                if (origin == Pyramid.Origin.BOTTOM_RIGHT || origin == Pyramid.Origin.TOP_RIGHT) {
                                    imgMinX = (imageWidth - imgMinX) - tileWidth
                                }
                                int imgMinY = (tileHeight * imgY)
                                // Bottom
                                if (origin == Pyramid.Origin.BOTTOM_LEFT || origin == Pyramid.Origin.BOTTOM_RIGHT) {
                                    imgMinY = (imageHeight - imgMinY) - tileHeight
                                }

                                long tileX = metaTileCursor.minX + imgX
                                long tileY = metaTileCursor.minY + imgY
                                ImageTile t = new ImageTile(zoom, tileX, tileY)
                                if (verbose) println "      ${tileCounter}). ${t}"

                                if (!missingOnly || (missingOnly && !t.data)) {
                                    BufferedImage subImage = image.getSubimage(imgMinX, imgMinY, tileWidth - 1, tileHeight - 1)
                                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()
                                    ImageIO.write(subImage, imageType, byteArrayOutputStream)
                                    t.data = byteArrayOutputStream.toByteArray()
                                    tileLayer.put(new ImageTile(zoom, tileX, tileY, byteArrayOutputStream.toByteArray()))
                                } else {
                                    if (verbose) println "          Already generated!"
                                }
                                tileCounter++
                            }
                        }
                    }
                }
            }
            if (verbose) {
                double endTime = System.nanoTime() - startTime
                int numberOfTiles = options.bounds ? tileLayer.tiles(options.bounds, zoom).size : tileLayer.pyramid.grid(zoom).size
                println "   Generating ${numberOfTiles} tile${numberOfTiles > 1 ? 's':''} took ${endTime / 1000000000.0} seconds"
            }
        }
    }

}

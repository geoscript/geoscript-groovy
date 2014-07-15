package geoscript.layer

import geoscript.geom.Bounds

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
     * @param tileLayer The TileLayer
     * @param renderer The TileRenderer
     * @param startZoom The start zoom level
     * @param endZoom The end zoom level
     */
    void generate(TileLayer tileLayer, TileRenderer renderer, int startZoom, int endZoom) {
        (startZoom..endZoom).each {zoom ->
            if (verbose) println "Zoom Level ${zoom}"
            long startTime = System.nanoTime()
            tileLayer.tiles(zoom).eachWithIndex { Tile t, int i ->
                if (verbose) println "   ${i}). ${t}"
                Bounds b = tileLayer.pyramid.bounds(t)
                if (verbose) println "          Bounds${b}"
                t.data = renderer.render(b)
                tileLayer.put(t)
            }
            if (verbose) {
                double endTime = System.nanoTime() - startTime
                int numberOfTiles = tileLayer.pyramid.grid(zoom).size
                println "   Generating ${numberOfTiles} tile${numberOfTiles > 1 ? 's':''} took ${endTime / 1000000000.0} seconds"
            }
        }
    }

}

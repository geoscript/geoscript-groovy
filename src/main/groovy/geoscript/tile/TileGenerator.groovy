package geoscript.tile

import geoscript.geom.Bounds

/**
 * A TileGenerator
 * @author Jared Erickson
 */
class TileGenerator {

    /**
     * The image type
     */
    String imageType = "png"

    /**
     * Whether to verbosely print status or not
     */
    boolean verbose = false

    /**
     * Generate Tiles for the TileLayer using one of more Map Layers between the start and end zoom levels
     * @param tileLayer The TileLayer
     * @param layers A Map Layer or a List of Map Layers
     * @param startZoom The start zoom level
     * @param endZoom The end zoom levelå
     */
    void generate(TileLayer tileLayer, def layers, int startZoom, int endZoom) {

        geoscript.render.Map map = new geoscript.render.Map(
            fixAspectRatio: false,
            proj: tileLayer.proj,
            width: tileLayer.pyramid.tileWidth,
            height: tileLayer.pyramid.tileHeight,
            type: imageType,
            layers: layers instanceof List ? layers : [layers],
            bounds: tileLayer.bounds
        )

        (startZoom..endZoom).each {zoom ->
            if (verbose) println "Zoom Level ${zoom}"
            tileLayer.tiles(zoom).eachWithIndex { Tile t, int i ->
                if (verbose) println "   ${i}). ${t}"
                Bounds b = tileLayer.pyramid.bounds(t)
                if (verbose) println "          Bounds${b}"
                map.bounds = b

                def out = new ByteArrayOutputStream()
                map.render(out)
                out.close()

                t.data = out.toByteArray()
                tileLayer.put(t)
            }
        }
    }

}

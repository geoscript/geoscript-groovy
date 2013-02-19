package geoscript.raster

import geoscript.proj.Projection
import org.geotools.gce.imagemosaic.ImageMosaicFormat

/**
 * A Format that can read and write image Mosaics.
 * @author Jared Erickson
 */
class Mosaic extends Format {

    /**
     * Create a new image Mosaic Format
     */
    Mosaic() {
        super(new ImageMosaicFormat())
    }
}
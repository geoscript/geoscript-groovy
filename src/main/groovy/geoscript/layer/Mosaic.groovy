package geoscript.layer

import org.geotools.gce.imagemosaic.ImageMosaicFormat

/**
 * A Format that can read and write image Mosaics.
 * @author Jared Erickson
 */
class Mosaic extends Format {

    /**
     * Create a new image Mosaic Format
     */
    @Deprecated
    Mosaic() {
        super(new ImageMosaicFormat())
    }

    /**
     * Create a new image Mosaic Format
     * @param stream The file
     */
    Mosaic(def stream) {
        super(new ImageMosaicFormat(), stream)
    }
}
package geoscript.raster

import geoscript.proj.Projection
import org.geotools.gce.imagemosaic.ImageMosaicFormat

/**
 * A Mosaic is a Raster that handles a mosaic of images.
 * @author Jared Erickson
 */
class Mosaic extends Raster {

    /**
     * Create a new Mosaic from a File directory
     * @param dir The File directory
     * @param proj The optional Projection
     */
    Mosaic(File dir, Projection proj = null) {
        super(new ImageMosaicFormat(), dir, proj)
    }
}
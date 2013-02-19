package geoscript.raster

import geoscript.proj.Projection
import org.geotools.gce.image.WorldImageFormat
import org.geotools.gce.image.WorldImageReader

/**
 * A Format that can read and write WorldImage Rasters.  The WorkImage Format
 * supports images with world files (gif/gfw, jpg/jgw, tif/tfw, png/pgw).
 * @author Jared Erickson
 */
class WorldImage extends Format {

    /**
     * Create a new WorldImage Format
     */
    WorldImage() {
        super(new WorldImageFormat())
    }
}
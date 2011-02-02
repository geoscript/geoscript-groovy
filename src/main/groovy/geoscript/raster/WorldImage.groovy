package geoscript.raster

import geoscript.proj.Projection
import org.geotools.gce.image.WorldImageFormat
import org.geotools.gce.image.WorldImageReader

/**
 * The WorldImage Raster supports images with world files (gif/gfw, jpg/jgw,
 * tif/tfw, png/pgw).
 * @author Jared Erickson
 */
class WorldImage extends Raster {

    /**
     * Create a new WorldImage Raster from a File
     * @param file The File
     * @param proj The optional Projection
     */
    WorldImage(File file, Projection proj = null) {
        super(new WorldImageFormat(), file, proj)
    }

    /**
     * Get the format name.
     * @return The format name
     */
    @Override
    String getFormat() {
        "${super.format} (${(reader as WorldImageReader).extension})"
    }
}
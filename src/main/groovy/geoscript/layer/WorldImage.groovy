package geoscript.layer

import org.geotools.gce.image.WorldImageFormat
import org.opengis.parameter.GeneralParameterValue

/**
 * A Format that can read and write WorldImage Rasters.  The WorldImage Format
 * supports images with world files (gif/gfw, jpg/jgw, tif/tfw, png/pgw).
 * @author Jared Erickson
 */
class WorldImage extends Format {

    /**
     * Create a new WorldImage Format
     */
    @Deprecated
    WorldImage() {
        super(new WorldImageFormat())
    }

    /**
     * Create a new WorldImage Format
     * @param stream The file
     */
    WorldImage(def stream) {
        super(new WorldImageFormat(), stream)
    }

    /**
     * Write the Raster to the destination object (usually a File)
     * @param raster The Raster to write
     * @param destination The destination object (usually a File)
     */
    @Override
    @Deprecated
    void write(Map options = [:], Raster raster, def destination) {
        String format = WorldImageFormat.FORMAT.getDefaultValue();
        if (destination instanceof File) {
            String fileName = ((File)destination).getName()
            format = fileName.substring(fileName.lastIndexOf(".") + 1)
        }
        options.put(WorldImageFormat.FORMAT.getName().toString(), format)
        super.write(options, raster, destination)
    }

    /**
     * Write the Raster to the destination object (usually a File)
     * @param raster The Raster to write
     */
    @Override
    void write(Map options = [:], Raster raster) {
        String format = WorldImageFormat.FORMAT.getDefaultValue();
        if (stream instanceof File) {
            String fileName = ((File)stream).getName()
            format = fileName.substring(fileName.lastIndexOf(".") + 1)
        }
        options.put(WorldImageFormat.FORMAT.getName().toString(), format)
        super.write(options, raster)
    }
}
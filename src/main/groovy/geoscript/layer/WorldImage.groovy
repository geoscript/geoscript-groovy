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
    WorldImage() {
        super(new WorldImageFormat())
    }

    /**
     * Write the Raster to the destination object (usually a File)
     * @param raster The Raster to write
     * @param destination The destination object (usually a File)
     */
    @Override
    void write(Map options = [:], Raster raster, def destination) {
        String format = WorldImageFormat.FORMAT.getDefaultValue();
        if (destination instanceof File) {
            String fileName = ((File)destination).getName()
            format = fileName.substring(fileName.lastIndexOf(".") + 1)
        }
        def params = gridFormat.getWriteParameters()
        params.parameter(WorldImageFormat.FORMAT.getName().toString()).setValue(format)
        def gpv = [params.parameter(WorldImageFormat.FORMAT.getName().toString())] as GeneralParameterValue[]
        gridFormat.getWriter(destination).write(raster.coverage, gpv)
    }
}
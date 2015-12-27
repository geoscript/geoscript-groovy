package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.image.WorldImageFormat

/**
 * A Format that can read and write WorldImage Rasters.  The WorldImage Format
 * supports images with world files (gif/gfw, jpg/jgw, tif/tfw, png/pgw).
 * @author Jared Erickson
 */
class WorldImage extends Format {

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

    /**
     * The WorldImage FormatFactory
     */
    static class Factory extends FormatFactory<WorldImage> {

        @Override
        protected List<String> getFileExtensions() {
            ["png", "jpg", "jpeg", "gif"]
        }

        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof WorldImageFormat) {
                new WorldImage(source)
            }
        }

        @Override
        protected Format createFromFile(File file) {
            new WorldImage(file)
        }
    }
}
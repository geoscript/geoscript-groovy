package geoscript.layer

import geoscript.layer.Format
import geoscript.layer.FormatFactory
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.geotiff.GeoTiffFormat

/**
 * A Format that can read and write GeoTIFFs.
 * <p>You can read a GeoTIFF.</p>
 * <p><code>GeoTIFF tiff = new GeoTIFF(new File("alki.tif"))</code></p>
 * <p><code>Raster raster  = tiff.read()</code></p>
 * @author Jared Erickson
 */
class GeoTIFF extends Format {

    /**
     * Create a new GeoTIFF
     * @param stream The file
     */
    GeoTIFF(def stream) {
        super(new GeoTiffFormat(), stream)
    }

    /**
     * The GeoTIFF FormatFactory
     */
    static class Factory extends FormatFactory<GeoTIFF> {

        @Override
        protected List<String> getFileExtensions() {
            ["tif"]
        }

        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof GeoTiffFormat) {
                new GeoTIFF(source)
            }
        }

        @Override
        protected Format createFromFile(File file) {
            new GeoTIFF(file)
        }

    }
}

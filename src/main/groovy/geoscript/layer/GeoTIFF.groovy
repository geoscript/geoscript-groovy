package geoscript.layer

import org.geotools.gce.geotiff.GeoTiffFormat

/**
 * A Format that can read and write GeoTIFFs.
 * <p>You can read a GeoTIFF.</p>
 * <p><code>GeoTIFF tiff = new GeoTIFF()</code></p>
 * <p><code>Raster raster  = tiff.read(new File("alki.tif"))</code></p>
 * @author Jared Erickson
 */
class GeoTIFF extends Format {

    /**
     * Create a new GeoTIFF
     */
    GeoTIFF() {
        super(new GeoTiffFormat())
    }
}

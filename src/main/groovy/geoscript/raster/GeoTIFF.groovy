package geoscript.raster

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.geotiff.GeoTiffFormat
import org.geotools.gce.geotiff.GeoTiffReader
import org.geotools.coverage.grid.io.imageio.IIOMetadataDumper
import org.geotools.coverage.grid.AbstractGridCoverage

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

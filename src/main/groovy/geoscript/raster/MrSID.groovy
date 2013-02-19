package geoscript.raster

import geoscript.proj.Projection
import org.geotools.coverageio.gdal.mrsid.MrSIDFormat

/**
 * A Format that can read and write MrSID Rasters.
 * @author Jared Erickson
 */
class MrSID extends Format {

    /**
     * Create a new MrSID Format
     */
    MrSID() {
        super(new MrSIDFormat())
    }
}
package geoscript.raster

import geoscript.proj.Projection
import org.geotools.coverageio.gdal.mrsid.MrSIDFormat

/**
 * A MrSID Raster
 */
class MrSID extends Raster {

    /**
     * Create a new MrSID Raster from a File
     * @param file The MrSID file
     * @param proj The optional Projection
     */
    MrSID(File file, Projection proj = null) {
        super(new MrSIDFormat(), file, proj)
    }
}
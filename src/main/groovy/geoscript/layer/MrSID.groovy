package geoscript.layer

import org.geotools.coverageio.gdal.mrsid.MrSIDFormat

/**
 * A Format that can read and write MrSID Rasters.
 * @author Jared Erickson
 */
class MrSID extends Format {

    /**
     * Create a new MrSID Format
     * @param stream The file
     */
    MrSID(def stream) {
        super(new MrSIDFormat(), stream)
    }
}
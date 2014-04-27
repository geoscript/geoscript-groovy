package geoscript.layer

import org.geotools.coverage.io.netcdf.NetCDFFormat
import org.opengis.coverage.grid.GridCoverageReader

/**
 * The NetCDF Format to read and write NetCFD Rasters.
 * @author Jared Erickson
 */
class NetCDF extends Format {

    /**
     * Create a new NetCFD
     * @param connectionParams The netcdf file
     */
    NetCDF(def connectionParams) {
        super(new NetCDFFormat(), connectionParams)
    }

}

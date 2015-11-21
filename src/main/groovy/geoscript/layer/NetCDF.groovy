package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.coverage.io.netcdf.NetCDFFormat

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

    /**
     * The NetCDF FormatFactory
     */
    static class Factory extends FormatFactory<NetCDF> {

        @Override
        protected List<String> getFileExtensions() {
            ["nc"]
        }

        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof NetCDFFormat) {
                new NetCDF(source)
            }
        }

        @Override
        protected Format createFromFile(File file) {
            new NetCDF(file)
        }
    }

}

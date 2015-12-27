package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
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

    /**
     * The MrSID FormatFactory
     */
    static class Factory extends FormatFactory<MrSID> {

        @Override
        protected List<String> getFileExtensions() {
            ["sid"]
        }

        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof MrSIDFormat) {
                new MrSID(source)
            }
        }

        @Override
        protected Format createFromFile(File file) {
            new MrSID(file)
        }

    }
}
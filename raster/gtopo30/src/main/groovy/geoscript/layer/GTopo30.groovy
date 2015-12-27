package geoscript.layer

import geoscript.layer.Format
import geoscript.layer.FormatFactory
import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.gtopo30.GTopo30Format

/**
 * A Format that can read and write GTopo30 Rasters.
 * @author Jared Erickson
 */
class GTopo30 extends Format {

    /**
     * Create a new GTopo30 Format
     * @param stream
     */
    GTopo30(def stream) {
        super(new GTopo30Format(), stream)
    }

    /**
     * The GTopo30 FormatFactory
     */
    static class Factory extends FormatFactory<GTopo30> {
        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof GTopo30Format) {
                new GTopo30(source)
            }
        }
    }
}

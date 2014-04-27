package geoscript.layer

import org.geotools.gce.gtopo30.GTopo30Format

/**
 * A Format that can read and write GTopo30 Rasters.
 * @author Jared Erickson
 */
class GTopo30 extends Format {

    /**
     * Create a new GTopo30 Format
     */
    @Deprecated
    GTopo30 () {
        super(new GTopo30Format())
    }

    /**
     * Create a new GTopo30 Format
     * @param stream
     */
    GTopo30 (def stream) {
        super(new GTopo30Format(), stream)
    }
}

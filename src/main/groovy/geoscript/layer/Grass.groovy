package geoscript.layer

import org.geotools.gce.grassraster.format.GrassCoverageFormat

/**
 * A Format that can read and write Grass Rasters.
 * @author Jared Erickson
 */
class Grass extends Format {

    /**
     * Create a new Grass Format
     * @param stream The file
     */
    Grass(def stream) {
        super(new GrassCoverageFormat(), stream)
    }
}

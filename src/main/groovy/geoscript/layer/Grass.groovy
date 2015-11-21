package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
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

    /**
     * The Grass FormatFactory
     */
    static class Factory extends FormatFactory<Grass> {

        @Override
        protected List<String> getFileExtensions() {
            ["arx"]
        }

        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof GrassCoverageFormat) {
                new Grass(source)
            }
        }

        @Override
        protected Format createFromFile(File file) {
            new Grass(file)
        }

    }
}

package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.imagemosaic.ImageMosaicFormat

/**
 * A Format that can read and write image Mosaics.
 * @author Jared Erickson
 */
class Mosaic extends Format {

    /**
     * Create a new image Mosaic Format
     * @param stream The file
     */
    Mosaic(def stream) {
        super(new ImageMosaicFormat(), stream)
    }

    /**
     * The Mosaic FormatFactory
     */
    static class Factory extends FormatFactory<Mosaic> {
        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof ImageMosaicFormat) {
                new Mosaic(source)
            }
        }
    }
}
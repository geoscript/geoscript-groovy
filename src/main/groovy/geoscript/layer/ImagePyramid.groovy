package geoscript.layer

import org.geotools.coverage.grid.io.AbstractGridFormat
import org.geotools.gce.imagepyramid.ImagePyramidFormat

/**
 * A Format that can read and write ImagePyramids.
 * @author Jared Erickson
 */
class ImagePyramid extends Format {

    /**
     * Create a new ImagePyramid Format
     * @param stream The file
     */
    ImagePyramid(def stream) {
        super(new ImagePyramidFormat(), stream)
    }

    /**
     * The ImagePyramid FormatFactory
     */
    static class Factory extends FormatFactory<ImagePyramid> {
        @Override
        protected Format createFromFormat(AbstractGridFormat gridFormat, Object source) {
            if (gridFormat instanceof ImagePyramidFormat) {
                new ImagePyramid(source)
            }
        }
    }
}

package geoscript.layer

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
}

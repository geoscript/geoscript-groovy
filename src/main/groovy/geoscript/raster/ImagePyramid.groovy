package geoscript.raster

import org.geotools.gce.imagepyramid.ImagePyramidFormat

/**
 * A Format that can read and write ImagePyramids.
 * @author Jared Erickson
 */
class ImagePyramid extends Format {

    /**
     * Create a new ImagePyramid Format
     */
    ImagePyramid() {
        super(new ImagePyramidFormat())
    }
}

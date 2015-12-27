package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module for adding methods to the Pyramid class.
 */
class GdalTmsPyramidExtensionModule {

    /**
     * Get this Pyramid as a GDAL TMS XML String
     * @return A GDAL TMS XML String
     */
    static String getGdalTms(Pyramid pyramid) {
        new GdalTmsPyramidWriter().write(pyramid)
    }

}

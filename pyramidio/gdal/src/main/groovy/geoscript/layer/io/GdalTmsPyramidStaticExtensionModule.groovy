package geoscript.layer.io

import geoscript.layer.Pyramid

/**
 * A Groovy Extension Module that adds static methods to the Pyramid class.
 * @author Jared Erickson
 */
class GdalTmsPyramidStaticExtensionModule {

    /**
     * Create a Pyramid from a GDAL TMS XML String
     * @param xml The GDAL TMS XML String
     * @return A Pyramid
     */
    static Pyramid fromGdalTms(Pyramid pyramid, String xml) {
        new GdalTmsPyramidReader().read(xml)
    }

}

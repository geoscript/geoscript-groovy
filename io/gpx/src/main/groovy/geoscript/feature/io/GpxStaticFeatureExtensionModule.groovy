package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class GpxStaticFeatureExtensionModule {

    /**
     * Read a Feature from a GPX String
     * @param feature The Feature
     * @param str The GPX String
     * @return A Feature
     */
    static Feature fromGpx(Feature feature, String str) {
        new GpxReader().read(str)
    }
}

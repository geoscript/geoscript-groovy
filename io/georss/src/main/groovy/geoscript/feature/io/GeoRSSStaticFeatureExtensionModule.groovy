package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class GeoRSSStaticFeatureExtensionModule {

    /**
     * Read a Feature from a GeoRSS String
     * @param feature The Feature
     * @param str The GeoRSS String
     * @return A Feature
     */
    static Feature fromGeoRSS(Feature feature, String str) {
        new GeoRSSReader().read(str)
    }

}

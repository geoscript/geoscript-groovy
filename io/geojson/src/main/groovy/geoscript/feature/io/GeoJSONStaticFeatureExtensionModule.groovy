package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class GeoJSONStaticFeatureExtensionModule {

    /**
     * Read a Feature from a GeoJSON String
     * @param feature The Feature
     * @param str The GeoJSON String
     * @return A Feature
     */
    static Feature fromGeoJSON(Feature feature, String str) {
        new GeoJSONReader().read(str)
    }

}

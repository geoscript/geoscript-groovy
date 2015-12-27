package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class GeoJSONFeatureExtensionModule {

    /**
     * Get this Feature as a GeoJSON String
     * @param feature The Feature
     * @return The GeoJSON String
     */
    static String getGeoJSON(Feature feature) {
        def writer = new geoscript.feature.io.GeoJSONWriter()
        writer.write(feature)
    }

}

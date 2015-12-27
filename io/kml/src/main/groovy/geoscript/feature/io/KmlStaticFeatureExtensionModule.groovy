package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class KmlStaticFeatureExtensionModule {

    /**
     * Read a Feature from a KML String
     * @param feature The Feature
     * @param options The optional named parameters
     * @param str The KML String
     * @return A Feature
     */
    static Feature fromKml(Feature feature, Map options = [:], String str) {
        new KmlReader().read(options, str)
    }

}

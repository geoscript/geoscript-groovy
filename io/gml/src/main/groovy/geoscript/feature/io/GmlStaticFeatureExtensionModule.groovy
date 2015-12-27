package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class GmlStaticFeatureExtensionModule {

    /**
     * Read a Feature from a GML String
     * @param feature The Feature
     * @param options The optional named parameters
     * @param str The GML String
     * @return A Feature
     */
    static Feature fromGml(Feature feature, Map options = [:], String str) {
        double version = options.get("version", 2)
        new GmlReader().read(str, version)
    }
}

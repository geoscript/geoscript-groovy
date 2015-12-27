package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class GeobufStaticFeatureExtensionModule {

    /**
     * Read a Feature from a GeoBuf String
     * @param feature The Feature
     * @param str The GeoBuf String
     * @return A Feature
     */
    static Feature fromGeobuf(Feature feature, String str) {
        new GeobufReader().read(str)
    }

    /**
     * Read a Feature from a GeoBuf byte array
     * @param feature The Feature
     * @param bytes The GeoBuf byte array
     * @return A Feature
     */
    static Feature fromGeobuf(Feature feature, byte[] bytes) {
        new GeobufReader().read(bytes)
    }

    /**
     * Read a Feature from a GeoBuf InputStream
     * @param feature The Feature
     * @param str The GeoBuf InputStream
     * @return A Feature
     */
    static Feature fromGeobuf(Feature feature, InputStream inputStream) {
        new GeobufReader().read(inputStream)
    }
}

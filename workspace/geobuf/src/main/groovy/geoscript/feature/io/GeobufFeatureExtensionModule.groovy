package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds methods to the Feature class.
 * @author Jared Erickson
 */
class GeobufFeatureExtensionModule {

    /**
     * Get this Feature as a Geobuf hex string
     * @param feature The Feature
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     * @return A Geobuf hex string
     */
    static String getGeobuf(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeobufWriter(options)
        writer.write(feature)
    }

    /**
     * Get this Feature as a Geobuf byte array
     * @param feature The Feature
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     * @return A Geobuf byte arary
     */
    static byte[] getGeobufBytes(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeobufWriter(options)
        writer.writeBytes(feature)
    }

}

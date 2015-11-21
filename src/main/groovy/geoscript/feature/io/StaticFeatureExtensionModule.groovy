package geoscript.feature.io

import geoscript.feature.Feature

/**
 * A Groovy Extension Module that adds static methods to the Feature class.
 * @author Jared Erickson
 */
class StaticFeatureExtensionModule {

    /**
     * Read a Feature from a GeoJSON String
     * @param feature The Feature
     * @param str The GeoJSON String
     * @return A Feature
     */
    static Feature fromGeoJSON(Feature feature, String str) {
        new GeoJSONReader().read(str)
    }

    /**
     * Read a Feature from a GeoRSS String
     * @param feature The Feature
     * @param str The GeoRSS String
     * @return A Feature
     */
    static Feature fromGeoRSS(Feature feature, String str) {
        new GeoRSSReader().read(str)
    }

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

    /**
     * Read a Feature from a GPX String
     * @param feature The Feature
     * @param str The GPX String
     * @return A Feature
     */
    static Feature fromGpx(Feature feature, String str) {
        new GpxReader().read(str)
    }

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

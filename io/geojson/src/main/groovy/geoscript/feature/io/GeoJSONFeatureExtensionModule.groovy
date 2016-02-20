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
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @return The GeoJSON String
     */
    static String getGeoJSON(Feature feature, Map options = [:]) {
        def writer = new geoscript.feature.io.GeoJSONWriter()
        writer.write(options, feature)
    }

}

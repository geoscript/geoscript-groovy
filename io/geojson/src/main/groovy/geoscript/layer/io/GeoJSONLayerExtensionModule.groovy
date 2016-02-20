package geoscript.layer.io

import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class GeoJSONLayerExtensionModule {

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toJSON(Layer layer, OutputStream out = System.out) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write([:], layer, out)
    }

    /**
     * Write the Layer as GeoJSON to an OutputStream
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param out The OutputStream (defaults to System.out)
     */
    static void toJSON(Layer layer, Map options, OutputStream out = System.out) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer, out)
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param file The File
     */
    static void toJSONFile(Layer layer, Map options = [:], File file) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer, file)
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param layer The Layer
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param out A GeoJSON String
     */
    static String toJSONString(Layer layer, Map options = [:]) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(options, layer)
    }
}

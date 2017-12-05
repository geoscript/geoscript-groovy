package geoscript.layer.io

import geoscript.layer.Layer
import groovy.json.JsonOutput
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON

/**
 * Write a {@link geoscript.layer.Layer Layer} to a GeoJSON InputStream, File, or String.
 * <p><blockquote><pre>
 * def layer = new Shapefile("states.shp")
 * GeoJSONWriter writer = new GeoJSONWriter()
 * String json = writer.write(layer)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoJSONWriter implements Writer {

    /**
     * Write the Layer to the OutputStream
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     *     <li> prettyPrint = Whether to pretty print the json or not </li>
     * </ol>
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Map options = [:], Layer layer, OutputStream out) {
        int numberOfDecimals = options.get("decimals", 4)
        boolean encodeFeatureBounds = options.get("encodeFeatureBounds", false)
        boolean encodeFeatureCollectionBounds = options.get("encodeFeatureCollectionBounds", false)
        boolean encodeFeatureCollectionCRS = options.get("encodeFeatureCollectionCRS", false)
        boolean encodeFeatureCRS = options.get("encodeFeatureCRS", false)
        boolean encodeNullValues = options.get("encodeNullValues", false)
        GeometryJSON geometryJSON = new GeometryJSON(numberOfDecimals)
        FeatureJSON featureJSON = new FeatureJSON(geometryJSON)
        featureJSON.encodeFeatureBounds = encodeFeatureBounds
        featureJSON.encodeFeatureCollectionBounds = encodeFeatureCollectionBounds
        featureJSON.encodeFeatureCollectionCRS = encodeFeatureCollectionCRS
        featureJSON.encodeFeatureCRS = encodeFeatureCRS
        featureJSON.encodeNullValues = encodeNullValues
        if (options.prettyPrint) {
            StringWriter writer = new StringWriter()
            featureJSON.writeFeatureCollection(layer.fs.features, writer)
            String json = JsonOutput.prettyPrint(writer.toString())
            out.withWriter { java.io.Writer w ->
                w.write(json)
            }
        } else {
            featureJSON.writeFeatureCollection(layer.fs.features, out)
        }
    }

    /**
     * Write the Layer to the File
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     *     <li> prettyPrint = Whether to pretty print the json or not </li>
     * </ol>
     * @param layer The Layer
     * @param file The File
     */
    void write(Map options = [:], Layer layer, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(options, layer, out)
        out.close()
    }

    /**
     * Write the Layer to a String
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     *     <li> prettyPrint = Whether to pretty print the json or not </li>
     * </ol>
     * @param layer The Layer
     * @return A String
     */
    String write(Map options = [:], Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(options, layer, out);
        out.close()
        return out.toString()
    }
}
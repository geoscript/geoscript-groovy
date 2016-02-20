package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.geojson.feature.FeatureJSON
import org.geotools.geojson.geom.GeometryJSON

/**
 * Write a Feature to GeoJSON.
 * <p><blockquote><pre>
 * {@link geoscript.feature.Schema Schema} schema = new {@link geoscript.feature.Schema Schema}("houses", [new {@link geoscript.feature.Field Field}("geom","Point"), new {@link geoscript.feature.Field Field}("name","string"), new {@link geoscript.feature.Field Field}("price","float")])
 * {@link geoscript.feature.Feature Feature} feature = new {@link geoscript.feature.Feature Feature}([new {@link geoscript.geom.Point Point}(111,-47), "House", 12.5], "house1", schema)
 * GeoJSONWriter writer = new GeoJSONWriter()
 * String json = writer.write(feature)
 *
 * {"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"house1"}
 * </pre></blockquote><p>
 * @author Jared Erickson
 */
class GeoJSONWriter implements Writer {

    /**
     * Write a Feature to a GeoJSON String.
     * @param options Optional named parameters:
     * <ol>
     *     <li> decimals = The number of decimals (defaults to 4) </li>
     *     <li> encodeFeatureBounds = Whether to encode Feature Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionBounds = Whether to encode FeatureCollection Bounds (default is false) </li>
     *     <li> encodeFeatureCollectionCRS = Whether to encode FeatureCollection CRS (default is false) </li>
     *     <li> encodeFeatureCRS = Whether to encode Feature CRS (default is false) </li>
     *     <li> encodeNullValues = Whether to encode null values (default is false) </li>
     * </ol>
     * @param feature The Feature
     * @return A GeoJSON String
     */
    String write(Map options = [:], Feature feature) {
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
        featureJSON.toString(feature.f)
    }
}
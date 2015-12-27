package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.geojson.feature.FeatureJSON

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
     * The GeoTools FeatureJSON reader/writer
     */
    private static final FeatureJSON featureJSON = new FeatureJSON()

     /**
     * Write a Feature to a GeoJSON String
     * @param feature The Feature
     * @return A GeoJSON String
     */
    String write(Feature feature) {
        featureJSON.toString(feature.f)
    }
}

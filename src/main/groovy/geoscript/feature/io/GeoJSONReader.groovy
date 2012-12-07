package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.geojson.feature.FeatureJSON

/**
 * Read a Feature from a GeoJSON String.
 * <p><blockquote><pre>
 * String geojson = """{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"house1"}"""
 * GeoJSONReader reader = new GeoJSONReader()
 * {@link geoscript.feature.Feature Feature} actual = reader.read(geojson)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class GeoJSONReader implements Reader {

    /**
     * The GeoTools FeatureJSON reader/writer
     */
    private static final FeatureJSON featureJSON = new FeatureJSON()

    /**
     * Read a Feature from a GeoJSON String.
     * @param str The GeoJSON String
     * @return A Feature
     */
    Feature read(String str) {
        new Feature(featureJSON.readFeature(str))
    }
}

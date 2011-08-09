package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.geojson.feature.FeatureJSON

/**
 * Read a Feature from a GeoJSON String.
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

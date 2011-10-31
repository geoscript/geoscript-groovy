package geoscript.feature.io

import geoscript.feature.Feature
import org.geotools.geojson.feature.FeatureJSON

/**
 * Write a Feature to GeoJSON.
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

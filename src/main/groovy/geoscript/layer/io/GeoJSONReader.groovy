package geoscript.layer.io

import geoscript.layer.Layer
import org.geotools.geojson.feature.FeatureJSON

/**
 * Read a GeoScript Layer from a GeoJSON InputStream, File, or String.
 * @author Jared Erickson
 */
class GeoJSONReader implements Reader {

    /**
     * The GeoTools FeatureJSON reader/writer
     */
    private static final FeatureJSON featureJSON = new FeatureJSON()

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    Layer read(InputStream input) {
        new Layer(featureJSON.readFeatureCollection(input))
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    Layer read(File file) {
        read(new FileInputStream(file))
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    Layer read(String str) {
        read(new ByteArrayInputStream(str))
    }

}

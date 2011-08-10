package geoscript.layer.io

import geoscript.layer.Layer
import org.geotools.geojson.feature.FeatureJSON

/**
 * Write a GeoScript Layer to a GeoJSON InputStream, File, or String.
 * @author Jared Erickson
 */
class GeoJSONWriter implements Writer {

    /**
     * The GeoTools FeatureJSON reader/writer
     */
    private static final FeatureJSON featureJSON = new FeatureJSON()

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    void write(Layer layer, OutputStream out) {
       featureJSON.writeFeatureCollection(layer.fs, out)
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    void write(Layer layer, File file) {
        FileOutputStream out = new FileOutputStream(file)
        write(layer, out)
        out.close()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    String write(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        write(layer, out);
        out.close()
        return out.toString()
    }

}

package geoscript.layer.io

import geoscript.layer.Layer
import org.geotools.geojson.feature.FeatureJSON

/**
 * Read a {@geoscript.layer.Layer Layer} from a GeoJSON InputStream, File, or String.
 * <p><blockquote><pre>
 * String json = """{"type":"FeatureCollection","features":[
 * {"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},
 * "properties":{"name":"House","price":12.5},"id":"fid-3eff7fce_131b538ad4c_-8000"},
 * {"type":"Feature","geometry":{"type":"Point","coordinates":[121,-45]},
 * "properties":{"name":"School","price":22.7},"id":"fid-3eff7fce_131b538ad4c_-7fff"}]}"""
 * GeoJSONReader reader = new GeoJSONReader()
 * Layer layer = reader.read(json)
 * </pre></blockquote></p>
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
        def featureCollection = featureJSON.readFeatureCollection(input)
        geoscript.workspace.Workspace ws = new geoscript.workspace.Memory()
        ws.ds.addFeatures(featureCollection)
        return ws.layers[0]
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
        read(new ByteArrayInputStream(str.getBytes("UTF-8")))
    }

}

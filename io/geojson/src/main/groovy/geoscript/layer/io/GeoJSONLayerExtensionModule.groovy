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
        geoJSONWriter.write(layer, out)
    }

    /**
     * Write the Layer as GeoJSON to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toJSONFile(Layer layer, File file) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(layer, file)
    }

    /**
     * Write the Layer as GeoJSON to a String
     * @param layer The Layer
     * @param out A GeoJSON String
     */
    static String toJSONString(Layer layer) {
        GeoJSONWriter geoJSONWriter = new GeoJSONWriter()
        geoJSONWriter.write(layer)
    }
}

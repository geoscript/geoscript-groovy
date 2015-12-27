package geoscript.layer.io

import geoscript.layer.Layer
import groovy.xml.StreamingMarkupBuilder
import groovy.xml.XmlUtil

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class GmlLayerExtensionModule {

    /**
     * Write the Layer as GML to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGML(Layer layer, OutputStream out = System.out) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer, out)
    }

    /**
     * Write the Layer as GML to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGMLFile(Layer layer, File file) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer, file)
    }

    /**
     * Write the Layer as GML to a String
     * @param layer The Layer
     * @param out A GML String
     */
    static String toGMLString(Layer layer) {
        GmlWriter gmlWriter = new GmlWriter()
        gmlWriter.write(layer)
    }
}

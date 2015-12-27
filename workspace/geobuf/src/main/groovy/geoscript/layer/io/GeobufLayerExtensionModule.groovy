package geoscript.layer.io

import geoscript.layer.Layer

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class GeobufLayerExtensionModule {

    /**
     * Write the Layer as Geobuf to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGeobuf(Layer layer, OutputStream out = System.out) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer, out)
    }

    /**
     * Write the Layer as Geobuf to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGeobufFile(Layer layer, File file) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer, file)
    }

    /**
     * Write the Layer as Geobuf to a String
     * @param layer The Layer
     * @param out A Geobuf Hex String
     */
    static String toGeobufString(Layer layer) {
        GeobufWriter writer = new GeobufWriter()
        writer.write(layer)
    }

    /**
     * Write the Layer as Geobuf to a byte array
     * @param layer The Layer
     * @param out A Geobuf byte array
     */
    static byte[] toGeobufBytes(Layer layer) {
        GeobufWriter writer = new GeobufWriter()
        writer.writeBytes(layer)
    }

}

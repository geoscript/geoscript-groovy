package geoscript.layer.io

import geoscript.layer.Layer

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class GpxLayerExtensionModule {

    /**
     * Write the Layer as Gpx to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGpx(Layer layer, OutputStream out = System.out) {
        GpxWriter writer = new GpxWriter()
        writer.write(layer, out)
    }

    /**
     * Write the Layer as Gpx to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGpxFile(Layer layer, File file) {
        GpxWriter writer = new GpxWriter()
        writer.write(layer, file)
    }

    /**
     * Write the Layer as Gpx to a String
     * @param layer The Layer
     * @param out A Gpx String
     */
    static String toGpxString(Layer layer) {
        GpxWriter writer = new GpxWriter()
        writer.write(layer)
    }
    
}

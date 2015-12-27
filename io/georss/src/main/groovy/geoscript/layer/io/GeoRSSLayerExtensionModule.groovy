package geoscript.layer.io

import geoscript.layer.Layer

/**
 * A Groovy Extension Module that adds methods to the Layer class.
 * @author Jared Erickson
 */
class GeoRSSLayerExtensionModule {

    /**
     * Write the Layer as GeoRSS to an OutputStream
     * @param layer The Layer
     * @param out The OutputStream (defaults to System.out)
     */
    static void toGeoRSS(Layer layer, OutputStream out = System.out) {
        GeoRSSWriter writer = new GeoRSSWriter()
        writer.write(layer, out)
    }

    /**
     * Write the Layer as GeoRSS to a File
     * @param layer The Layer
     * @param file The File
     */
    static void toGeoRSSFile(Layer layer, File file) {
        GeoRSSWriter writer = new GeoRSSWriter()
        writer.write(layer, file)
    }

    /**
     * Write the Layer as GeoRSS to a String
     * @param layer The Layer
     * @param out A GeoRSS String
     */
    static String toGeoRSSString(Layer layer) {
        GeoRSSWriter writer = new GeoRSSWriter()
        writer.write(layer)
    }
    
}

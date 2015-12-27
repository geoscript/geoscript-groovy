package geoscript.layer.io

import geoscript.layer.Layer
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufFeature
import org.geotools.data.geobuf.GeobufFeatureCollection
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Write a Layer to a Geobuf encoded protocol buffer.
 * @author Jared Erickson
 */
class GeobufWriter implements Writer {

    /**
     * The Geobuf FeatureCollection decoder
     */
    GeobufFeatureCollection geobufFeatureCollection

    /**
     * Create a new GeobufWriter
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     */
    GeobufWriter(Map options = [:]) {
        geobufFeatureCollection = new GeobufFeatureCollection(
                new GeobufFeature(new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2)))
        )
    }

    /**
     * Write the Layer to the OutputStream
     * @param layer The Layer
     * @param out The OutputStream
     */
    @Override
    void write(Layer layer, OutputStream out) {
        geobufFeatureCollection.encode(layer.fs.features, out)
    }

    /**
     * Write the Layer to the File
     * @param layer The Layer
     * @param file The File
     */
    @Override
    void write(Layer layer, File file) {
        OutputStream out = new FileOutputStream(file)
        try {
            write(layer, out)
        } finally {
            out.close()
        }
    }

    /**
     * Write the Layer to an array of bytes
     * @param layer The Layer
     * @return An array of bytes
     */
    byte[] writeBytes(Layer layer) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        try {
            write(layer, out)
        } finally {
            out.close()
        }
        out.toByteArray()
    }

    /**
     * Write the Layer to a String
     * @param layer The Layer
     * @return A String
     */
    @Override
    String write(Layer layer) {
        String.valueOf(Hex.encodeHex(writeBytes(layer)))
    }
}

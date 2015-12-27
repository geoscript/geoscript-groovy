package geoscript.feature.io

import geoscript.feature.Feature
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufFeature
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Write a Feature to a Geobuf encoded protocol buffer
 * @author Jared Erickson
 */
class GeobufWriter implements Writer {

    /**
     * The Geobuf Feature Encoder
     */
    private GeobufFeature geobufFeature

    /**
     * Create a new GeobufWriter
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     */
    GeobufWriter(Map options = [:]) {
        geobufFeature = new GeobufFeature(new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2)))
    }

    /**
     * Write a Feature to a String
     * @param feature The Feature
     * @return A String
     */
    @Override
    String write(Feature feature) {
        new String(Hex.encodeHex(writeBytes(feature)))
    }

    /**
     * Write a Feature to a String
     * @param feature The Feature
     * @return A String
     */
    byte[] writeBytes(Feature feature) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        try {
            write(feature, out)
        } finally {
            out.close()
        }
        out.toByteArray()
    }

    /**
     * Write a Feature to the OutputStream
     * @param feature The Feature
     * @param out The OutputStream
     */
    void write(Feature feature, OutputStream out) {
        geobufFeature.encode(feature.f, out)
    }
}

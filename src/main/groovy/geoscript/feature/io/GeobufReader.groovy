package geoscript.feature.io

import geoscript.feature.Feature
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufFeature
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Read a Feature from a Geobuf encoded protocol buffer.
 * @author Jared Erickson
 */
class GeobufReader implements Reader {

    /**
     * The Geobuf Feature Encoder
     */
    private GeobufFeature geobufFeature

    /**
     * Create a new GeobufReader
     * @param options Optional named parameters:
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     */
    GeobufReader(Map options = [:]) {
        geobufFeature = new GeobufFeature(new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2)))
    }

    /**
     * Read a Feature from a String.
     * @param str The String
     * @return A Feature
     */
    @Override
    Feature read(String str) {
        read(Hex.decodeHex(str.toCharArray()))
    }

    /**
     * Read a Feature from an array of bytes
     * @param bytes An array of bytes
     * @return A Feature
     */
    Feature read(byte[] bytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)
        try {
            read(inputStream)
        } finally {
            inputStream.close()
        }
    }

    /**
     * Read a Feature from an InputStream
     * @param inputStream The InputStream
     * @return A Feature
     */
    Feature read(InputStream inputStream) {
        new Feature(geobufFeature.decode(inputStream))
    }

}

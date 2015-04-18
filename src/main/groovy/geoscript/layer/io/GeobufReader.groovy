package geoscript.layer.io

import geoscript.layer.Layer
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufFeature
import org.geotools.data.geobuf.GeobufFeatureCollection
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Read a Layer from a Geobuf encoded protocol buffer.
 * @author Jared Erickson
 */
class GeobufReader implements Reader {

    /**
     * The Geobuf FeatureCollection decoder
     */
    GeobufFeatureCollection geobufFeatureCollection

    /**
     * Create a new GeobufReader
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     */
    GeobufReader(Map options = [:]) {
        geobufFeatureCollection = new GeobufFeatureCollection(
                new GeobufFeature(new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2)))
        )
    }

    /**
     * Read a GeoScript Layer from an InputStream
     * @param input An InputStream
     * @return A GeoScript Layer
     */
    @Override
    Layer read(InputStream input) {
        new Layer(geobufFeatureCollection.decode(input))
    }

    /**
     * Read a GeoScript Layer from a File
     * @param file A File
     * @return A GeoScript Layer
     */
    @Override
    Layer read(File file) {
        InputStream inputStream = new FileInputStream(file)
        try {
            read(inputStream)
        } finally {
            inputStream.close()
        }
    }

    /**
     * Read a GeoScript Layer from a String
     * @param str A String
     * @return A GeoScript Layer
     */
    @Override
    Layer read(String str) {
        InputStream inputStream = new ByteArrayInputStream(Hex.decodeHex(str.toCharArray()))
        try {
            read(inputStream)
        } finally {
            inputStream.close()
        }
    }

    /**
     * Read a GeoScript Layer from an array of bytes
     * @param bytes An array of bytes
     * @return A GeoScript Layer
     */
    Layer read(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes)
        try {
            read(inputStream)
        } finally {
            inputStream.close()
        }
    }
}

package geoscript.geom.io

import geoscript.geom.Geometry
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Write a Geometry to a Geobuf encoded protocol buffer.
 * @author Jared Erickson
 */
class GeobufWriter implements Writer {

    /**
     * The Geobuf Geometry Encoder
     */
    private GeobufGeometry geobufGeometry

    /**
     * Create a new GeobufWriter
     * @param options The optional named parameters
     * <ul>
     *     <li> precision = The maximum precision (defaults to 6) </li>
     *     <li> dimension = The supported geometry coordinates dimension (defaults to 2) </li>
     * </ul>
     */
    GeobufWriter(Map options = [:]) {
        geobufGeometry = new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2))
    }

    /**
     * Write the Geometry to Geobuf hex String
     * @param geom The Geometry
     * @return A Geobuf hex String
     */
    String write(Geometry geom) {
        new String(Hex.encodeHex(writeBytes(geom)))
    }

    /**
     * Write the Geometry to Geobuf byte array
     * @param geom The Geometry
     * @return A Geobuf byte array
     */
    byte[] writeBytes(Geometry geom) {
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        try {
            geobufGeometry.encode(geom.g, out)
        } finally {
            out.close()
        }
        out.toByteArray()
    }
}

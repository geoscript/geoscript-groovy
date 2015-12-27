package geoscript.geom.io

import geoscript.geom.Geometry
import org.apache.commons.codec.binary.Hex
import org.geotools.data.geobuf.GeobufGeometry

/**
 * Read a Geometry from a Geobuf encoded hex String or byte array
 * @author Jared Erickson
 */
class GeobufReader implements Reader {

    /**
     * The Geobuf Geometry decoder
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
    GeobufReader(Map options = [:]) {
        geobufGeometry = new GeobufGeometry(options.get("precision", 6), options.get("dimension", 2))
    }

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        read(Hex.decodeHex(str.toCharArray()))
    }

    /**
     * Read a Geometry from a byte array
     * @param bytes The byte array
     * @return A Geometry
     */
    Geometry read(byte[] bytes) {
        InputStream input = new ByteArrayInputStream(bytes)
        try {
            Geometry.wrap(geobufGeometry.decode(input))
        } finally {
            input.close()
        }
    }

}

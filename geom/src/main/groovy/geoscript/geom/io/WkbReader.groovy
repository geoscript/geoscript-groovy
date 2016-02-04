package geoscript.geom.io

import com.vividsolutions.jts.io.WKBReader
import geoscript.geom.Geometry

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a WKB hex String or byte array.
 * <p><blockquote><pre>
 * WkbReader reader = new WkbReader()
 * {@link geoscript.geom.Point Point} pt = reader.read("0000000001405BC00000000000C047800000000000")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class WkbReader implements Reader {

    /**
     * The JTS WKBReader
     */
    private final static WKBReader reader = new WKBReader()

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        read(WKBReader.hexToBytes(str))
    }

    /**
     * Read a Geometry from a byte array
     * @param bytes The byte array
     * @return A Geometry
     */
    Geometry read(byte[] bytes) {
        Geometry.wrap(reader.read(bytes))
    }

}

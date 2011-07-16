package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKBReader

/**
 * Read a Geometry from a WKB hex String or byte array.
 * <p><code>WkbReader reader = new WkbReader()</code></p>
 * <p><code>Point pt = reader.read("0000000001405BC00000000000C047800000000000")</code></p>
 * <p><code>POINT (111 -47)</code></p>
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

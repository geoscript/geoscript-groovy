package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKTReader

/**
 * Read a Geometry from a WKT String.
 * <p><code>WktReader reader = new WktReader()</code></p>
 * <p><code>Point pt = reader.read("POINT (111 -47)")</code></p>
 * <p><code>POINT (111 -47)</code></p>
 * @author Jared Erickson
 */
class WktReader implements Reader {

    /**
     * The JTS WKBReader
     */
    private final static WKTReader reader = new WKTReader()

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        Geometry.wrap(reader.read(str))
    }
}

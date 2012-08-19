package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKTReader

/**
 * Read a {@link geoscript.geom.Geometry Geometry} from a WKT String.
 * <p><blockquote><pre>
 * WktReader reader = new WktReader()
 * {@link geoscript.geom.Point Point} pt = reader.read("POINT (111 -47)")
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
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

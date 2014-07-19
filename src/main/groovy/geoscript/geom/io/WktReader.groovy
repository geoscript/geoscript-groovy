package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKTReader
import org.geotools.geometry.jts.WKTReader2

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
     * The JTS WKTReader
     */
    private final static WKTReader reader = new WKTReader()

    /**
     * The GeoTools WKTReader2 that can support Curved Geometry
     */
    private final static WKTReader2 reader2 = new WKTReader2()

    /**
     * Read a Geometry from a String
     * @param str The String
     * @return A Geometry
     */
    Geometry read(String str) {
        if (str.startsWith("MULTIPOINT")) {
            Geometry.wrap(reader.read(str))
        } else {
            Geometry.wrap(reader2.read(str))
        }
    }
}

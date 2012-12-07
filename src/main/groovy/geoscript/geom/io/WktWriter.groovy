package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKTWriter

/**
 * Write a {@link geoscript.geom.Geometry Geometry} to a WKT String.
 * <p><blockquote><pre>
 * WktWriter writer = new WktWriter()
 * String wkt = writer.write(new {@link geoscript.geom.Point Point}(111,-47)
 *
 * POINT (111 -47)
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class WktWriter implements Writer {

    /**
     * The JTS WKTWriter
     */
    private final static WKTWriter writer = new WKTWriter()

    /**
     * Write the Geometry to WKB hex String
     * @param geom The Geometry
     * @return A WKB hex String
     */
    String write(Geometry geom) {
        writer.write(geom.g)
    }
}

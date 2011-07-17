package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKTWriter

/**
 * Write a Geometry to a WKT String.
 * <p><code>WktWriter writer = new WktWriter()</code></p>
 * <p><code>String wkt = writer.write(new Point(111,-47)</code></p>
 * <p><code>POINT (111 -47)</code></p>
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

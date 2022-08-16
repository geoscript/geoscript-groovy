package geoscript.geom.io

import geoscript.geom.Geometry
import org.locationtech.jts.io.WKBWriter
import org.locationtech.jts.io.twkb.TWKBWriter

/**
 * Write a {@link geoscript.geom.Geometry Geometry} to a WKB hex String or byte array.
 * <p><blockquote><pre>
 * WkbWriter writer = new WkbWriter()
 * String wkb = writer.write(new {@link geoscript.geom.Point Point}(111,-47)
 *
 * "0000000001405BC00000000000C047800000000000"
 * </pre></blockquote></p>
 */
class TWkbWriter implements Writer {

    /**
     * The JTS WKBWriter
     */
    private final TWKBWriter writer

    /**
     * Create a new WkbWriter with an output dimension of 2 and big endian byte order.
     */
    TWkbWriter() {
        writer = new TWKBWriter()
    }

    /**
     * Write the Geometry to WKB hex String
     * @param geom The Geometry
     * @return A WKB hex String
     */
    String write(Geometry geom) {
        WKBWriter.toHex(writer.write(geom.g))
    }

    /**
     * Write the Geometry to WKB byte array
     * @param geom The Geometry
     * @return A WKB byte array
     */
    byte[] writeBytes(Geometry geom) {
        writer.write(geom.g)
    }
}

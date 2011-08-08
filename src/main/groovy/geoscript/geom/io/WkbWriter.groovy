package geoscript.geom.io

import geoscript.geom.Geometry
import com.vividsolutions.jts.io.WKBWriter

/**
 * Write a Geometry to a WKB hex String or byte array.
 * <p><code>WkbWriter writer = new WkbWriter()</code></p>
 * <p><code>String wkb = writer.write(new Point(111,-47)</code></p>
 * <p><code>"0000000001405BC00000000000C047800000000000"</code></p>
 */
class WkbWriter implements Writer {

    /**
     * The JTS WKBWriter
     */
    private final WKBWriter writer

    /**
     * The big endian byte order constant
     */
    public static final int BIG_ENDIAN = 1

    /**
     * The little endian byte order constant
     */
    public static final int LITTLE_ENDIAN = 2

    /**
     * Create a new WkbWriter with an output dimension of 2 and big endian byte order.
     */
    WkbWriter() {
        writer = new WKBWriter()
    }

    /**
     * Create a new WkbWriter with an output dimension and byte order.
     * @param outputDimension The output dimension (2 or 3)
     * @param byteOrder The byte order (BIG_ENDIAN or LITTLE_ENDIAN)
     */
    WkbWriter(int outputDimension, int byteOrder) {
        writer = new WKBWriter(outputDimension, byteOrder)
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

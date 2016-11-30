package geoscript.layer

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.apache.commons.codec.binary.Base64

/**
 * A Tile
 * @author Jared Erickson
 */
@EqualsAndHashCode
@ToString(includePackage = false, includeNames = true, excludes = "data")
class Tile {

    /**
     * The column
     */
    long x

    /**
     * The row
     */
    long y

    /**
     * The zoom level
     */
    long z

    /**
     * The data
     */
    byte[] data

    /**
     * Create a new Tile with no data
     * @param z The zoom level
     * @param x The x or column
     * @param y The y or row
     */
    Tile(long z, long x, long y) {
        this.z = z
        this.x = x
        this.y = y
    }

    /**
     * Create a new Tile with data
     * @param z The zoom level
     * @param x The x or column
     * @param y The y or row
     * @param data The array of bytes
     */
    Tile(long z, long x, long y, byte[] data) {
        this.z = z
        this.x = x
        this.y = y
        this.data = data
    }

    /**
     * Get the data as a Base64 encoded string
     * @return A Base64 encoded string
     */
    String getBase64String() {
        if (data == null) {
            null
        } else {
            new String(Base64.encodeBase64(data), "UTF-8")
        }
    }
}

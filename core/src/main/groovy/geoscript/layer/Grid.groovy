package geoscript.layer

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * A Tile Grid.
 * @author Jared Erickson
 */
@EqualsAndHashCode
@ToString(includePackage = false, includeNames = true)
class Grid {

    /**
     * The zoom level
     */
    long z

    /**
     * The width or number of columns
     */
    long width

    /**
     * The height or number of rows
     */
    long height

    /**
     * The number of tiles
     */
    long size

    /**
     * The x resolution
     */
    double xResolution

    /**
     * The y resolution
     */
    double yResolution

    /**
     * Create a new Grid
     * @param z The zoom level
     * @param width The width or number of columns
     * @param height The height or number of rows
     * @param xResolution The x resolution
     * @param yResolution The y resolution
     */
    Grid(long z, long width, long height, double xResolution, double yResolution) {
        this.z = z
        this.width = width
        this.height = height
        this.xResolution = xResolution
        this.yResolution = yResolution
        this.size = this.width * this.height
    }
}

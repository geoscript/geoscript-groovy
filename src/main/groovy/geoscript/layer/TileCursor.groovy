package geoscript.layer

import geoscript.geom.Bounds
import org.geotools.util.logging.Logging
import java.util.logging.Logger

/**
 * A TileCursor provides an easy way to iterate through Tiles.
 * @author Jared Erickson
 */
class TileCursor<T extends Tile> implements Iterator {

    /**
     * The TileLayer that the Tiles comes from
     */
    private final TileLayer tileLayer

    /**
     * The zoom level
     */
    private final long z

    /**
     * The minX or min column
     */
    private final long minX

    /**
     * The minY or min row
     */
    private final long minY

    /**
     * The maxX or max column
     */
    private final long maxX

    /**
     * The maxY or max row
     */
    private final long maxY

    /**
     * The number of columns
     */
    private final long width

    /**
     * The number of rows
     */
    private final long height

    /**
     * The number of tiles
     */
    private final long size

    /**
     * The internal counter used to iterate through Tiles
     */
    private long counter = 0

    /**
     * The internal cached Bounds
     */
    private Bounds bounds

    /**
     * The Logger
     */
    private static final Logger LOGGER = Logging.getLogger("geoscript.layer.TileCursor");

    /**
     * Create a TileCursor that iterates over every Tile in a given zoom level
     * @param layer The TileLayer
     * @param z The zoom level
     */
    TileCursor (TileLayer layer, long z) {
        this(layer, z, 0, 0, layer.pyramid.grid(z).width - 1, layer.pyramid.grid(z).height - 1)
    }

    /**
     * Create TileCursor that iterates over every Tile in a given zoom level between the
     * given columns and rows
     * @param layer The TileLayer
     * @param z The zoom level
     * @param minX The min x or column
     * @param minY The min y or row
     * @param maxX The max x or column
     * @param maxY The max y or row
     */
    TileCursor (TileLayer layer, long z, long minX, long minY, long maxX, long maxY) {
        this.tileLayer = layer
        this.z = z
        if(validate(z, tileLayer.pyramid.grids.collect{it.z}, "z"))
        {
            Grid grid = tileLayer.pyramid.grid(this.z)
            this.minX = validate(minX, 0, grid.width - 1, "minX")
            this.minY = validate(minY, 0, grid.height - 1, "minY")
            this.maxX = validate(maxX, 0, grid.width - 1, "maxX")
            this.maxY = validate(maxY, 0, grid.height - 1, "maxY")
            this.width = (maxX - minX) + 1
            this.height = (maxY - minY) + 1
            this.size = width * height
        } else {
            this.size = 0
        }
    }

    /**
     * Create TileCursor that iterates over every Tile in a given zoom level and within the given Bounds
     * @param layer The TileLayer
     * @param b The Bounds
     * @param z The zoom level
     */
    TileCursor(TileLayer layer, Bounds b, long z) {
        // Limit the Bounds to the intersection of the requested Bounds
        // and the Pyramid's Bounds
        Bounds pyramidBounds = layer.pyramid.bounds
        Bounds intersectedBounds = pyramidBounds.intersection(b)
        // Check that the intersected Bounds is not empty
        if (!intersectedBounds.empty) {
            Grid m = layer.pyramid.grid(z)
            Map tileCoords = layer.getTileCoordinates(intersectedBounds, m)
            this.tileLayer = layer
            this.z = z
            if(validate(z, tileLayer.pyramid.grids.collect{it.z}, "z"))
            {
                Grid grid = tileLayer.pyramid.grid(this.z)
                this.minX = validate(tileCoords.minX, 0, grid.width - 1, "minX")
                this.minY = validate(tileCoords.minY, 0, grid.height - 1, "minY")
                this.maxX = validate(tileCoords.maxX, 0, grid.width - 1, "maxX")
                this.maxY = validate(tileCoords.maxY, 0, grid.height - 1, "maxY")
                this.width = (maxX - minX) + 1
                this.height = (maxY - minY) + 1
                this.size = width * height
            } else {
                this.size = 0
            }
        } else {
            // Cache the bounds as the empty Bounds with the correct projection
            this.bounds = intersectedBounds
            this.size = 0
        }
    }

    /**
     * Create TileCursor that iterates over every Tile within the given Bounds and for the x and y resolutions
     * @param layer The TileLayer
     * @param b The Bounds
     * @param resX The x resolution
     * @param resY The y resolution
     */
    TileCursor(TileLayer layer, Bounds b, double resX, double resY) {
        // Limit the Bounds to the intersection of the requested Bounds
        // and the Pyramid's Bounds
        Bounds pyramidBounds = layer.pyramid.bounds
        Bounds intersectedBounds = pyramidBounds.intersection(b)
        // Check that the intersected Bounds is not empty
        if (!intersectedBounds.empty) {
            Grid m = layer.pyramid.grid(intersectedBounds, resX, resY)
            Map tileCoords = layer.getTileCoordinates(intersectedBounds, m)
            this.tileLayer = layer
            this.z = m.z
            if(validate(m.z, tileLayer.pyramid.grids?.collect{it.z}, "z"))
            {
                Grid grid = tileLayer.pyramid.grid(this.z)
                this.minX = validate(tileCoords.minX, 0, grid.width - 1, "minX")
                this.minY = validate(tileCoords.minY, 0, grid.height - 1, "minY")
                this.maxX = validate(tileCoords.maxX, 0, grid.width - 1, "maxX")
                this.maxY = validate(tileCoords.maxY, 0, grid.height - 1, "maxY")
                this.width = (maxX - minX) + 1
                this.height = (maxY - minY) + 1
                this.size = width * height
            } else {
                this.size = 0
            }
        } else {
            // Cache the bounds as the empty Bounds with the correct projection
            this.bounds = intersectedBounds
            this.size = 0
        }
    }

    /**
     * Create TileCursor that iterates over every Tile within the Bounds for the image size
     * @param layer The TileLayer
     * @param b The Bounds
     * @param w The image width
     * @param h The image height
     */
    TileCursor(TileLayer layer, Bounds b, int w, int h) {
        // Limit the Bounds to the intersection of the requested Bounds
        // and the Pyramid's Bounds
        Bounds pyramidBounds = layer.pyramid.bounds
        Bounds intersectedBounds = pyramidBounds.intersection(b)
        // Check that the intersected Bounds is not empty
        if (!intersectedBounds.empty) {
            Grid m = layer.pyramid.grid(intersectedBounds, w, h)
            Map tileCoords = layer.getTileCoordinates(intersectedBounds, m)
            this.tileLayer = layer
            this.z = m.z
            if(validate(m.z, tileLayer.pyramid.grids?.collect{it.z}, "z"))
            {
                Grid grid = tileLayer.pyramid.grid(this.z)
                this.minX = validate(tileCoords.minX, 0, grid.width - 1, "minX")
                this.minY = validate(tileCoords.minY, 0, grid.height - 1, "minY")
                this.maxX = validate(tileCoords.maxX, 0, grid.width - 1, "maxX")
                this.maxY = validate(tileCoords.maxY, 0, grid.height - 1, "maxY")
                this.width = (maxX - minX) + 1
                this.height = (maxY - minY) + 1
                this.size = width * height
            } else {
                this.size = 0
            }

        } else {
            // Cache the bounds as the empty Bounds with the correct projection
            this.bounds = intersectedBounds
            this.size = 0
        }
    }

    /**
     * Validate number parameters passed into the TileCursor constructor
     * @param num The number value
     * @param min The minimum value
     * @param max The maximum value
     * @param name The name of the value for logging
     * @return The validated value
     */
    private long validate(long num, long min, long max, String name) {
        if (num < min) {
            LOGGER.warning("${name} cannot be less than ${min}!")
            num = min
        } else if (num > max) {
            LOGGER.warning("${name} cannot be greater than ${max}")
            num = max
        }
        num
    }

    /**
     * Validate if the given num is found within a list of values
     * @param num The number value
     * @param list Values to compare if num is in
     * @param name The name of the value for logging
     * @return The validated value
     */
    private Boolean validate(long num, List values, String name) {
        Boolean found = (values.find{it==num} != null)
        if(!found) {
            LOGGER.warning("${name} with value ${num} is not found in the given list ${values}!")
        }
        found
    }

    /**
     * Get the TileLayer
     * @return The TileLayer
     */
    TileLayer getTileLayer() {
        this.tileLayer
    }

    /**
     * Get the zoom level
     * @return The zoom level
     */
    long getZ() {
        this.z
    }

    /**
     * Get the min x or column
     * @return The min x or column
     */
    long getMinX() {
        this.minX
    }

    /**
     * Get the min y or row
     * @return The min y or row
     */
    long getMinY() {
        this.minY
    }

    /**
     * Get the max x or column
     * @return The max x or column
     */
    long getMaxX() {
        this.maxX
    }

    /**
     * Get the max y or row
     * @return The max y or row
     */
    long getMaxY() {
        this.maxY
    }

    /**
     * Get the width or number of columns
     * @return The width or number of columns
     */
    long getWidth() {
        this.width
    }

    /**
     * Get the height or number of rows
     * @return The height or number of rows
     */
    long getHeight() {
        this.height
    }

    /**
     * Get the number of tiles
     * @return The number of tiles
     */
    long getSize() {
        this.size
    }

    /**
     * Get the Bounds of tiles in this TileCursor
     * @return The Bounds
     */
    Bounds getBounds() {
        if (!this.bounds) {
            Bounds min = tileLayer.pyramid.bounds(new Tile(z, minX, minY))
            Bounds max = tileLayer.pyramid.bounds(new Tile(z, maxX, maxY))
            this.bounds = min.expand(max)
        }
        this.bounds
    }

    /**
     * Get the next Tile
     * @return The next Tile
     */
    @Override
    Tile next() {
        long x = minX + counter % width
        long y = minY + Math.floor(counter / width) as long
        counter++
        tileLayer.get(this.z, x, y)
    }

    /**
     * This method is unsupported and throws an UnsupportedOperationException
     */
    @Override
    void remove() {
        throw new UnsupportedOperationException()
    }

    /**
     * Whether there are Tiles remaining
     * @return Whether there are Tiles remaining
     */
    @Override
    boolean hasNext() {
        boolean hasNext = counter < size
        if (!hasNext) {
            reset()
        }
        hasNext
    }

    /**
     * Whether the TileCursor is empty or not
     * @return
     */
    boolean getEmpty() {
        this.size == 0
    }

    /**
     * Reset and read the Tiles again.
     */
    void reset() {
        counter = 0
    }

    @Override
    String toString() {
        "TileCursor: zoom level = ${z} (${minX}, ${minY}) - (${maxX}, ${maxY}) width = ${width} height = ${height} number = ${size}"
    }

}

package geoscript.layer

import geoscript.geom.Bounds

/**
 * A TileCursor provides an easy way to iterate through Tiles.
 * @author Jared Erickson
 */
class TileCursor implements Iterator {

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
        this.minX = minX
        this.minY = minY
        this.maxX = maxX
        this.maxY = maxY
        this.width = (maxX - minX) + 1
        this.height = (maxY - minY) + 1
        this.size = width * height
    }

    /**
     * Create TileCursor that iterates over every Tile in a given zoom level and within the given Bounds
     * @param layer The TileLayer
     * @param b The Bounds
     * @param z The zoom level
     */
    TileCursor(TileLayer layer, Bounds b, long z) {
        Grid m = layer.pyramid.grid(z)
        Map tileCoords = layer.getTileCoordinates(b, m)
        this.tileLayer = layer
        this.z = z
        this.minX = tileCoords.minX
        this.minY = tileCoords.minY
        this.maxX = tileCoords.maxX
        this.maxY = tileCoords.maxY
        this.width = (maxX - minX) + 1
        this.height = (maxY - minY) + 1
        this.size = width * height
    }

    /**
     * Create TileCursor that iterates over every Tile within the given Bounds and for the x and y resolutions
     * @param layer The TileLayer
     * @param b The Bounds
     * @param resX The x resolution
     * @param resY The y resolution
     */
    TileCursor(TileLayer layer, Bounds b, double resX, double resY) {
        Grid m = layer.pyramid.grid(b, resX, resY)
        Map tileCoords = layer.getTileCoordinates(b, m)
        this.tileLayer = layer
        this.z = m.z
        this.minX = tileCoords.minX
        this.minY = tileCoords.minY
        this.maxX = tileCoords.maxX
        this.maxY = tileCoords.maxY
        this.width = (maxX - minX) + 1
        this.height = (maxY - minY) + 1
        this.size = width * height
    }

    /**
     * Create TileCursor that iterates over every Tile within the Bounds for the image size
     * @param layer The TileLayer
     * @param b The Bounds
     * @param w The image width
     * @param h The image height
     */
    TileCursor(TileLayer layer, Bounds b, int w, int h) {
        Grid m = layer.pyramid.grid(b, w, h)
        Map tileCoords = layer.getTileCoordinates(b, m)
        this.tileLayer = layer
        this.z = m.z
        this.minX = tileCoords.minX
        this.minY = tileCoords.minY
        this.maxX = tileCoords.maxX
        this.maxY = tileCoords.maxY
        this.width = (maxX - minX) + 1
        this.height = (maxY - minY) + 1
        this.size = width * height
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
        Bounds min = tileLayer.pyramid.bounds(new Tile(z, minX, minY))
        Bounds max = tileLayer.pyramid.bounds(new Tile(z, maxX, maxY))
        min.expand(max)
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

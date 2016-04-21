package geoscript.layer

import geoscript.geom.Bounds
import groovy.util.logging.Log

/**
 * A TileLayer that can generate Tiles on demand as a client tries to get them.
 * @author Jared Erickson.
 */
@Log
class GeneratingTileLayer extends TileLayer<Tile> {

    /**
     * The wrapped TileLayer
     */
    private TileLayer tileLayer

    /**
     * The TileRenderer used to generate Tiles on demand
     */
    private TileRenderer tileRenderer

    /**
     * Create a new GeneratingTileLayer that uses the TileRenderer to create Tiles on demand for the given TileLayer.
     * @param tileLayer The TileLayer
     * @param tileRenderer The TileRenderer
     */
    GeneratingTileLayer(TileLayer tileLayer, TileRenderer tileRenderer) {
        this.tileLayer = tileLayer
        this.tileRenderer = tileRenderer
        this.name = tileLayer.name
        this.bounds = tileLayer.bounds
        this.proj = tileLayer.proj
    }

    /**
     * Get the Pyramid
     * @return The Pyramid
     */
    @Override
    Pyramid getPyramid() {
        tileLayer.pyramid
    }

    /**
     * Get a Tile
     * @param z The zoom level
     * @param x The column
     * @param y The row
     * @return A Tile
     */
    @Override
    Tile get(long z, long x, long y) {
        Tile tile = tileLayer.get(z, x, y)
        if (!tile.data) {
            log.info "Generating tile ${tile}..."
            Bounds bounds = tileLayer.pyramid.bounds(tile)
            tile.data = tileRenderer.render(bounds)
            put(tile)
        }
        tile
    }

    /**
     * Add a Tile
     * @param t The Tile
     */
    @Override
    void put(Tile tile) {
       tileLayer.put(tile)
    }

    /**
     * Delete a Tile
     * @param t The Tile
     */
    @Override
    void delete(Tile tile) {
        tileLayer.delete(tile)
    }

    /**
     * Close the TileLayer
     */
    @Override
    void close() throws IOException {
        tileLayer.close()
    }

}

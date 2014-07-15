package geoscript.layer

import geoscript.geom.Bounds

/**
 * A TileRenderer can generate a Tile's data for a given Bounds
 * @author Jared Erickson
 */
interface TileRenderer {

    /**
     * Renderer a Tile's data for a given Bounds
     * @param b The Bounds
     * @return The Tile's data
     */
    byte[] render(Bounds b)

}

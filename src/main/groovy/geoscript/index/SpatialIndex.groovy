package geoscript.index

import org.locationtech.jts.index.SpatialIndex as JtsSpatialIndex
import geoscript.geom.Bounds

/**
 * A SpatialIndex base class.
 */
class SpatialIndex {

    /**
     * The wrapped JTS SpatialIndex
     */
    JtsSpatialIndex index

    /**
     * Create a new SpatialIndex with a JTS SpatialIndex
     * @param the JTS SpatialIndex
     */
    SpatialIndex(JtsSpatialIndex index) {
        this.index = index
    }

    /**
     * Create a STR Tree SpatialIndex
     * @return a SpatialIndex using the STR Tree algorithm
     */
    static SpatialIndex createSTRtree() {
        new STRtree()
    }

    /**
     * Create a Quad Tree SpatialIndex
     * @return a SpatialIndex using the Quad Tree algorithm
     */
    static SpatialIndex createQuadtree() {
        new Quadtree()
    }

    /**
     * Get the number of items indexed
     * @return The number of items indexed
     */
    int getSize() {
        index.size()
    }

    /**
     * Insert a Bounds and Item
     * @param bounds The Bounds
     * @param item The value
     */
    void insert(Bounds bounds, def item) {
        index.insert(bounds.env, item)
    }

    /**
     * Query the index by the Bounds
     * @param bounds The Bounds
     * @return The List of values
     */
    List query(Bounds bounds) {
        index.query(bounds.env)
    }
}

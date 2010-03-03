package geoscript.geom

import com.vividsolutions.jts.index.SpatialIndex as JtsSpatialIndex
import com.vividsolutions.jts.index.strtree.STRtree
import geoscript.geom.Bounds

/**
 * A SpatialIndex
 */
class SpatialIndex {

    /**
     * The wrapped JTS SpatialIndex
     */
    JtsSpatialIndex index

    /**
     * Create a new SpatialIndex with a JTS SpatialIndex
     */
    SpatialIndex(JtsSpatialIndex index) {
        this.index = index
    }

    /**
     * Create a new SpatialIndex
     */
    SpatialIndex() {
        this(new STRtree())
    }

    /**
     * Get the number of items indexed
     */
    int getSize() {
        index.size()
    }

    /**
     * Insert a Bounds and Item
     */
    void insert(Bounds bounds, def item) {
        index.insert(bounds.env, item)
    }

    /**
     * Query the index by the Bounds
     */
    List query(Bounds bounds) {
        index.query(bounds.env)
    }
}


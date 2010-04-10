package geoscript.index

import com.vividsolutions.jts.index.quadtree.Quadtree as JtsQuadtree
import geoscript.geom.Bounds

/**
 * Create a SpatialIndex using the Quad Tree spatial index.
 * @author Jared Erickson
 */
class Quadtree extends SpatialIndex {

    /**
     * Create a SpatialIndex using the Quad Tree spatial index.
     */
    Quadtree() {
        super(new JtsQuadtree())
    }

    /**
     * Get a List of all entries in the spatial index
     */
    List queryAll() {
        index.queryAll()
    }

    /**
     * Remove an item from the index
     * @param bounds The Bounds
     * @param item The Object
     * @return Whether an item was removed or not
     */
    boolean remove(Bounds bounds, Object item) {
        index.remove(bounds.env, item)
    }
}


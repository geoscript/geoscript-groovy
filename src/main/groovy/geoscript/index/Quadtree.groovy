package geoscript.index

import com.vividsolutions.jts.index.quadtree.Quadtree as JtsQuadtree

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
}


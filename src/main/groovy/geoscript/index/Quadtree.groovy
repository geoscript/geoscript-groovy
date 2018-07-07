package geoscript.index

import org.locationtech.jts.index.quadtree.Quadtree as JtsQuadtree
import geoscript.geom.Bounds

/**
 * Create a SpatialIndex using the Quad Tree spatial index.
 * <p><blockquote><pre>
 * def index = new Quadtree()
 * index.insert(new Bounds(0,0,10,10), new Point(5,5))
 * index.insert(new Bounds(2,2,6,6), new Point(4,4))
 *
 * def results = index.query(new Bounds(4,4,7,7))
 * </pre></blockquote></p>
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
     * @param The List of results
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


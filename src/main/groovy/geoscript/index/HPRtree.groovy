package geoscript.index

import org.locationtech.jts.index.hprtree.HPRtree as JtsHPRtree

/**
 * Create a SpatialIndex using the Hilbert Packed Rtree (HPRtree) spatial index.
 * <p><blockquote><pre>
 * def index = new HPRtree()
 * index.insert(new Bounds(0,0,10,10), new Point(5,5))
 * index.insert(new Bounds(2,2,6,6), new Point(4,4))
 *
 * def results = index.query(new Bounds(1,1,5,5))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class HPRtree extends SpatialIndex {

    /**
     * Create a SpatialIndex using the HPR Tree spatial index.
     */
    HPRtree() {
        super(new JtsHPRtree())
    }

}


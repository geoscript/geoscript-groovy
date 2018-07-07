package geoscript.index

import org.locationtech.jts.index.strtree.STRtree as JtsSTRtree

/**
 * Create a SpatialIndex using the STR Tree spatial index.
 * <p><blockquote><pre>
 * def index = new STRtree()
 * index.insert(new Bounds(0,0,10,10), new Point(5,5))
 * index.insert(new Bounds(2,2,6,6), new Point(4,4))
 *
 * def results = index.query(new Bounds(1,1,5,5))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class STRtree extends SpatialIndex {
	
    /**
     * Create a SpatialIndex using the STR Tree spatial index.
     */
    STRtree() {
        super(new JtsSTRtree())
    }

}


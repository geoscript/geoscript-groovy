package geoscript.index

import com.vividsolutions.jts.index.strtree.STRtree as JtsSTRtree

/**
 * Create a SpatialIndex using the STR Tree spatial index.
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


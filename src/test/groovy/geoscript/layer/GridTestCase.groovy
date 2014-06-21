package geoscript.layer

import geoscript.layer.Grid

import static junit.framework.Assert.*
import org.junit.Test

/**
 * The Tile Grid Unit Test
 * @author Jared Erickson
 */
class GridTestCase {

    @Test void grid() {
        Grid grid = new Grid(1,2,3,520,250)
        assertEquals 1, grid.z
        assertEquals 2, grid.width
        assertEquals 3, grid.height
        assertEquals 6, grid.size
        assertEquals 520, grid.xResolution, 0.1
        assertEquals 250, grid.yResolution, 0.1
        assertEquals "Grid(z:1, width:2, height:3, size:6, xResolution:520.0, yResolution:250.0)", grid.toString()
    }

}

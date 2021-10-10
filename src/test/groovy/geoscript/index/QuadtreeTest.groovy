package geoscript.index

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import geoscript.geom.*

/**
 * The QuadtreeTest
 */
class QuadtreeTest {

    @Test void index() {

        def spatialIndex = new Quadtree()
        spatialIndex.insert(new Bounds(0,0,10,10), new Point(5,5))
        spatialIndex.insert(new Bounds(2,2,6,6), new Point(4,4))
        spatialIndex.insert(new Bounds(20,20,60,60), new Point(30,30))
        spatialIndex.insert(new Bounds(22,22,44,44), new Point(32,32))

        assertEquals 4, spatialIndex.size

        def bounds = new Bounds(4,4,7,7)
        def results = spatialIndex.query(bounds)
        //println("Results #1 for ${bounds}: ${results}")
        // TODO Why 4?  Should be 2?!?
        assertEquals 4, results.size()

        bounds = new Bounds(25,25,50,55)
        results = spatialIndex.query(bounds)
        assertEquals 2, results.size()
        assertTrue(results[0].toString() == 'POINT (30 30)' || results[0].toString() == 'POINT (32 32)')
        assertTrue(results[1].toString() == 'POINT (30 30)' || results[1].toString() == 'POINT (32 32)')

        List all = spatialIndex.queryAll()
        assertEquals 4, all.size()

        // Remove an item
        assertTrue(spatialIndex.remove(all[3].bounds, all[3]))
        all = spatialIndex.queryAll()
        assertEquals 3, all.size()
    }
}


package geoscript.layer

import geoscript.workspace.Workspace
import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Feature

/**
 * The Cursor UnitTest
 */
class CursorTestCase {

    @Test void constructors() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        assertNotNull(shp)
        int count = shp.count()
        int counter = 0
        Cursor c = shp.cursor
        while(c.hasNext()) {
            Feature f = c.next()
            counter++
        }
        c.close()
        assertEquals count, counter
    }

    @Test void iterator() {
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        assertNotNull(shp)
        Cursor c = shp.cursor
        def names = c.collect{f -> f.get("NAME")}
        c.close()
        assertEquals 49, names.size()
    }

    @Test void reset() {

        // Get a Shapefle
        Workspace w = Workspace.getWorkspace([url: getClass().getClassLoader().getResource("states.shp")])
        Layer shp = w.get("states")
        assertNotNull(shp)

        // Use Cursor to get a List of NAME values
        Cursor c = shp.cursor
        def names = c.collect{f -> f.get("NAME")}
        c.close()
        assertEquals 49, names.size()

        // Reset the Cursor to read again
        c.reset()

        // Use Cursor to get a List of NAME values again
        names = c.collect{f -> f.get("NAME")}
        c.close()
        assertEquals 49, names.size()
    }

    @Test void sortStartAndMax() {
        // Property files don't natively support paging
        File file = new File(getClass().getClassLoader().getResource("points.properties").toURI())
        assertNotNull(file)
        Workspace w = Workspace.getWorkspace([directory: file.absolutePath])
        Layer layer = w.get("points")
        assertNotNull(layer)
        // 3 to 4
        Cursor c = layer.getCursor(sort: ["name DESC"], start: 2, max: 2)
        assertEquals "point 2", c.next()['name']
        assertEquals "point 1", c.next()['name']
        assertFalse c.hasNext()
        c.close()
        // 2 to 4
        c = layer.getCursor(sort: ["name DESC"], start: 1, max: 3)
        assertEquals "point 3", c.next()['name']
        assertEquals "point 2", c.next()['name']
        assertEquals "point 1", c.next()['name']
        assertFalse c.hasNext()
        c.close()
        // 1 to 3
        c = layer.getCursor(sort: ["name DESC"], start: 0, max: 3)
        assertEquals "point 4", c.next()['name']
        assertEquals "point 3", c.next()['name']
        assertEquals "point 2", c.next()['name']
        assertFalse c.hasNext()
        c.close()
    }

}


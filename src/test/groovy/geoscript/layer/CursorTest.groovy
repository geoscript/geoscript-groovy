package geoscript.layer

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import geoscript.feature.Feature

/**
 * The Cursor UnitTest
 */
class CursorTest {

    @Test void constructors() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
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
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        Cursor c = shp.cursor
        def names = c.collect{f -> f.get("NAME")}
        c.close()
        assertEquals 49, names.size()
    }

    @Test void reset() {

        // Get a Shapefle
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)
        Shapefile shp = new Shapefile(file)
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
        Layer layer = new Property(file)
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

    @Test void sortStartAndMaxShapfile() {
        File file = new File(getClass().getClassLoader().getResource("points.shp").toURI())
        assertNotNull(file)
        Layer layer = new Shapefile(file)
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

        // Paging, No Sorting
        c = layer.getCursor(start: 0, max: 3)
        assertEquals "point 1", c.next()['name']
        assertEquals "point 2", c.next()['name']
        assertEquals "point 3", c.next()['name']
        assertFalse c.hasNext()
        c.close()
        c = layer.getCursor(start: 1, max: 2)
        assertEquals "point 2", c.next()['name']
        assertEquals "point 3", c.next()['name']
        assertFalse c.hasNext()
        c.close()
        c = layer.getCursor(start: 2, max: 2)
        assertEquals "point 3", c.next()['name']
        assertEquals "point 4", c.next()['name']
        assertFalse c.hasNext()
        c.close()
        c = layer.getCursor(max: 2)
        assertEquals "point 1", c.next()['name']
        assertEquals "point 2", c.next()['name']
        assertFalse c.hasNext()
        c.close()
    }

}


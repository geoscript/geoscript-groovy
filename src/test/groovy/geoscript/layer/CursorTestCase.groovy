package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Feature

/**
 * The Cursor UnitTest
 */
class CursorTestCase {

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

}


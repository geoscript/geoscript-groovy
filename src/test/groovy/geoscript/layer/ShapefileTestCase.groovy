package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Shapefile UnitTest
 */
class ShapefileTestCase {

    @Test void constructors() {

        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        assertEquals file, shp.file

        println("Number of Features: ${shp.count()}")
        println("Bounds: ${shp.bounds()}")
        assertEquals 49, shp.count()
        assertEquals "(-124.73142200000001,24.955967,-66.969849,49.371735,EPSG:4326)", shp.bounds().toString()
    }

    @Test void bounds() {
        File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
        Shapefile shp = new Shapefile(file)
        assertEquals "(-124.73142200000001,24.955967,-66.969849,49.371735,EPSG:4326)", shp.bounds.toString()
        assertEquals "(-109.055199,36.988972000000004,-102.036758,41.00341,EPSG:4326)", shp.bounds("STATE_NAME = 'Colorado'").toString()
    }

}


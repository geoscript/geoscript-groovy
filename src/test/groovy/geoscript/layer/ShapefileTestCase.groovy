package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Shapefile UnitTest
 */
class ShapefileTestCase {

    @Test void constructors() {

        File file = new File(getClass().getClassLoader().getResource("110m-admin-0-countries.shp").toURI())
        assertNotNull(file)

        Shapefile shp = new Shapefile(file)
        assertNotNull(shp)
        assertEquals file.parentFile, shp.file

        println("Number of Features: ${shp.count()}")
        println("Bounds: ${shp.bounds()}")
        assertEquals 268, shp.count()
        assertEquals "(-180.00004106353606,-89.99889902136007,180.00000044181036,83.64512726514185,EPSG:4326)", shp.bounds().toString()
    }

}


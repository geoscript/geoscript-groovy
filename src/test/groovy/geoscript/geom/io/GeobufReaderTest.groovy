package geoscript.geom.io

import geoscript.geom.Geometry
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The GeobufReader Unit Test.
 * @author Jared Erickson
 */
class GeobufReaderTest {

    @Test void read() {
        GeobufReader reader = new GeobufReader()
        String str = "10021806320c08001a08ffc9ac7480a7e92c"
        Geometry g = reader.read(str)
        assertEquals "POINT (-122 47)", g.wkt
    }

}

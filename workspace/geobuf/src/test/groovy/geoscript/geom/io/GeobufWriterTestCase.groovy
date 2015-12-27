package geoscript.geom.io

import geoscript.geom.Geometry
import org.junit.Test
import static org.junit.Assert.*

/**
 * The GeobufWriter Unit Test
 * @author Jared Erickson
 */
class GeobufWriterTestCase {

    @Test void write() {
        // Write
        GeobufWriter writer = new GeobufWriter()
        Geometry g = Geometry.fromWKT("POINT (-122 47)")
        String str = writer.write(g)
        assertEquals "10021806320c08001a08ffc9ac7480a7e92c", str
        byte[] bytes = writer.writeBytes(g)
        assertTrue bytes.length > 0
        // Read
        GeobufReader reader = new GeobufReader()
        Geometry decodedGeom = reader.read(str)
        assertEquals "POINT (-122 47)", decodedGeom.wkt
        decodedGeom = reader.read(bytes)
        assertEquals "POINT (-122 47)", decodedGeom.wkt
    }

}

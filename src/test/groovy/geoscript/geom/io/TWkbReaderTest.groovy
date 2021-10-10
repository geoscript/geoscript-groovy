package geoscript.geom.io

import geoscript.geom.Geometry
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals

class TWkbReaderTest {

    @Test
    void read() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("01000204")
        assertEquals("POINT (1 2)", geometry.wkt)
        geometry = reader.read("01000204".decodeHex())
        assertEquals("POINT (1 2)", geometry.wkt)
    }

}

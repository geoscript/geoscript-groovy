package geoscript.feature

import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.*

class FeatureTestCase {

    @Test void getGeobuf() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        assertEquals "0a046e616d650a057072696365100218062a1f0a0c08001a" +
                "0880e7ed69ffa6e92c6a070a05486f7573656a060a0431322e35", feature.geobuf
        assertArrayEquals([10, 4, 110, 97, 109, 101, 10, 5, 112, 114, 105, 99, 101, 16, 2, 24, 6, 42, 31, 10, 12, 8, 0,
                           26, 8, -128, -25, -19, 105, -1, -90, -23, 44, 106, 7, 10, 5, 72, 111, 117, 115, 101, 106, 6,
                           10, 4, 49, 50, 46, 53] as byte[], feature.geobufBytes)
    }

    @Test void fromGeobuf() {
        Feature f = Feature.fromGeobuf("0a046e616d650a057072696365100218062a1f0a0c08001a" +
                "0880e7ed69ffa6e92c6a070a05486f7573656a060a0431322e35")
        assertNotNull f
        assertEquals(111, f.geom.x, 0.1)
        assertEquals(-47, f.geom.y, 0.1)
        assertEquals("House", f["name"])
        assertEquals(12.5, f["price"] as double, 0.1)
    }

}

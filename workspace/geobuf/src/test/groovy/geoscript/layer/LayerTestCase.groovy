package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class LayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void toGeobuf() {
        Schema schema = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        byte[] expectedBytes = [10, 4, 110, 97, 109, 101, 10, 5, 112, 114, 105, 99, 101, 16, 2, 24, 6, 34, 33, 10, 31,
                                10, 12, 8, 0, 26, 8, -128, -25, -19, 105, -1, -90, -23, 44, 106, 7, 10, 5, 72, 111,
                                117, 115, 101, 106, 6, 10, 4, 49, 50, 46, 53]
        // OutputStream
        def out = new java.io.ByteArrayOutputStream()
        layer.toGeobuf(out)
        assertArrayEquals(expectedBytes, out.toByteArray())
        // Bytes
        assertArrayEquals(expectedBytes, layer.toGeobufBytes())
        // File
        File file = folder.newFile("test.pbf")
        layer.toGeobufFile(file)
        file.withInputStream {InputStream inputStream ->
            assertArrayEquals(expectedBytes, inputStream.bytes)
        }
        // String
        String hex = layer.toGeobufString()
        assertEquals "0a046e616d650a0570726963651002180622210a1f0a0c08001a0880e" +
                "7ed69ffa6e92c6a070a05486f7573656a060a0431322e35", hex

    }

}

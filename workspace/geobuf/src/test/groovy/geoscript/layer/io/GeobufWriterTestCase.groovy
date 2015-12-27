package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.Memory
import org.apache.commons.codec.binary.Hex
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import static org.junit.Assert.*

/**
 * The GeobufWriter Unit Test.
 * @author Jared Erickson
 */
class GeobufWriterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void write() {
        Schema schema = new Schema("houses", [new Field("geom", "Point"), new Field("name", "string"), new Field("price", "float")])
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111, -47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121, -45), "School", 22.7], "house2", schema))

        String expectedHex = "0a046e616d650a0570726963651002180622430a1f0a0c08001a0880e7ed69ffa6e92c6a070a05486f7573656a060a" +
                "0431322e350a200a0c08001a0880c1b273ff94f52a6a080a065363686f6f6c6a060a0432322e37"
        GeobufWriter writer = new GeobufWriter()
        String hex = writer.write(layer)
        assertEquals expectedHex, hex

        byte[] bytes = writer.writeBytes(layer)
        assertEquals expectedHex, String.valueOf(Hex.encodeHex(bytes))

        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        bytes = out.toByteArray()
        assertEquals expectedHex, String.valueOf(Hex.encodeHex(bytes))

        File file = folder.newFile("houses.pbf")
        writer.write(layer, file)
        file.withInputStream { InputStream inputStream ->
            assertEquals expectedHex, String.valueOf(Hex.encodeHex(inputStream.bytes))
        }
    }
}

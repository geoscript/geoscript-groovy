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
 * The GeobufReader Unit Test
 * @author Jared Erickson
 */
class GeobufReaderTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void read() {
        String hex = "0a046e616d650a0570726963651002180622430a1f0a0c08001a0880e7ed69ffa6e92c6a070a05486f7573656a060a" +
                "0431322e350a200a0c08001a0880c1b273ff94f52a6a080a065363686f6f6c6a060a0432322e37"

        Schema schema = new Schema("houses", [new Field("geom", "Point"), new Field("name", "string"), new Field("price", "float")])
        Memory memory = new Memory()
        Layer expectedLayer = memory.create(schema)
        expectedLayer.add(new Feature([new Point(111, -47), "House", 12.5], "house1", schema))
        expectedLayer.add(new Feature([new Point(121, -45), "School", 22.7], "house2", schema))

        GeobufReader reader = new GeobufReader()

        Layer actualLayer = reader.read(hex)
        assertEquals 2, actualLayer.count
        assertEquals "POINT (111 -47)", actualLayer.features[0].geom.wkt
        assertEquals "House", actualLayer.features[0]["name"]
        assertEquals "12.5", actualLayer.features[0]["price"]
        assertEquals "POINT (121 -45)", actualLayer.features[1].geom.wkt
        assertEquals "School", actualLayer.features[1]["name"]
        assertEquals "22.7", actualLayer.features[1]["price"]

        actualLayer = reader.read(Hex.decodeHex(hex.toCharArray()))
        assertEquals 2, actualLayer.count
        assertEquals "POINT (111 -47)", actualLayer.features[0].geom.wkt
        assertEquals "House", actualLayer.features[0]["name"]
        assertEquals "12.5", actualLayer.features[0]["price"]
        assertEquals "POINT (121 -45)", actualLayer.features[1].geom.wkt
        assertEquals "School", actualLayer.features[1]["name"]
        assertEquals "22.7", actualLayer.features[1]["price"]

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Hex.decodeHex(hex.toCharArray()))
        actualLayer = reader.read(inputStream)
        inputStream.close()
        assertEquals 2, actualLayer.count
        assertEquals "POINT (111 -47)", actualLayer.features[0].geom.wkt
        assertEquals "House", actualLayer.features[0]["name"]
        assertEquals "12.5", actualLayer.features[0]["price"]
        assertEquals "POINT (121 -45)", actualLayer.features[1].geom.wkt
        assertEquals "School", actualLayer.features[1]["name"]
        assertEquals "22.7", actualLayer.features[1]["price"]

        File file = folder.newFile("houses.pbf")
        file.withOutputStream { OutputStream out ->
           out.write(Hex.decodeHex(hex.toCharArray()))
        }
        actualLayer = reader.read(file)
        inputStream.close()
        assertEquals 2, actualLayer.count
        assertEquals "POINT (111 -47)", actualLayer.features[0].geom.wkt
        assertEquals "House", actualLayer.features[0]["name"]
        assertEquals "12.5", actualLayer.features[0]["price"]
        assertEquals "POINT (121 -45)", actualLayer.features[1].geom.wkt
        assertEquals "School", actualLayer.features[1]["name"]
        assertEquals "22.7", actualLayer.features[1]["price"]
    }
}

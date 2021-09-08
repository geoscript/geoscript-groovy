package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.Memory
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

        GeobufWriter writer = new GeobufWriter()
        String hex = writer.write(layer)

        GeobufReader reader = new GeobufReader()
        Layer layerFromGeobuf = reader.read(hex)

        assertEquals 2, layerFromGeobuf.count
        assertEquals "POINT (111 -47)", layerFromGeobuf.features[0].geom.wkt
        assertEquals "House", layerFromGeobuf.features[0]["name"]
        assertEquals "12.5", layerFromGeobuf.features[0]["price"]
        assertEquals "POINT (121 -45)", layerFromGeobuf.features[1].geom.wkt
        assertEquals "School", layerFromGeobuf.features[1]["name"]
        assertEquals "22.7", layerFromGeobuf.features[1]["price"]

    }
}

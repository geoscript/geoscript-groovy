package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*

/**
 * The GeobufWriter Unit Test
 * @author Jared Erickson
 */
class GeobufWriterTestCase {

    @Test void write() {
        GeobufWriter writer = new GeobufWriter()
        GeobufReader reader = new GeobufReader()
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        String hex = writer.write(feature)
        assertEquals "0a046e616d650a057072696365100218062a2d0a0c08001a0880e7ed69ffa6e92c5a06686f757365316a070a05486f7573656a060a0431322e35720400000101", hex
        Feature decodedFeature = reader.read(hex)
        assertEquals feature.geom, decodedFeature.geom
        assertEquals feature["name"], decodedFeature["name"]
        assertEquals String.valueOf(feature["price"]), decodedFeature["price"]
    }
}

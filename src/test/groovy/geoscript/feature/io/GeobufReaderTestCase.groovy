package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.assertEquals

/**
 * The GeobufReader Unit Test
 * @author Jared Erickson
 */
class GeobufReaderTestCase {

    @Test void read() {
        GeobufReader reader = new GeobufReader()
        Feature feature = reader.read("0a046e616d650a057072696365100218062a1f0a0c08001a0880e7ed69ffa6e92c6a070a05486f7573656a060a0431322e35")
        assertEquals new Point(111,-47), feature.geom
        assertEquals "House", feature["name"]
        assertEquals "12.5", feature["price"]
    }

}

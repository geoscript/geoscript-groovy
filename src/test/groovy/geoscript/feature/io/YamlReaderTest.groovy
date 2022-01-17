package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class YamlReaderTest {

    @Test void write() {
        YamlReader reader = new YamlReader()
        Feature feature = reader.read("""---
type: "Feature"
properties:
  name: "House"
  price: 12.5
geometry:
  type: "Point"
  coordinates:
  - 111.0
  - -47.0
""")
        assertEquals("POINT (111 -47)", feature.geom.wkt)
        assertEquals("House", feature.get("name"))
        assertEquals(12.5, feature.get("price"), 0.1)
    }

}

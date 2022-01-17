package geoscript.feature.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

class YamlWriterTest {

    @Test void write() {
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature feature = new Feature([new Point(111,-47), "House", 12.5], "house1", schema)
        YamlWriter writer = new YamlWriter()
        String expected = """---
type: "Feature"
properties:
  name: "House"
  price: 12.5
geometry:
  type: "Point"
  coordinates:
  - 111.0
  - -47.0
"""
        String actual = writer.write(feature)
        assertEquals expected, actual
    }

}

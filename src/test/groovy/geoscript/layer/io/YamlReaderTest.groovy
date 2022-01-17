package geoscript.layer.io

import geoscript.layer.Layer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class YamlReaderTest {

    @TempDir
    File folder

    @Test void read() {
        String yaml = """---
type: "FeatureCollection"
features:
- properties:
    name: "House"
    price: 12.5
  geometry:
    type: "Point"
    coordinates:
    - 111.0
    - -47.0
- properties:
    name: "School"
    price: 22.7
  geometry:
    type: "Point"
    coordinates:
    - 121.0
    - -45.0
"""
        YamlReader yamlReader = new YamlReader()
        Layer layer = yamlReader.read(yaml)
        assertEquals(2, layer.count)
        assertEquals("POINT (111 -47)", layer.features[0].geom.wkt)
        assertEquals("House", layer.features[0]["name"])
        assertEquals(12.5, layer.features[0]["price"], 0.1)
        assertEquals("POINT (121 -45)", layer.features[1].geom.wkt)
        assertEquals("School", layer.features[1]["name"])
        assertEquals(22.7, layer.features[1]["price"], 0.1)

        File file = new File(folder, "layer.yml")
        file.text = yaml
        layer = yamlReader.read(yaml)
        assertEquals(2, layer.count)
        assertEquals("POINT (111 -47)", layer.features[0].geom.wkt)
        assertEquals("House", layer.features[0]["name"])
        assertEquals(12.5, layer.features[0]["price"], 0.1)
        assertEquals("POINT (121 -45)", layer.features[1].geom.wkt)
        assertEquals("School", layer.features[1]["name"])
        assertEquals(22.7, layer.features[1]["price"], 0.1)

        file.withInputStream {InputStream inputStream ->
            layer = yamlReader.read(inputStream)
            assertEquals(2, layer.count)
            assertEquals("POINT (111 -47)", layer.features[0].geom.wkt)
            assertEquals("House", layer.features[0]["name"])
            assertEquals(12.5, layer.features[0]["price"], 0.1)
            assertEquals("POINT (121 -45)", layer.features[1].geom.wkt)
            assertEquals("School", layer.features[1]["name"])
            assertEquals(22.7, layer.features[1]["price"], 0.1)
        }
    }

}

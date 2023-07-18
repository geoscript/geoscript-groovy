package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.Memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*

class YamlWriterTest {

    @TempDir
    File folder

    @Test void write() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        String expected = """---
type: FeatureCollection
features:
- properties:
    name: House
    price: 12.5
  geometry:
    type: Point
    coordinates:
    - 111.0
    - -47.0
- properties:
    name: School
    price: 22.7
  geometry:
    type: Point
    coordinates:
    - 121.0
    - -45.0
"""

        // Write the Layer to a Yaml String
        YamlWriter writer = new YamlWriter()
        String yaml = writer.write(layer)
        assertEquals(expected, yaml)

        // Write Layer as Yaml to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        yaml = out.toString()
        assertEquals(expected, yaml)

        // Write Layer as GeoJSON to a File
        File file = new File(folder,"layer.yml")
        writer.write(layer, file)
        yaml = file.text
        assertEquals(expected, yaml)
    }

}

package geoscript.layer.io

import org.junit.Test
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Feature
import geoscript.geom.Point

import static org.junit.Assert.assertEquals

/**
 * The CsvWriter Unit Test
 * @author Jared Erickson
 */
class CsvWriterTestCase {

    @Test void writeWTK() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        String expected = """"geom","name","price"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter()
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeXY() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        String expected = """"lon"|"lat"|"name"|"price"
"111.0"|"-47.0"|"House"|"12.5"
"121.0"|"-45.0"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat", separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

}

package geoscript.layer.io

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import geoscript.layer.Layer
import geoscript.workspace.Memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The KmlWriter UnitTest
 * @author Jared Erickson
 */
class KmlWriterTest {

    @TempDir
    File folder

    @Test
    void write() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom", "Point"), new Field("name", "string"), new Field("price", "float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111, -47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121, -45), "School", 22.7], "house2", schema))

        // Write the Layer to a KML String
        KmlWriter writer = new KmlWriter()
        String kml = writer.write(layer)
        assertTrue kml.startsWith("<kml:kml xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:kml=\"http://earth.google.com/kml/2.1\">")

        // Write Layer as KML to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        kml = out.toString()
        assertTrue kml.startsWith("<kml:kml xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:kml=\"http://earth.google.com/kml/2.1\">")

        // Write Layer as KML to a File
        File file = new File(folder,"layer.kml")
        writer.write(layer, file)
        kml = file.text
        assertTrue kml.startsWith("<kml:kml xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:kml=\"http://earth.google.com/kml/2.1\">")
    }

}

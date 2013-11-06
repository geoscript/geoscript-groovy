package geoscript.layer.io

import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The KmlWriter UnitTest
 * @author Jared Erickson
 */
class KmlWriterTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void write() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        // Write the Layer to a KML String
        KmlWriter writer = new KmlWriter()
        String kml = writer.write(layer)
        println kml
        assertTrue kml.startsWith("<kml:kml xmlns:kml=\"http://earth.google.com/kml/2.1\">")

        // Write Layer as KML to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        kml = out.toString()
        assertTrue kml.startsWith("<kml:kml xmlns:kml=\"http://earth.google.com/kml/2.1\">")

        // Write Layer as KML to a File
        File file = folder.newFile("layer.kml")
        writer.write(layer, file)
        kml = file.text
        assertTrue kml.startsWith("<kml:kml xmlns:kml=\"http://earth.google.com/kml/2.1\">")
    }

}

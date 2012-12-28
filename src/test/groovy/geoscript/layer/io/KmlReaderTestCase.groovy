package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The KmlReader UnitTest
 * @author Jared Erickson
 */
class KmlReaderTestCase {

    @Test void read() {
        String kml = """<kml:kml xmlns:kml="http://earth.google.com/kml/2.1">
    <kml:Document id="featureCollection">
        <kml:Placemark id="fid--259df7e1_131b6de0b8f_-8000">
            <kml:name>House</kml:name>
            <kml:Point>
                <kml:coordinates>111.0,-47.0</kml:coordinates>
            </kml:Point>
        </kml:Placemark>
        <kml:Placemark id="fid--259df7e1_131b6de0b8f_-7fff">
            <kml:name>School</kml:name>
            <kml:Point>
                <kml:coordinates>121.0,-45.0</kml:coordinates>
            </kml:Point>
        </kml:Placemark>
    </kml:Document>
</kml:kml>"""

        KmlReader reader = new KmlReader()

        // Read from a String
        Layer layer = reader.read(kml)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(kml.getBytes("UTF-8"))
        layer = reader.read(input)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from a File
        File file = File.createTempFile("layer",".kml")
        file.write(kml)
        layer = reader.read(file)
        assertNotNull layer
        assertEquals 2, layer.count
    }
}


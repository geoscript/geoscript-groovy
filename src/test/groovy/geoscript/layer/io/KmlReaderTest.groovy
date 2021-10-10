package geoscript.layer.io

import geoscript.proj.Projection
import geoscript.workspace.Memory
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.layer.Layer

/**
 * The KmlReader UnitTest
 * @author Jared Erickson
 */
class KmlReaderTest {

    @TempDir
    File folder

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
        assertEquals "kml", layer.name
        assertEquals "EPSG:4326", layer.proj.id
        assertTrue layer.schema.has("Geometry")
        assertTrue layer.schema.has("name")
        assertTrue layer.schema.has("description")
        assertEquals 2, layer.count
        layer.eachFeature{f ->
            assertNotNull f.geom
            assertNotNull f["name"]
            assertNull f["description"]
        }

        // Read from a String with custom name, projections, workspace
        layer = reader.read(kml, name: "points", projection: new Projection("EPSG:4326"), workspace: new Memory())
        assertNotNull layer
        assertEquals "points", layer.name
        assertEquals "EPSG:4326", layer.proj.id
        assertTrue layer.schema.has("Geometry")
        assertTrue layer.schema.has("name")
        assertTrue layer.schema.has("description")
        assertEquals 2, layer.count
        layer.eachFeature{f ->
            assertNotNull f.geom
            assertNotNull f["name"]
            assertNull f["description"]
        }

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(kml.getBytes("UTF-8"))
        layer = reader.read(input)
        assertNotNull layer
        assertEquals "kml", layer.name
        assertEquals "EPSG:4326", layer.proj.id
        assertTrue layer.schema.has("Geometry")
        assertTrue layer.schema.has("name")
        assertTrue layer.schema.has("description")
        assertEquals 2, layer.count
        layer.eachFeature{f ->
            assertNotNull f.geom
            assertNotNull f["name"]
            assertNull f["description"]
        }

        // Read from a File
        File file = new File(folder,"layer.kml")
        file.write(kml)
        layer = reader.read(file)
        assertNotNull layer
        assertEquals "kml", layer.name
        assertEquals "EPSG:4326", layer.proj.id
        assertTrue layer.schema.has("Geometry")
        assertTrue layer.schema.has("name")
        assertTrue layer.schema.has("description")
        assertEquals 2, layer.count
        layer.eachFeature{f ->
            assertNotNull f.geom
            assertNotNull f["name"]
            assertNull f["description"]
        }
    }
}


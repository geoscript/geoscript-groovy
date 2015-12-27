package geoscript.layer.io

import geoscript.layer.Layer
import geoscript.proj.Projection
import geoscript.workspace.Memory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

/**
 * The GmlReader UnitTest
 * @author Jared Erickson
 */
class GmlReaderTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void read() {

        String gml = """<wfs:FeatureCollection xmlns:ogc="http://www.opengis.net/ogc"
    xmlns:gml="http://www.opengis.net/gml"
    xmlns:wfs="http://www.opengis.net/wfs" xmlns:gsf="http://geoscript.org/feature">
    <gml:boundedBy>
        <gml:Box>
            <gml:coord>
                <gml:X>111.0</gml:X>
                <gml:Y>-47.0</gml:Y>
            </gml:coord>
            <gml:coord>
                <gml:X>121.0</gml:X>
                <gml:Y>-45.0</gml:Y>
            </gml:coord>
        </gml:Box>
    </gml:boundedBy>
    <gml:featureMember>
        <gsf:houses fid="fid-1dc41567_131b1fc196d_-8000">
            <gml:name>House</gml:name>
            <gsf:geom>
                <gml:Point>
                    <gml:coord>
                        <gml:X>111.0</gml:X>
                        <gml:Y>-47.0</gml:Y>
                    </gml:coord>
                </gml:Point>
            </gsf:geom>
            <gsf:price>12.5</gsf:price>
        </gsf:houses>
    </gml:featureMember>
    <gml:featureMember>
        <gsf:houses fid="fid-1dc41567_131b1fc196d_-7fff">
            <gml:name>School</gml:name>
            <gsf:geom>
                <gml:Point>
                    <gml:coord>
                        <gml:X>121.0</gml:X>
                        <gml:Y>-45.0</gml:Y>
                    </gml:coord>
                </gml:Point>
            </gsf:geom>
            <gsf:price>22.7</gsf:price>
        </gsf:houses>
    </gml:featureMember>
</wfs:FeatureCollection>"""

        GmlReader reader = new GmlReader()

        // Read from a String
        Layer layer = reader.read(gml)
        assertNotNull layer
        assertEquals "gml", layer.name
        assertTrue layer.workspace instanceof Memory
        assertNull layer.proj
        assertEquals 2, layer.count

        // Read from a String with custom workspace, name, projection
        layer = reader.read(gml, workspace: new Memory(), name: "points", projection: new Projection("EPSG:4326"))
        assertNotNull layer
        assertEquals "points", layer.name
        assertTrue layer.workspace instanceof Memory
        assertEquals "EPSG:4326", layer.proj.id
        assertEquals 2, layer.count

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(gml.getBytes("UTF-8"))
        layer = reader.read(input)
        assertNotNull layer
        assertEquals "gml", layer.name
        assertTrue layer.workspace instanceof Memory
        assertNull layer.proj
        assertEquals 2, layer.count

        // Read from a File
        File file = folder.newFile("layer.gml")
        file.write(gml)
        layer = reader.read(file)
        assertNotNull layer
        assertEquals "gml", layer.name
        assertTrue layer.workspace instanceof Memory
        assertNull layer.proj
        assertEquals 2, layer.count

    }

}

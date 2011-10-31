package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.Layer

/**
 * The GmlReader UnitTest
 * @author Jared Erickson
 */
class GmlReaderTestCase {

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
        assertEquals 2, layer.count

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(gml.getBytes("UTF-8"))
        layer = reader.read(input)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from a File
        File file = File.createTempFile("layer",".gml")
        file.write(gml)
        layer = reader.read(file)
        assertNotNull layer
        assertEquals 2, layer.count

    }

}

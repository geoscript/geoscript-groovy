package geoscript.layer.io

import org.junit.Test
import static org.junit.Assert.*
import geoscript.layer.Layer

/**
 * The GeoJSONReader UnitTest
 * @author Jared Erickson
 */
class GeoJSONReaderTestCase {

    @Test void read() {

        String json = """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"fid-3eff7fce_131b538ad4c_-8000"},{"type":"Feature","geometry":{"type":"Point","coordinates":[121,-45]},"properties":{"name":"School","price":22.7},"id":"fid-3eff7fce_131b538ad4c_-7fff"}]}"""

        GeoJSONReader reader = new GeoJSONReader()

        // Read from a String
        Layer layer = reader.read(json)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes("UTF-8"))
        layer = reader.read(input)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from a File
        File file = File.createTempFile("layer",".json")
        file.write(json)
        layer = reader.read(file)
        assertNotNull layer
        assertEquals 2, layer.count
    }

}

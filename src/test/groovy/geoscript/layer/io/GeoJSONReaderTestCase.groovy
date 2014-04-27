package geoscript.layer.io

import geoscript.proj.Projection
import geoscript.workspace.Memory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*
import geoscript.layer.Layer

/**
 * The GeoJSONReader UnitTest
 * @author Jared Erickson
 */
class GeoJSONReaderTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void read() {

        String json = """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"fid-3eff7fce_131b538ad4c_-8000"},{"type":"Feature","geometry":{"type":"Point","coordinates":[121,-45]},"properties":{"name":"School","price":22.7},"id":"fid-3eff7fce_131b538ad4c_-7fff"}]}"""

        GeoJSONReader reader = new GeoJSONReader()

        // Read from a String
        Layer layer = reader.read(json, uniformSchema: true)
        assertNotNull layer
        assertEquals "geojson", layer.name
        assertTrue layer.workspace instanceof Memory
        assertNull layer.proj
        assertEquals 2, layer.count

        // Read from a String with custom workspace, name, projection
        layer = reader.read(json, workspace: new Memory(), name: "points", projection: new Projection("EPSG:4326"), uniformSchema: true)
        assertNotNull layer
        assertEquals "points", layer.name
        assertTrue layer.workspace instanceof Memory
        assertEquals "EPSG:4326", layer.proj.id
        assertEquals 2, layer.count

        // Read from an InputStream
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes("UTF-8"))
        layer = reader.read(input, uniformSchema: true)
        assertNotNull layer
        assertEquals 2, layer.count

        // Read from a File
        File file = folder.newFile("layer.json")
        file.write(json)
        layer = reader.read(file, uniformSchema: true)
        assertNotNull layer
        assertEquals 2, layer.count
    }

    @Test void readWithDifferentProperties() {
        String json = """{"type":"FeatureCollection","features":[
            {"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House"},"id":"fid-3eff7fce_131b538ad4c_-8000"},
            {"type":"Feature","geometry":{"type":"Point","coordinates":[121,-45]},"properties":{"price":22.7},"id":"fid-3eff7fce_131b538ad4c_-7fff"}
        ]}"""

        GeoJSONReader reader = new GeoJSONReader()

        // Read from a String
        Layer layer = reader.read(json)
        assertNotNull layer
        assertEquals "geojson", layer.name
        assertTrue layer.workspace instanceof Memory
        assertNull layer.proj
        assertEquals 2, layer.count
        assertTrue layer.schema.has("name")
        assertTrue layer.schema.has("price")
    }

}

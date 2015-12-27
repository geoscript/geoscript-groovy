package geoscript

import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * Created by jericks on 11/21/15.
 */
class GeoScriptTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void geoJsonFileAsLayer() {
        String json = """{"type":"FeatureCollection","features":[{"type":"Feature","geometry":{"type":"Point","coordinates":[111,-47]},"properties":{"name":"House","price":12.5},"id":"fid-3eff7fce_131b538ad4c_-8000"},{"type":"Feature","geometry":{"type":"Point","coordinates":[121,-45]},"properties":{"name":"School","price":22.7},"id":"fid-3eff7fce_131b538ad4c_-7fff"}]}"""
        File jsonFile = folder.newFile("layer.json")
        jsonFile.write(json)
        use(GeoScript) {
            Layer layer = jsonFile as Layer
            assertEquals("geojson", layer.name)
            assertTrue(layer.schema.has("name"))
            assertTrue(layer.schema.has("price"))
            assertTrue(layer.schema.has("geometry"))
            assertEquals(2, layer.count)
            layer.eachFeature { f ->
                assertTrue(f.geom instanceof geoscript.geom.Point)
            }
        }
    }

}

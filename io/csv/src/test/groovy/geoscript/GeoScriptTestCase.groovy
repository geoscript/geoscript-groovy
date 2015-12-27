package geoscript

import geoscript.layer.Layer
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class GeoScriptTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void csvFileAsLayer() {
        String csv = """"geom","name","price"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        File csvFile = folder.newFile("layer.csv")
        csvFile.write(csv)
        use(GeoScript) {
            Layer layer = csvFile as Layer
            assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
            assertEquals(2, layer.count)
            layer.eachFeature { f ->
                assertTrue(f.geom instanceof geoscript.geom.Point)
            }
        }
    }

}

package geoscript

import geoscript.layer.Layer
import geoscript.workspace.Workspace
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class GeoScriptTestCase {

    @Test void fileAsShapefile() {
        use(GeoScript) {
            File file = new File(getClass().getClassLoader().getResource("states.shp").toURI())
            Layer shp = file as Layer
            assertEquals 49, shp.count
        }
    }

    @Test void stringAsWorkspace() {
        use(GeoScript) {
            URL url = getClass().getClassLoader().getResource("states.shp")
            String str = "url='${url}' 'create spatial index'=true".toString()
            Workspace w = str as Workspace
            assertNotNull(w)
            assertEquals("Directory", w.format)
        }
    }

    @Test void mapAsWorkspace() {
        use(GeoScript) {
            URL url = getClass().getClassLoader().getResource("states.shp")
            Workspace w = ["url": url] as Workspace
            assertNotNull(w)
            assertEquals("Directory", w.format)
        }
    }
}

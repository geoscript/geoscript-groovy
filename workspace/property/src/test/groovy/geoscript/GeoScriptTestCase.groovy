package geoscript

import geoscript.layer.Layer
import org.junit.Test

import static org.junit.Assert.assertEquals

class GeoScriptTestCase {

    @Test void fileAsProperty() {
        use(GeoScript) {
            File file = new File(getClass().getClassLoader().getResource("points.properties").toURI())
            Layer prop = file as Layer
            assertEquals 4, prop.count
        }
    }

}
package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.*

class LayerTestCase {

    @Test void toJSON() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toJSON(out)
        String json = out.toString()
        assertNotNull json
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
        json = layer1.toJSONString()
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
    }

}

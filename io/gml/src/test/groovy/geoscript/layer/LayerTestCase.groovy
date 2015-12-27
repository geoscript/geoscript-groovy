package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import org.junit.Test
import static org.junit.Assert.*

class LayerTestCase {

    @Test void toGML() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        def out = new java.io.ByteArrayOutputStream()
        layer1.toGML(out)
        String gml = out.toString()
        assertNotNull gml
    }

}

package geoscript.layer

import geoscript.feature.Feature
import geoscript.feature.Field
import geoscript.feature.Schema
import geoscript.geom.Point
import groovy.json.JsonSlurper
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class LayerTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void toJSON() {
        Schema s1 = new Schema("facilities", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        Layer layer1 = new Layer("facilities", s1)
        layer1.add(new Feature([new Point(111,-47), "House", 12.5], "house1", s1))
        // OutputStream
        def out = new java.io.ByteArrayOutputStream()
        layer1.toJSON(out)
        String json = out.toString()
        assertNotNull json
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
        // File
        File file = folder.newFile("layer.json")
        layer1.toJSONFile(file)
        json = file.text
        assertNotNull json
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
        // String
        json = layer1.toJSONString()
        assertNotNull json
        assertTrue json.startsWith("{\"type\":\"FeatureCollection\"")
    }

    @Test void toJSONWithOptions() {
        Schema schema = new Schema("facilities", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Layer layer = new Layer("facilities", schema)
        layer.add(new Feature([new Point(111.123456,-47.123456), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121.123456,-45.123456), "School", 22.7], "house2", schema))
        // OutputStream
        def out = new java.io.ByteArrayOutputStream()
        layer.toJSON(out, decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        String json = out.toString()
        assertNotNull json
        checkJson(json)
        // File
        File file = folder.newFile("layer.json")
        layer.toJSONFile(file, decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        json = file.text
        checkJson(json)
        // String
        json = layer.toJSONString(decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        checkJson(json)
    }

    private void checkJson(String geojson) {
        JsonSlurper slurper = new JsonSlurper()
        Map json = slurper.parseText(geojson)
        assertEquals("FeatureCollection", json.type)
        assertTrue json.containsKey("bbox")
        assertTrue json.containsKey("crs")
        assertTrue json.containsKey("features")
        List features = json.features
        assertEquals(2, features.size())
        features.each { Map feature ->
            assertEquals("Feature", feature.type)
            assertTrue feature.containsKey("bbox")
            assertTrue feature.containsKey("crs")
            assertTrue feature.containsKey("geometry")
            assertTrue feature.containsKey("properties")
        }
    }

}

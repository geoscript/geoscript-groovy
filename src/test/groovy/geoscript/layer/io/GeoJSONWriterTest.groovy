package geoscript.layer.io

import groovy.json.JsonSlurper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

import static org.junit.jupiter.api.Assertions.*
import geoscript.geom.Point
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.feature.Feature

/**
 * The GeoJSONWriter UnitTest
 * @author Jared Erickson
 */
class GeoJSONWriterTest {

    @TempDir
    File folder

    @Test void write() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))

        String expected = ""

        // Write the Layer to a GeoJSON String
        GeoJSONWriter writer = new GeoJSONWriter()
        String geojson = writer.write(layer)
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")

        // Write Layer as GeoJSON to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        geojson = out.toString()
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")

        // Write Layer as GeoJSON to a File
        File file = new File(folder,"layer.json")
        writer.write(layer, file)
        geojson = file.text
        assertTrue geojson.startsWith("{\"type\":\"FeatureCollection\",\"features\":[")
    }

    @Test void writeWithOptions() {

        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point","EPSG:4326"), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111.123456,-47.123456), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121.123456,-45.123456), "School", 22.7], "house2", schema))

        String expected = ""

        // Write the Layer to a GeoJSON String
        GeoJSONWriter writer = new GeoJSONWriter()
        String geojson = writer.write(layer, decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        checkJson(geojson)

        // Write Layer as GeoJSON to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out, decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        out.close()
        geojson = out.toString()
        checkJson(geojson)

        // Write Layer as GeoJSON to a File
        File file = new File(folder,"layer.json")
        writer.write(layer, file, decimals: 6, encodeFeatureBounds: true, encodeFeatureCRS: true,
                encodeFeatureCollectionBounds: true, encodeFeatureCollectionCRS: true)
        geojson = file.text
        checkJson(geojson)

        // Pretty print
        geojson = writer.write(layer, prettyPrint: true)
        assertTrue geojson.split("\n").length > 1
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

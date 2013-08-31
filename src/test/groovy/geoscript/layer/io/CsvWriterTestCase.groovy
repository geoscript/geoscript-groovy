package geoscript.layer.io

import geoscript.proj.Projection
import org.junit.Test
import geoscript.feature.Schema
import geoscript.feature.Field
import geoscript.workspace.Memory
import geoscript.layer.Layer
import geoscript.feature.Feature
import geoscript.geom.Point

import static org.junit.Assert.assertEquals

/**
 * The CsvWriter Unit Test
 * @author Jared Erickson
 */
class CsvWriterTestCase {

    private Layer createLayer(Projection proj = null) {
        // Create a simple Schema
        Schema schema = new Schema("houses", [new Field("geom","Point", proj), new Field("name","string"), new Field("price","float")])

        // Create a Layer in memory with a couple of Features
        Memory memory = new Memory()
        Layer layer = memory.create(schema)
        layer.add(new Feature([new Point(111,-47), "House", 12.5], "house1", schema))
        layer.add(new Feature([new Point(121,-45), "School", 22.7], "house2", schema))
        layer
    }

    @Test void writeWTK() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeWTKWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter()
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeWTKWithTypesAndProjection() {

        Layer layer = createLayer(new Projection("EPSG:4326"))

        String expected = """"geom:Point:EPSG:4326","name:String","price:Float"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter()
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeXY() {

        Layer layer = createLayer()

        String expected = """"lon"|"lat"|"name"|"price"
"111.0"|"-47.0"|"House"|"12.5"
"121.0"|"-45.0"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat", separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeXYWithTypes() {

        Layer layer = createLayer()

        String expected = """"lon:Double"|"lat:Double"|"name:String"|"price:Float"
"111.0"|"-47.0"|"House"|"12.5"
"121.0"|"-45.0"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat", separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)

        // Write Layer as CSV to an OutputStream
        ByteArrayOutputStream out = new ByteArrayOutputStream()
        writer.write(layer, out)
        out.close()
        csv = out.toString()
        assertEquals(expected, csv)

        // Write Layer as CSV to a File
        File file = File.createTempFile("layer",".csv")
        writer.write(layer, file)
        csv = file.text
        assertEquals(expected, csv)
    }

    @Test void writeXYInSingleColumn() {

        Layer layer = createLayer()

        String expected = """"geom"|"name"|"price"
"111.0,-47.0"|"House"|"12.5"
"121.0,-45.0"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.XY, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeXYInSingleColumnWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point"|"name:String"|"price:Float"
"111.0,-47.0"|"House"|"12.5"
"121.0,-45.0"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.XY, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMS() {

        Layer layer = createLayer()

        String expected = """"lon"|"lat"|"name"|"price"
"111\u00B0 0' 0.0000"" W"|" -47\u00B0 0' 0.0000"" N"|"House"|"12.5"
"121\u00B0 0' 0.0000"" W"|" -45\u00B0 0' 0.0000"" N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DMS, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSWithTypes() {

        Layer layer = createLayer()

        String expected = """"lon:String"|"lat:String"|"name:String"|"price:Float"
"111\u00B0 0' 0.0000"" W"|" -47\u00B0 0' 0.0000"" N"|"House"|"12.5"
"121\u00B0 0' 0.0000"" W"|" -45\u00B0 0' 0.0000"" N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DMS, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSInSingleColumn() {

        Layer layer = createLayer()

        String expected = """"geom"|"name"|"price"
"111\u00B0 0' 0.0000"" W, -47\u00B0 0' 0.0000"" N"|"House"|"12.5"
"121\u00B0 0' 0.0000"" W, -45\u00B0 0' 0.0000"" N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DMS, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSInSingleColumnWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point"|"name:String"|"price:Float"
"111\u00B0 0' 0.0000"" W, -47\u00B0 0' 0.0000"" N"|"House"|"12.5"
"121\u00B0 0' 0.0000"" W, -45\u00B0 0' 0.0000"" N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DMS, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSChar() {

        Layer layer = createLayer()

        String expected = """"lon"|"lat"|"name"|"price"
"111d 0m 0.0000s W"|" -47d 0m 0.0000s N"|"House"|"12.5"
"121d 0m 0.0000s W"|" -45d 0m 0.0000s N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DMSChar, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSCharWithTypes() {

        Layer layer = createLayer()

        String expected = """"lon:String"|"lat:String"|"name:String"|"price:Float"
"111d 0m 0.0000s W"|" -47d 0m 0.0000s N"|"House"|"12.5"
"121d 0m 0.0000s W"|" -45d 0m 0.0000s N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DMSChar, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSCharInSingleColumn() {

        Layer layer = createLayer()

        String expected = """"geom"|"name"|"price"
"111d 0m 0.0000s W, -47d 0m 0.0000s N"|"House"|"12.5"
"121d 0m 0.0000s W, -45d 0m 0.0000s N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DMSChar, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDMSCharInSingleColumnWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point"|"name:String"|"price:Float"
"111d 0m 0.0000s W, -47d 0m 0.0000s N"|"House"|"12.5"
"121d 0m 0.0000s W, -45d 0m 0.0000s N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DMSChar, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDM() {

        Layer layer = createLayer()

        String expected = """"lon"|"lat"|"name"|"price"
"111\u00B0 0.0000' W"|" -47\u00B0 0.0000' N"|"House"|"12.5"
"121\u00B0 0.0000' W"|" -45\u00B0 0.0000' N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DDM, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMWithTypes() {

        Layer layer = createLayer()

        String expected = """"lon:String"|"lat:String"|"name:String"|"price:Float"
"111\u00B0 0.0000' W"|" -47\u00B0 0.0000' N"|"House"|"12.5"
"121\u00B0 0.0000' W"|" -45\u00B0 0.0000' N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DDM, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMInSingleColumn() {

        Layer layer = createLayer()

        String expected = """"geom"|"name"|"price"
"111\u00B0 0.0000' W, -47\u00B0 0.0000' N"|"House"|"12.5"
"121\u00B0 0.0000' W, -45\u00B0 0.0000' N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DDM, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMInSingleColumnWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point"|"name:String"|"price:Float"
"111\u00B0 0.0000' W, -47\u00B0 0.0000' N"|"House"|"12.5"
"121\u00B0 0.0000' W, -45\u00B0 0.0000' N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DDM, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMChar() {

        Layer layer = createLayer()

        String expected = """"lon"|"lat"|"name"|"price"
"111d 0.0000m W"|" -47d 0.0000m N"|"House"|"12.5"
"121d 0.0000m W"|" -45d 0.0000m N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DDMChar, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMCharWithTypes() {

        Layer layer = createLayer()

        String expected = """"lon:String"|"lat:String"|"name:String"|"price:Float"
"111d 0.0000m W"|" -47d 0.0000m N"|"House"|"12.5"
"121d 0.0000m W"|" -45d 0.0000m N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter("lon","lat",CsvWriter.Type.DDMChar, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMCharInSingleColumn() {

        Layer layer = createLayer()

        String expected = """"geom"|"name"|"price"
"111d 0.0000m W, -47d 0.0000m N"|"House"|"12.5"
"121d 0.0000m W, -45d 0.0000m N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DDMChar, separator: "|")
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeDDMCharInSingleColumnWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point"|"name:String"|"price:Float"
"111d 0.0000m W, -47d 0.0000m N"|"House"|"12.5"
"121d 0.0000m W, -45d 0.0000m N"|"School"|"22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.DDMChar, separator: "|", encodeFieldType: true)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeWTB() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"0000000001405BC00000000000C047800000000000","House","12.5"
"0000000001405E400000000000C046800000000000","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.WKB, encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeWTBWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"0000000001405BC00000000000C047800000000000","House","12.5"
"0000000001405E400000000000C046800000000000","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.WKB)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGeoJSON() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"{ ""type"": ""Point"", ""coordinates"": [111.0, -47.0] }","House","12.5"
"{ ""type"": ""Point"", ""coordinates"": [121.0, -45.0] }","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GEOJSON, encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGeoJSONWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"{ ""type"": ""Point"", ""coordinates"": [111.0, -47.0] }","House","12.5"
"{ ""type"": ""Point"", ""coordinates"": [121.0, -45.0] }","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GEOJSON)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeKML() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"<Point><coordinates>111.0,-47.0</coordinates></Point>","House","12.5"
"<Point><coordinates>121.0,-45.0</coordinates></Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.KML, encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeKMLWithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"<Point><coordinates>111.0,-47.0</coordinates></Point>","House","12.5"
"<Point><coordinates>121.0,-45.0</coordinates></Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.KML)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGML2() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>","House","12.5"
"<gml:Point><gml:coordinates>121.0,-45.0</gml:coordinates></gml:Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GML2, encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGML2WithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>","House","12.5"
"<gml:Point><gml:coordinates>121.0,-45.0</gml:coordinates></gml:Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GML2)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGML3() {

        Layer layer = createLayer()

        String expected = """"geom","name","price"
"<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>","House","12.5"
"<gml:Point><gml:pos>121.0 -45.0</gml:pos></gml:Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GML3, encodeFieldType: false)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }

    @Test void writeGML3WithTypes() {

        Layer layer = createLayer()

        String expected = """"geom:Point","name:String","price:Float"
"<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>","House","12.5"
"<gml:Point><gml:pos>121.0 -45.0</gml:pos></gml:Point>","School","22.7"
"""
        // Write the Layer to a CSV String
        CsvWriter writer = new CsvWriter(CsvWriter.Type.GML3)
        String csv = writer.write(layer)
        assertEquals(expected, csv)
    }
}

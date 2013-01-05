package geoscript.layer.io

import org.junit.Test
import geoscript.layer.Layer
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * The CsvReader UnitTest
 * @author Jared Erickson
 */
class CsvReaderTestCase {

    @Test void readWKT() {
        String csv = """"geom","name","price"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readWKTLines() {
        String csv = """"geom","name","price"
"LINESTRING (0 0, 5 5)","House","12.5"
"LINESTRING (3 3, 12 12)","School","22.7"
"""
        CsvReader reader = new CsvReader("geom")
        Layer layer = reader.read(csv)
        assertEquals("csv geom: LineString, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.LineString)
        }
    }

    @Test void readXYInOneField() {
        String csv = """"name","price","xy"
"House","12.5", 111 -47
"School","22.7",121 -45
"""
        CsvReader reader = new CsvReader("xy", CsvReader.Type.XY)
        Layer layer = reader.read(csv)
        assertEquals("csv name: String, price: String, xy: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readXY() {
        String csv = """"name","price","x","y"
"House","12.5",111,-47
"School","22.7",121,-45
"""
        CsvReader reader = new CsvReader("x","y")
        Layer layer = reader.read(csv)
        assertEquals("csv name: String, price: String, x: String, y: String, geom: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readLatLon() {
        String csv = """"lon"|"lat"|"name"|"price"
"111.0"|"-47.0"|"House"|"12.5"
"121.0"|"-45.0"|"School"|"22.7"
"""
        CsvReader reader = new CsvReader("lon","lat", separator: "|")
        Layer layer = reader.read(csv)
        assertEquals("csv lon: String, lat: String, name: String, price: String, geom: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readDMS() {
        String csv = """"lon"|"lat"|"name"|"price"
"-122\u00B0 31' 32.2284\\" W"|"47\u00B0 12' 43.2828\\" N"|"House"|"12.5"
"-123\u00B0 15' 21.4821\\" W"|"43\u00B0 34' 12.9857\\" N"|"School"|"22.7"
"""
        CsvReader reader = new CsvReader("lon","lat", separator: "|")
        Layer layer = reader.read(csv)
        assertEquals("csv lon: String, lat: String, name: String, price: String, geom: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readUsgsEarthQuakes() {
        String csv = """Src,EqId,Version,Datetime,Lat,Lon,Magnitude,Depth,NST,Region
ak,10501931,?,"Thursday, June 28, 2012 03:15:59 UTC",63.17,-150.6201,1,125.4,?,"87km WSW of Cantwell, Alaska"
ci,11128130,?,"Thursday, June 28, 2012 03:12:52 UTC",34.5023,-116.5243,2,6.7,?,"39km NE of Big Bear City, California"
ak,10501928,?,"Thursday, June 28, 2012 02:57:34 UTC",57.8956,-156.779,1.8,13.5,?,"88km S of King Salmon, Alaska"
ak,10501917,?,"Thursday, June 28, 2012 02:30:58 UTC",60.0233,-152.9946,2,2.9,?,"53km SSW of Redoubt Volcano, Alaska"
"""
        CsvReader reader = new CsvReader("Lon","Lat")
        Layer layer = reader.read(csv)
        assertEquals("csv Src: String, EqId: String, Version: String, Datetime: String, Lat: String, Lon: String, Magnitude: String, Depth: String, NST: String, Region: String, geom: Point", layer.schema.toString())
        assertEquals(4, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readUSGSGNIS() {
        String csv = """FEATURE_ID|FEATURE_NAME|FEATURE_CLASS|STATE_ALPHA|STATE_NUMERIC|COUNTY_NAME|COUNTY_NUMERIC|PRIMARY_LAT_DMS|PRIM_LONG_DMS|PRIM_LAT_DEC|PRIM_LONG_DEC|SOURCE_LAT_DMS|SOURCE_LONG_DMS|SOURCE_LAT_DEC|SOURCE_LONG_DEC|ELEV_IN_M|ELEV_IN_FT|MAP_NAME|DATE_CREATED|DATE_EDITED
233358|Pacific Crest National Scenic Trail|Trail|CA|06|San Diego|073|490001N|1204808W|49.00021|-120.802102|||||1298|4258|Castle Peak OE N|01/19/1981|02/02/2012
247074|Pacific Ocean|Sea|CA|06|Mendocino|045|391837N|1235041W|39.3102778|-123.8447222|||||0|0|Mendocino|01/19/1981|05/16/2011
252293|Coast Ranges|Range|CA|06|Trinity|105|410003N|1230254W|41.0006957|-123.0483683|||||2721|8927|Thompson Peak|04/01/1991|
371761|Buffalo Rapids|Rapids|ID|16|Nez Perce|069|461101N|1165627W|46.1834911|-116.940705|||||236|774|Captain John Rapids|06/21/1979|
371844|Cable Creek|Stream|WA|53|Spokane|063|474126N|1170356W|47.6904552|-117.0654781|473729N|1170044W|47.6246236|-117.0121389|616|2021|Liberty Lake|09/10/1979|
"""
        CsvReader reader = new CsvReader("PRIM_LONG_DEC","PRIM_LAT_DEC", separator: "|")
        Layer layer = reader.read(csv)
        assertEquals("csv FEATURE_ID: String, FEATURE_NAME: String, FEATURE_CLASS: String, STATE_ALPHA: String, STATE_NUMERIC: String, COUNTY_NAME: String, COUNTY_NUMERIC: String, PRIMARY_LAT_DMS: String, PRIM_LONG_DMS: String, PRIM_LAT_DEC: String, PRIM_LONG_DEC: String, SOURCE_LAT_DMS: String, SOURCE_LONG_DMS: String, SOURCE_LAT_DEC: String, SOURCE_LONG_DEC: String, ELEV_IN_M: String, ELEV_IN_FT: String, MAP_NAME: String, DATE_CREATED: String, DATE_EDITED: String, geom: Point", layer.schema.toString())
        assertEquals(5, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readOpenLayersText() {
        String csv = """lat	lon	title	description	icon	iconSize	iconOffset
48.9459301	9.6075669	Title One	Description one<br>Second line.<br><br>(click again to close)	Ol_icon_blue_example.png	24,24	0,-24
48.9899851	9.5382032	Title Two	Description two.	Ol_icon_red_example.png	16,16	-8,-8
"""
        CsvReader reader = new CsvReader("lon","lat", separator: "\t")
        Layer layer = reader.read(csv)
        assertEquals("csv lat: String, lon: String, title: String, description: String, icon: String, iconSize: String, iconOffset: String, geom: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readGeoJson() {
        String csv = """"geom","name","price"
"{ ""type"": ""Point"", ""coordinates"": [111.0, -47.0] }","House","12.5"
"{ ""type"": ""Point"", ""coordinates"": [121.0, -45.0] }","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GEOJSON)
        Layer layer = reader.read(csv)
        println layer.features
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readWkb() {
        String csv = """"geom","name","price"
"0000000001405BC00000000000C047800000000000","House","12.5"
"0000000001405E400000000000C046800000000000","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.WKB)
        Layer layer = reader.read(csv)
        println layer.features
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readKml() {
        String csv = """"geom","name","price"
"<Point><coordinates>111.0,-47.0</coordinates></Point>","House","12.5"
"<Point><coordinates>121.0,-45.0</coordinates></Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.KML)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readGml2() {
        String csv = """"geom","name","price"
"<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>","House","12.5"
"<gml:Point><gml:coordinates>121.0,-45.0</gml:coordinates></gml:Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GML2)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readGml3() {
        String csv = """"geom","name","price"
"<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>","House","12.5"
"<gml:Point><gml:pos>121.0 -45.0</gml:pos></gml:Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GML3)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readCsvWithBlankLine() {
        String csv = """"the_geom"
"POLYGON ((-126.72872789292975 22.964486856824095, -126.72872789292975 51.368178553333294, -64.97359027747028 51.368178553333294, -64.97359027747028 22.964486856824095, -126.72872789292975 22.964486856824095))"

"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(1, layer.count)
    }

    @Test void readCsvFromKml() {
        String csv = """"name","visibility","open","address","phoneNumber","description","LookAt","Style","Region","Geometry"
,"true","true",,,,,,,"POINT (1 1)"

"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(1, layer.count)
    }
}

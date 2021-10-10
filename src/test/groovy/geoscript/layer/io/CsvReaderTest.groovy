package geoscript.layer.io

import org.junit.jupiter.api.Test
import geoscript.layer.Layer
import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

/**
 * The CsvReader UnitTest
 * @author Jared Erickson
 */
class CsvReaderTest {

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

    @Test void readWKTWithTypes() {
        String csv = """"geom:Point","name:String","price:double"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point, name: String, price: Double", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readWKTWithSemiColons() {
        String csv = """"geom:geom","name:x","price:y"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals("csv geom:geom: Point, name:x: String, price:y: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readWKTWithTypesAndProj() {
        String csv = """"geom:Point:EPSG:4326","name:String","price:Double"
"POINT (111 -47)","House","12.5"
"POINT (121 -45)","School","22.7"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Double", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readXYInOneFieldWithTypes() {
        String csv = """"name:String","price:Float","xy:Point"
"House","12.5", 111 -47
"School","22.7",121 -45
"""
        CsvReader reader = new CsvReader("xy", CsvReader.Type.XY)
        Layer layer = reader.read(csv)
        assertEquals("csv name: String, price: Float, xy: Point", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readXYInOneFieldWithTypesAndProj() {
        String csv = """"name:String","price:Float","xy:Point:EPSG:4326"
"House","12.5", 111 -47
"School","22.7",121 -45
"""
        CsvReader reader = new CsvReader("xy", CsvReader.Type.XY)
        Layer layer = reader.read(csv)
        assertEquals("csv name: String, price: Float, xy: Point(EPSG:4326)", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readXYWithTypes() {
        String csv = """"name:String","price:Float","x:Double","y:Double"
"House","12.5",111,-47
"School","22.7",121,-45
"""
        CsvReader reader = new CsvReader("x","y")
        Layer layer = reader.read(csv)
        assertEquals("csv name: String, price: Float, x: Double, y: Double, geom: Point", layer.schema.toString())
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

    @Test void readLatLonWithTypes() {
        String csv = """"lon:Double"|"lat:Double"|"name:String"|"price:Float"
"111.0"|"-47.0"|"House"|"12.5"
"121.0"|"-45.0"|"School"|"22.7"
"""
        CsvReader reader = new CsvReader("lon","lat", separator: "|")
        Layer layer = reader.read(csv)
        assertEquals("csv lon: Double, lat: Double, name: String, price: Float, geom: Point", layer.schema.toString())
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

    @Test void readDMSWithTypes() {
        String csv = """"lon:String"|"lat:String"|"name:String"|"price:Float"
"-122\u00B0 31' 32.2284\\" W"|"47\u00B0 12' 43.2828\\" N"|"House"|"12.5"
"-123\u00B0 15' 21.4821\\" W"|"43\u00B0 34' 12.9857\\" N"|"School"|"22.7"
"""
        CsvReader reader = new CsvReader("lon","lat", separator: "|")
        Layer layer = reader.read(csv)
        assertEquals("csv lon: String, lat: String, name: String, price: Float, geom: Point", layer.schema.toString())
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
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readGeoJsonWithTypes() {
        String csv = """"geom:Point:EPSG:4326","name:String","price:Float"
"{ ""type"": ""Point"", ""coordinates"": [111.0, -47.0] }","House","12.5"
"{ ""type"": ""Point"", ""coordinates"": [121.0, -45.0] }","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GEOJSON)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Float", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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
        assertEquals("csv geom: Point, name: String, price: String", layer.schema.toString())
        assertEquals(2, layer.count)
        layer.eachFeature { f ->
            assertTrue(f.geom instanceof geoscript.geom.Point)
        }
    }

    @Test void readWkbWithTypes() {
        String csv = """"geom:Point:EPSG:4326","name:String","price:Float"
"0000000001405BC00000000000C047800000000000","House","12.5"
"0000000001405E400000000000C046800000000000","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.WKB)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Float", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readKmlWithTypes() {
        String csv = """"geom:Point:EPSG:4326","name:String","price:Float"
"<Point><coordinates>111.0,-47.0</coordinates></Point>","House","12.5"
"<Point><coordinates>121.0,-45.0</coordinates></Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.KML)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Float", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readGml2WithTypes() {
        String csv = """"geom:Point:EPSg:4326","name:String","price:Float"
"<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>","House","12.5"
"<gml:Point><gml:coordinates>121.0,-45.0</gml:coordinates></gml:Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GML2)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Float", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readGml3WithTypes() {
        String csv = """"geom:Point:EPSG:4326","name:String","price:Float"
"<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>","House","12.5"
"<gml:Point><gml:pos>121.0 -45.0</gml:pos></gml:Point>","School","22.7"
"""
        CsvReader reader = new CsvReader("geom", CsvReader.Type.GML3)
        Layer layer = reader.read(csv)
        assertEquals("csv geom: Point(EPSG:4326), name: String, price: Float", layer.schema.toString())
        assertEquals "EPSG:4326", layer.proj.id
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

    @Test void readCsvWithBlankValues() {
        String csv = """"name","visibility","open","address","phoneNumber","description","LookAt","Style","Region","Geometry"
,"true","true",,,,,,,"POINT (1 1)"

"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(1, layer.count)
    }

    @Test void readNoFeatures() {
        String csv = """"geom","id","name"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(0, layer.count)
        assertEquals("csv geom: Point, id: String, name: String", layer.schema.toString())
    }

    @Test void readNoFeaturesWithType() {
        String csv = """"geom:Point:EPSG:4326","id:int","name:String"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(0, layer.count)
        assertEquals("csv geom: Point(EPSG:4326), id: Integer, name: String", layer.schema.toString())
    }

    @Test void readNoFeaturesWithLineStringType() {
        String csv = """"abc:LineString:EPSG:2927","id:int","name:String"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(0, layer.count)
        assertEquals("csv abc: LineString(EPSG:2927), id: Integer, name: String", layer.schema.toString())
    }

    @Test void readNoFeatureWithPolygonType() {
        String csv = "\"the_geom:MultiPolygon:EPSG:4326\",\"STATE_NAME:String\",\"STATE_FIPS:String\"," +
                "\"SUB_REGION:String\",\"STATE_ABBR:String\",\"LAND_KM:Double\",\"WATER_KM:Double\"," +
                "\"PERSONS:Double\",\"FAMILIES:Double\",\"HOUSHOLD:Double\",\"MALE:Double\"," +
                "\"FEMALE:Double\",\"WORKERS:Double\",\"DRVALONE:Double\",\"CARPOOL:Double\"," +
                "\"PUBTRANS:Double\",\"EMPLOYED:Double\",\"UNEMPLOY:Double\",\"SERVICE:Double\"," +
                "\"MANUAL:Double\",\"P_MALE:Double\",\"P_FEMALE:Double\",\"SAMP_POP:Double\""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(0, layer.count)
        assertEquals("csv the_geom: MultiPolygon(EPSG:4326), STATE_NAME: String, STATE_FIPS: String, " +
                "SUB_REGION: String, STATE_ABBR: String, LAND_KM: Double, WATER_KM: Double, PERSONS: Double, " +
                "FAMILIES: Double, HOUSHOLD: Double, MALE: Double, FEMALE: Double, WORKERS: Double, " +
                "DRVALONE: Double, CARPOOL: Double, PUBTRANS: Double, EMPLOYED: Double, UNEMPLOY: Double, " +
                "SERVICE: Double, MANUAL: Double, P_MALE: Double, P_FEMALE: Double, SAMP_POP: Double",
                layer.schema.toString())
    }

    @Test void readFromWktWithNoSpace() {
        String csv = """the_geom,ADMIN_NAME
POINT(10.1999998092651 59.7000007629395),Buskerud
POINT(-2.96670007705688 56.4667015075684),Scotland
POINT(-4.85678577423096 55.736743927002),Scotland
POINT(14.7166996002197 55.11669921875),Bornholm
POINT(69.2166976928711 54.88330078125),North Kazakhstan
POINT(-1.16670000553131 54.5999984741211),England
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(6, layer.count)
        assertEquals("csv the_geom: Point, ADMIN_NAME: String", layer.schema.toString())
    }

    @Test void readPointsAndPolygons() {
        String csv = """"geom:Geometry"
"POINT (79.51659774435456 64.56209440460259)"
"POINT (56.71497354298235 52.39700510300415)"
"POLYGON ((13.885584941145911 44.681478564642596, 13.693437745178215 42.730575344481316, 13.124380266258779 40.854644240991696, 12.200281064171364 39.125776234446576, 10.956652753011387 37.61041075277712, 9.441287271341935 36.36678244161715, 7.712419264796811 35.44268323952973, 5.836488161307195 34.873625760610295, 3.8855849411459125 34.681478564642596, 1.93468172098463 34.873625760610295, 0.058750617495015 35.44268323952973, -1.670117389050108 36.36678244161715, -3.1854828707195626 37.61041075277712, -4.429111181879541 39.125776234446576, -5.353210383966956 40.8546442409917, -5.922267862886394 42.730575344481316, -6.114415058854088 44.6814785646426, -5.9222678628863905 46.63238178480389, -5.3532103839669505 48.5083128882935, -4.4291111818795335 50.23718089483863, -3.185482870719551 51.752546376508086, -1.6701173890500955 52.99617468766806, 0.0587506174950332 53.92027388975547, 1.9346817209846514 54.489331368674904, 3.885584941145937 54.681478564642596, 5.836488161307221 54.4893313686749, 7.712419264796837 53.92027388975545, 9.44128727134196 52.99617468766803, 10.956652753011412 51.75254637650805, 12.200281064171385 50.23718089483859, 13.124380266258793 48.50831288829346, 13.693437745178223 46.63238178480384, 13.885584941145911 44.681478564642596))"
"POLYGON ((68.36237527090798 25.970070592613357, 68.17022807494028 24.019167372452074, 67.60117059602084 22.14323626896246, 66.67707139393343 20.414368262417334, 65.43344308277345 18.89900278074788, 63.918077601103995 17.655374469587905, 62.189209594558875 16.73127526750049, 60.313278491069255 16.162217788581053, 58.362375270907975 15.970070592613357, 56.411472050746696 16.162217788581053, 54.535540947257076 16.73127526750049, 52.806672940711955 17.655374469587905, 51.2913074590425 18.89900278074788, 50.04767914788252 20.414368262417334, 49.12357994579511 22.14323626896246, 48.55452246687567 24.01916737245208, 48.362375270907975 25.970070592613364, 48.554522466875675 27.920973812774648, 49.123579945795115 29.796904916264268, 50.04767914788253 31.52577292280939, 51.291307459042514 33.04113840447884, 52.80667294071197 34.284766715638824, 54.5355409472571 35.20886591772623, 56.41147205074672 35.77792339664566, 58.362375270908 35.970070592613354, 60.31327849106928 35.777923396645654, 62.1892095945589 35.208865917726214, 63.918077601104024 34.284766715638796, 65.43344308277348 33.04113840447881, 66.67707139393345 31.525772922809352, 67.60117059602086 29.79690491626422, 68.17022807494028 27.9209738127746, 68.36237527090798 25.970070592613357))"
"""
        CsvReader reader = new CsvReader()
        Layer layer = reader.read(csv)
        assertEquals(4, layer.count)
        assertEquals("csv geom: Geometry", layer.schema.toString())
    }

}

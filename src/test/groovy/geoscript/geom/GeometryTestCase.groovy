package geoscript.geom

import org.locationtech.jts.geom.Envelope
import org.locationtech.jts.geom.IntersectionMatrix
import org.junit.Test
import static org.junit.Assert.*
import org.locationtech.jts.geom.Point as JtsPoint
import org.locationtech.jts.geom.Coordinate

/**
 * The Geometry unit test
 */
class GeometryTestCase {

    @Test void constructor() {
        JtsPoint pt = Geometry.factory.createPoint(new Coordinate(111,-47))
        Geometry g = new Geometry(pt)
        assertNotNull(g)
        assertEquals "POINT (111 -47)", g.toString()
    }
	
    @Test void buffer() {
        JtsPoint pt = Geometry.factory.createPoint(new Coordinate(111,-47))
        Geometry g = new Geometry(pt)
        Geometry p = g.buffer(5.0)
        assertNotNull(p)
        assertEquals pt.buffer(5.0).toString(), p.toString()
    }

    @Test void singleSidedBuffer() {
       String wkt = "LINESTRING (0.693359375 46.591796875, 5.703125 51.337890625, 9.306640625 48.0419921875, " +
               "12.03125 53.0078125, 19.2822265625 45.80078125)"
       Geometry g = Geometry.fromWKT(wkt)
       Geometry buffer = g.singleSidedBuffer(-2)
       assertEquals "POLYGON ((0.693359375 46.591796875, 5.703125 51.337890625, 9.306640625 48.0419921875, " +
               "12.03125 53.0078125, 19.2822265625 45.80078125, 17.8723180343991 44.38227571867897, 12.506275490911024 " +
               "49.71579679220651, 11.060054369792876 47.07994216823311, 10.832564011572027 46.749104791663626, " +
               "10.543576944652168 46.470372468507726, 10.20473972588441 46.25497847602753, 9.82970793845354 " +
               "46.11160347389942, 9.43359585401137 46.04602566227302, 9.032367307756616 46.06088791274607, " +
               "8.642192334992412 46.155591257181634, 8.278795497549897 46.32631902690761, 7.9568221633754534 " +
               "46.566190669457164, 5.727082494228705 48.605586708310895, 2.0688486708215663 45.139891507188345, " +
               "0.693359375 46.591796875))", buffer.wkt
    }

    @Test void variableBuffer() {
        String wkt = "LINESTRING (0.693359375 46.591796875, 5.703125 51.337890625, 9.306640625 48.0419921875, " +
                "12.03125 53.0078125, 19.2822265625 45.80078125)"
        Geometry g = Geometry.fromWKT(wkt)
        Geometry buffer = g.variableBuffer([5,10])
        assertEquals "POLYGON ((16.639165921339224 59.73347194942151, 24.934201825506648 54.05033733662435, " +
                "26.353294374365476 52.871849061865476, 27.596922685525453 51.35648358019602, " +
                "28.521021887612868 49.6276155736509, 29.090079366532304 47.75168447016128, 29.2822265625 45.80078125, " +
                "29.090079366532304 43.84987802983872, 28.521021887612868 41.9739469263491, " +
                "27.596922685525453 40.24507891980398, 26.353294374365476 38.729713438134524, " +
                "24.837928892696024 37.486085126974544, 23.1090608861509 36.56198592488713, " +
                "21.233129782661283 35.99292844596769, 19.2822265625 35.80078125, 17.331323342338717 35.99292844596769, " +
                "15.455392238849104 36.56198592488713, 13.72652423230398 37.486085126974544, " +
                "11.06718109869518 40.09876160538111, 10.425735356792835 41.02290888313816, " +
                "9.306640625 40.91268767251427, 7.915782311420394 41.04967525968971, " +
                "6.578373902829325 41.45537366506438, 5.345811254341676 42.11419212643803, " +
                "4.299583605720616 42.97280896803987, 3.4196278586248363 42.400441467116536, " +
                "2.60677653682545 41.97239921244357, 1.6688109850806416 41.687870472983846, " +
                "0.693359375 41.591796875, -0.2820922350806433 41.687870472983846, " +
                "-1.2200577868254516 41.97239921244357, -2.084491790098011 42.43444881348727, " +
                "-2.8421745309327386 43.05626296906726, -3.463988686512727 43.813945709901986, " +
                "-3.926038287556434 44.67837971317455, -4.210567027016152 45.61634526491936, " +
                "-4.306640625 46.591796875, -4.210567027016152 47.56724848508064, " +
                "-3.926038287556434 48.50521403682545, -3.4639886865127263 49.369648040098014, " +
                "-3.344618968209007 49.54047978433004, 0.6581395348429799 55.02192782246175, " +
                "1.2858855670550637 55.75513005794494, 2.232522247546463 56.53201470424467, " +
                "3.312532245478202 57.10929207507738, 4.484411382306353 57.46477772482284, " +
                "5.255328767328899 57.54070645994614, 6.266382951761842 58.77267954823816, " +
                "7.501823024427345 59.786579013214556, 8.91132643563219 60.539974282664176, " +
                "10.4407267597424 61.003912798891, 12.03125 61.160565664896154, 13.6217732402576 61.003912798891, " +
                "15.15117356436781 60.539974282664176, 16.560676975572658 59.786579013214556, " +
                "16.639165921339224 59.73347194942151))", buffer.wkt

        buffer = g.variableBuffer([5,10,20])
        assertEquals "POLYGON ((6.559380488488829 61.37791668387934, 8.338487539477658 62.54098961775868, " +
                "11.628557915198202 64.27837190022574, 15.380420122177455 65.41648685806462, 19.2822265625 65.80078125, " +
                "23.184033002822574 65.4164868580646, 26.93589520980181 64.27837190022572, " +
                "30.393631222892047 62.430173496050905, 33.42436218623095 59.94291687373095, " +
                "35.91161880855091 56.91218591039204, 37.759817212725736 53.4544498973018, " +
                "38.89793217056461 49.70258769032257, 39.2822265625 45.80078125, 38.8979321705646 41.898974809677426, " +
                "37.75981721272573 38.14711260269819, 35.911618808550905 34.68937658960796, " +
                "33.42436218623095 31.658645626269045, 30.393631222892036 29.17138900394909, " +
                "26.9358952098018 27.323190599774268, 23.184033002822567 26.18507564193539, " +
                "19.2822265625 25.80078125, 15.380420122177426 26.185075641935395, 11.628557915198193 27.32319059977427, " +
                "8.170821902107956 29.171389003949095, 5.140090938769045 31.65864562626905, 2.6528343164490913 34.68937658960796, " +
                "0.8046359122742643 38.14711260269821, -0.2690516304601481 41.686586085534145, " +
                "-0.2820922350806433 41.687870472983846, -1.2200577868254516 41.97239921244357, " +
                "-2.084491790098011 42.43444881348727, -2.8421745309327386 43.05626296906726, " +
                "-3.463988686512727 43.813945709901986, -3.926038287556434 44.67837971317455, " +
                "-4.210567027016152 45.61634526491936, -4.306640625 46.591796875, -4.210567027016152 47.56724848508064, " +
                "-3.926038287556434 48.50521403682545, -3.6412863060734937 49.08395393657355, " +
                "-0.3458813488962189 54.81570057697087, -0.0984621812696602 55.214387245550824, " +
                "0.7692806636323439 56.271734961367656, 1.8266283794491764 57.13947780626966, " +
                "3.0329476864294236 57.78426890936304, 3.2559574100103457 57.851918169423506, " +
                "3.628037134368597 58.42869928491691, 3.7165538769745474 58.56351483019602, " +
                "4.960182188134525 60.078880311865476, 6.47554766980398 61.322508623025456, " +
                "6.559380488488829 61.37791668387934))", buffer.wkt

        buffer = g.variableBuffer([1,3,5,10,20])
        assertEquals "POLYGON ((6.559380488488829 61.37791668387934, 8.338487539477658 62.54098961775868, " +
                "11.628557915198202 64.27837190022574, 15.380420122177455 65.41648685806462, 19.2822265625 65.80078125, " +
                "23.184033002822574 65.4164868580646, 26.93589520980181 64.27837190022572, " +
                "30.393631222892047 62.430173496050905, 33.42436218623095 59.94291687373095, " +
                "35.91161880855091 56.91218591039204, 37.759817212725736 53.4544498973018, " +
                "38.89793217056461 49.70258769032257, 39.2822265625 45.80078125, 38.8979321705646 41.898974809677426, " +
                "37.75981721272573 38.14711260269819, 35.911618808550905 34.68937658960796, " +
                "33.42436218623095 31.658645626269045, 30.393631222892036 29.17138900394909, " +
                "26.9358952098018 27.323190599774268, 23.184033002822567 26.18507564193539, 19.2822265625 25.80078125, " +
                "15.380420122177426 26.185075641935395, 11.628557915198193 27.32319059977427, " +
                "8.170821902107956 29.171389003949095, 5.140090938769045 31.65864562626905, " +
                "2.6528343164490913 34.68937658960796, 0.8046359122742643 38.14711260269821, " +
                "-0.3334790455646086 41.89897480967743, -0.7177734375 45.80078125, " +
                "-0.3334790455646086 49.702587690322574, 0.8046359122742643 53.4544498973018, " +
                "2.475800831237194 56.64255481983383, 3.628037134368597 58.42869928491691, " +
                "3.7165538769745474 58.56351483019602, 4.960182188134525 60.078880311865476, " +
                "6.47554766980398 61.322508623025456, 6.559380488488829 61.37791668387934))", buffer.wkt
    }

    @Test void equals() {
        Geometry g1 = new Point(111, -47)
        Geometry g2 = new Point(111, -47)
        Geometry g3 = new Point(123, -32)
        assertTrue g1.equals(g2)
        assertFalse g1.equals(g3)
        assertFalse g2.equals(g3)
    }

    @Test void testHashCode() {
        Geometry g1 = new Point(111, -47)
        Geometry g2 = new Point(111, -47)
        Geometry g3 = new Point(123, -32)
        assertTrue g1.hashCode().equals(g2.hashCode())
        assertFalse g1.hashCode().equals(g3.hashCode())
        assertFalse g2.hashCode().equals(g3.hashCode())
    }

    @Test void equalsNorm() {
        Geometry g1 = new Point(111, -47)
        Geometry g2 = new Point(111, -47)
        Geometry g3 = new Point(123, -32)
        assertTrue g1.equalsNorm(g2)
        assertFalse g1.equalsNorm(g3)
        assertFalse g2.equalsNorm(g3)
    }

    @Test void equalsTopo() {
        Geometry g1 = new Point(111, -47)
        Geometry g2 = new Point(111, -47)
        Geometry g3 = new Point(123, -32)
        assertTrue g1.equalsTopo(g2)
        assertFalse g1.equalsTopo(g3)
        assertFalse g2.equalsTopo(g3)
    }

    @Test void getWkt() {
        Geometry g = new Geometry(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.wkt
    }

    @Test void getWkb() {
        Geometry g = new Point(111,-47)
        assertEquals "0000000001405BC00000000000C047800000000000", g.wkb
        assertArrayEquals([0, 0, 0, 0, 1, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0] as byte[], g.wkbBytes)
    }

    @Test void fromWkb() {
        Geometry expected = new Point(111, -47)
        Geometry actual = Geometry.fromWKB("0000000001405BC00000000000C047800000000000")
        assertEquals expected.wkt, actual.wkt
        actual = Geometry.fromWKB([0, 0, 0, 0, 1, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0] as byte[])
        assertEquals expected.wkt, actual.wkt
    }

    @Test void string() {
        Geometry g = new Geometry(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.toString()
    }
	
    @Test void wrap() {
        Geometry g = Geometry.wrap(Geometry.factory.createPoint(new Coordinate(111,-47)))
        assertEquals "POINT (111 -47)", g.toString()
        g = Geometry.wrap(null)
        assertNull g
    }
	
    @Test void fromWkt() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getKml() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<Point><coordinates>111.0,-47.0</coordinates></Point>", g.kml
    }

    @Test void fromKml() {
        Geometry g = Geometry.fromKml("<Point><coordinates>111.0,-47.0</coordinates></Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getGeoJSON() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals """{"type":"Point","coordinates":[111,-47]}""", g.geoJSON
    }

    @Test void getGeoJSONWithDecimals() {
        Geometry g = Geometry.fromWKT("POINT (111.123456 -47.123456)")
        assertEquals """{"type":"Point","coordinates":[111.123456,-47.123456]}""", g.getGeoJSON(decimals: 6)
    }

    @Test void fromGeoJSON() {
        Geometry g = Geometry.fromGeoJSON("""{ "type": "Point", "coordinates": [111.0, -47.0] }""")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getGml2() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>", g.gml2
    }

    @Test void fromGml2() {
        Geometry g = Geometry.fromGML2("<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getGml3() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>", g.gml3
    }

    @Test void fromGml3() {
        Geometry g = Geometry.fromGML3("<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void fromGpx() {
        Geometry g = Geometry.fromGpx("<wpt lat='2.0' lon='1.0'/>")
        assertEquals "POINT (1 2)", g.toString()
    }

    @Test void getGpx() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<wpt lat='-47.0' lon='111.0'/>", g.gpx
    }

    @Test void getGeobuf() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "10021806320c08001a0880e7ed69ffa6e92c", g.geobuf
        assertArrayEquals([16, 2, 24, 6, 50, 12, 8, 0, 26, 8, -128, -25, -19, 105, -1, -90, -23, 44] as byte[], g.geobufBytes)
    }

    @Test void fromGeobuf() {
        Geometry g = Geometry.fromGeobuf("10021806320c08001a0880e7ed69ffa6e92c")
        assertEquals "POINT (111 -47)", g.wkt
        g = Geometry.fromGeobuf([16, 2, 24, 6, 50, 12, 8, 0, 26, 8, -128, -25, -19, 105, -1, -90, -23, 44] as byte[])
        assertEquals "POINT (111 -47)", g.wkt
    }

    @Test void getCoordinates() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        def coordinates = g.coordinates
        assertEquals 1, coordinates.length
        assertEquals(111, coordinates[0].x, 0.0)
        assertEquals(-47, coordinates[0].y, 0.0)
    }

    @Test void getMinimumBoundingCircle() {
        Geometry g = new GeometryCollection([
            new Point(-122.394276, 46.970863),
            new Point(-121.927567, 46.929644),
            new Point(-122.533838, 47.284403),
            new Point(-122.079196, 47.187141)
        ])
        Geometry circle = g.minimumBoundingCircle
        // println("Minimum Bounding Circle: ${circle}")
        assertNotNull(circle)
        assertTrue(circle instanceof Polygon)
    }

    @Test void getDelaunayTriangleDiagram() {
        Geometry g = new GeometryCollection([
            new Point(-122.394276, 46.970863),
            new Point(-121.927567, 46.929644),
            new Point(-122.533838, 47.284403),
            new Point(-122.079196, 47.187141)
        ])
        Geometry triangles = g.delaunayTriangleDiagram
        // println("Delaunay Triangles: ${triangles}")
        assertNotNull(triangles)
        assertTrue(triangles instanceof GeometryCollection)

        triangles = g.getDelaunayTriangleDiagram(true)
        assertNotNull(triangles)
        assertTrue(triangles instanceof GeometryCollection)
    }

    @Test void getVoronoiDiagram() {
        Geometry g = new GeometryCollection([
            new Point(-122.394276, 46.970863),
            new Point(-121.927567, 46.929644),
            new Point(-122.533838, 47.284403),
            new Point(-122.079196, 47.187141)
        ])
        Geometry diagram = g.voronoiDiagram
        // println("Voronoi Diagram: ${diagram}")
        assertNotNull(diagram)
        assertTrue(diagram instanceof GeometryCollection)
    }

    @Test void getBounds() {
        Geometry g = new GeometryCollection([
            new Point(-122.394276, 46.970863),
            new Point(-121.927567, 46.929644),
            new Point(-122.533838, 47.284403),
            new Point(-122.079196, 47.187141)
        ])
        Bounds b = g.bounds
        assertEquals "(-122.533838,46.929644,-121.927567,47.284403)", b.toString()
    }

    @Test void simplify() {
        String wkt = """POLYGON ((13.63525390625 51.337890625, 13.59130859375 51.337890625, 13.41552734375 51.337890625, 12.44873046875 51.8212890625, 11.04248046875 52.5244140625, 9.98779296875 52.919921875, 8.53759765625 53.359375, 7.52685546875 53.53515625, 6.47216796875 53.6669921875, 5.63720703125 53.6669921875, 5.02197265625 53.6669921875, 4.67041015625 53.6669921875, 4.40673828125 53.4912109375, 4.14306640625 53.2275390625, 3.96728515625 52.919921875, 3.79150390625 52.568359375, 3.65966796875 52.1728515625, 3.52783203125 51.7333984375, 3.35205078125 51.25, 3.13232421875 50.72265625, 2.82470703125 49.931640625, 2.56103515625 49.2724609375, 2.29736328125 48.61328125, 2.07763671875 47.998046875, 1.90185546875 47.4267578125, 1.81396484375 46.8994140625, 1.77001953125 46.4599609375, 1.72607421875 46.1962890625, 1.72607421875 45.8447265625, 1.72607421875 45.6689453125, 1.72607421875 45.44921875, 1.72607421875 45.2734375, 1.72607421875 45.009765625, 1.85791015625 44.5263671875, 2.03369140625 44.21875, 2.25341796875 43.779296875, 2.51708984375 43.4716796875, 2.73681640625 43.2080078125, 3.00048828125 43.0322265625, 3.30810546875 42.9443359375, 3.96728515625 42.900390625, 4.05517578125 42.900390625, 4.31884765625 42.900390625, 4.75830078125 42.7685546875, 5.59326171875 42.5048828125, 6.47216796875 42.2412109375, 7.30712890625 41.93359375, 7.87841796875 41.8017578125, 8.58154296875 41.6259765625, 9.37255859375 41.494140625, 10.03173828125 41.40625, 10.64697265625 41.3623046875, 10.91064453125 41.3623046875, 11.13037109375 41.3623046875, 11.52587890625 41.3623046875, 12.18505859375 41.40625, 13.28369140625 41.7138671875, 14.33837890625 41.9775390625, 14.86572265625 42.197265625, 15.48095703125 42.548828125, 15.87646484375 42.7685546875, 16.18408203125 42.98828125, 16.40380859375 43.2080078125, 16.57958984375 43.427734375, 16.75537109375 43.69140625, 16.88720703125 44.04296875, 17.06298828125 44.482421875, 17.19482421875 45.009765625, 17.45849609375 46.064453125, 17.54638671875 46.328125, 17.63427734375 46.8115234375, 17.67822265625 47.0751953125, 17.67822265625 47.2509765625, 17.67822265625 47.3388671875, 17.63427734375 47.3388671875, 17.54638671875 47.3388671875, 17.28271484375 47.3388671875, 17.01904296875 47.3388671875, 16.62353515625 47.3388671875, 16.27197265625 47.3388671875, 15.87646484375 47.3388671875, 15.52490234375 47.3388671875, 15.21728515625 47.3388671875, 14.90966796875 47.294921875, 14.64599609375 47.2509765625, 14.38232421875 47.1630859375, 14.16259765625 47.1630859375, 13.89892578125 47.0751953125, 13.67919921875 47.03125, 13.45947265625 46.9873046875, 13.19580078125 46.9873046875, 12.97607421875 46.9873046875, 12.80029296875 46.9873046875, 12.58056640625 47.03125, 12.40478515625 47.1630859375, 12.22900390625 47.4267578125, 12.05322265625 47.6904296875, 12.00927734375 47.998046875, 11.96533203125 48.4375, 11.96533203125 48.701171875, 11.96533203125 48.9208984375, 12.00927734375 49.0966796875, 12.18505859375 49.2724609375, 12.36083984375 49.404296875, 12.58056640625 49.580078125, 12.75634765625 49.6240234375, 13.02001953125 49.7119140625, 13.28369140625 49.7119140625, 13.45947265625 49.7119140625, 13.76708984375 49.7119140625, 14.03076171875 49.66796875, 14.25048828125 49.580078125, 14.38232421875 49.4921875, 14.55810546875 49.4482421875, 14.68994140625 49.3603515625, 14.86572265625 49.3603515625, 14.99755859375 49.3603515625, 15.17333984375 49.3603515625, 15.39306640625 49.3603515625, 15.65673828125 49.3603515625, 15.83251953125 49.404296875, 16.00830078125 49.404296875, 16.31591796875 49.4921875, 16.44775390625 49.4921875, 16.57958984375 49.4921875, 16.71142578125 49.4921875, 16.79931640625 49.4921875, 16.88720703125 49.5361328125, 16.97509765625 49.5361328125, 17.01904296875 49.580078125, 17.10693359375 49.6240234375, 17.15087890625 49.66796875, 17.15087890625 49.7119140625, 17.19482421875 49.84375, 17.23876953125 50.01953125, 17.23876953125 50.2392578125, 17.23876953125 50.37109375, 17.28271484375 50.546875, 17.28271484375 50.634765625, 17.28271484375 50.72265625, 17.28271484375 50.7666015625, 17.28271484375 50.810546875, 17.28271484375 50.8544921875, 17.28271484375 50.8984375, 13.63525390625 51.337890625))"""
        Geometry g = Geometry.fromWKT(wkt)
        Geometry simplified = g.simplify(2)
        assertEquals "POLYGON ((13.63525390625 51.337890625, 17.28271484375 50.8984375, 11.96533203125 48.9208984375, 17.67822265625 47.3388671875, 15.87646484375 42.7685546875, 2.25341796875 43.779296875, 4.67041015625 53.6669921875, 13.63525390625 51.337890625))", simplified.wkt
    }

    @Test void simplifyPreservingTopology() {
        String wkt = """POLYGON ((13.63525390625 51.337890625, 13.59130859375 51.337890625, 13.41552734375 51.337890625, 12.44873046875 51.8212890625, 11.04248046875 52.5244140625, 9.98779296875 52.919921875, 8.53759765625 53.359375, 7.52685546875 53.53515625, 6.47216796875 53.6669921875, 5.63720703125 53.6669921875, 5.02197265625 53.6669921875, 4.67041015625 53.6669921875, 4.40673828125 53.4912109375, 4.14306640625 53.2275390625, 3.96728515625 52.919921875, 3.79150390625 52.568359375, 3.65966796875 52.1728515625, 3.52783203125 51.7333984375, 3.35205078125 51.25, 3.13232421875 50.72265625, 2.82470703125 49.931640625, 2.56103515625 49.2724609375, 2.29736328125 48.61328125, 2.07763671875 47.998046875, 1.90185546875 47.4267578125, 1.81396484375 46.8994140625, 1.77001953125 46.4599609375, 1.72607421875 46.1962890625, 1.72607421875 45.8447265625, 1.72607421875 45.6689453125, 1.72607421875 45.44921875, 1.72607421875 45.2734375, 1.72607421875 45.009765625, 1.85791015625 44.5263671875, 2.03369140625 44.21875, 2.25341796875 43.779296875, 2.51708984375 43.4716796875, 2.73681640625 43.2080078125, 3.00048828125 43.0322265625, 3.30810546875 42.9443359375, 3.96728515625 42.900390625, 4.05517578125 42.900390625, 4.31884765625 42.900390625, 4.75830078125 42.7685546875, 5.59326171875 42.5048828125, 6.47216796875 42.2412109375, 7.30712890625 41.93359375, 7.87841796875 41.8017578125, 8.58154296875 41.6259765625, 9.37255859375 41.494140625, 10.03173828125 41.40625, 10.64697265625 41.3623046875, 10.91064453125 41.3623046875, 11.13037109375 41.3623046875, 11.52587890625 41.3623046875, 12.18505859375 41.40625, 13.28369140625 41.7138671875, 14.33837890625 41.9775390625, 14.86572265625 42.197265625, 15.48095703125 42.548828125, 15.87646484375 42.7685546875, 16.18408203125 42.98828125, 16.40380859375 43.2080078125, 16.57958984375 43.427734375, 16.75537109375 43.69140625, 16.88720703125 44.04296875, 17.06298828125 44.482421875, 17.19482421875 45.009765625, 17.45849609375 46.064453125, 17.54638671875 46.328125, 17.63427734375 46.8115234375, 17.67822265625 47.0751953125, 17.67822265625 47.2509765625, 17.67822265625 47.3388671875, 17.63427734375 47.3388671875, 17.54638671875 47.3388671875, 17.28271484375 47.3388671875, 17.01904296875 47.3388671875, 16.62353515625 47.3388671875, 16.27197265625 47.3388671875, 15.87646484375 47.3388671875, 15.52490234375 47.3388671875, 15.21728515625 47.3388671875, 14.90966796875 47.294921875, 14.64599609375 47.2509765625, 14.38232421875 47.1630859375, 14.16259765625 47.1630859375, 13.89892578125 47.0751953125, 13.67919921875 47.03125, 13.45947265625 46.9873046875, 13.19580078125 46.9873046875, 12.97607421875 46.9873046875, 12.80029296875 46.9873046875, 12.58056640625 47.03125, 12.40478515625 47.1630859375, 12.22900390625 47.4267578125, 12.05322265625 47.6904296875, 12.00927734375 47.998046875, 11.96533203125 48.4375, 11.96533203125 48.701171875, 11.96533203125 48.9208984375, 12.00927734375 49.0966796875, 12.18505859375 49.2724609375, 12.36083984375 49.404296875, 12.58056640625 49.580078125, 12.75634765625 49.6240234375, 13.02001953125 49.7119140625, 13.28369140625 49.7119140625, 13.45947265625 49.7119140625, 13.76708984375 49.7119140625, 14.03076171875 49.66796875, 14.25048828125 49.580078125, 14.38232421875 49.4921875, 14.55810546875 49.4482421875, 14.68994140625 49.3603515625, 14.86572265625 49.3603515625, 14.99755859375 49.3603515625, 15.17333984375 49.3603515625, 15.39306640625 49.3603515625, 15.65673828125 49.3603515625, 15.83251953125 49.404296875, 16.00830078125 49.404296875, 16.31591796875 49.4921875, 16.44775390625 49.4921875, 16.57958984375 49.4921875, 16.71142578125 49.4921875, 16.79931640625 49.4921875, 16.88720703125 49.5361328125, 16.97509765625 49.5361328125, 17.01904296875 49.580078125, 17.10693359375 49.6240234375, 17.15087890625 49.66796875, 17.15087890625 49.7119140625, 17.19482421875 49.84375, 17.23876953125 50.01953125, 17.23876953125 50.2392578125, 17.23876953125 50.37109375, 17.28271484375 50.546875, 17.28271484375 50.634765625, 17.28271484375 50.72265625, 17.28271484375 50.7666015625, 17.28271484375 50.810546875, 17.28271484375 50.8544921875, 17.28271484375 50.8984375, 13.63525390625 51.337890625))"""
        Geometry g = Geometry.fromWKT(wkt)
        Geometry simplified = g.simplifyPreservingTopology(2)
        assertEquals "POLYGON ((13.63525390625 51.337890625, 4.67041015625 53.6669921875, 2.25341796875 43.779296875, 15.87646484375 42.7685546875, 17.67822265625 47.3388671875, 11.96533203125 48.9208984375, 17.28271484375 50.8984375, 13.63525390625 51.337890625))", simplified.wkt
    }

    @Test void densify() {
        Geometry g = new LineString([[0,0],[0,10]])
        Geometry densifiedGeom = g.densify(1)
        assertEquals("LINESTRING (0 0, 0 0.9090909090909092, 0 1.8181818181818183, 0 2.727272727272727, 0 3.6363636363636367, 0 4.545454545454545, 0 5.454545454545454, 0 6.363636363636363, 0 7.272727272727273, 0 8.181818181818182, 0 9.09090909090909, 0 10)", densifiedGeom.wkt)
    }

    @Test void minimumRectangle() {
        Geometry g = new MultiPoint([
            new Point(2,1),
            new Point(1,2),
            new Point(4,3),
            new Point(3,4)
        ])
        Geometry minRect = g.minimumRectangle
        assertEquals("POLYGON ((1 2, 2 1, 4 3, 3 4, 1 2))", minRect.wkt)
    }

    @Test void minimumDiameter() {
        Geometry g = new MultiPoint([
            new Point(2,1),
            new Point(1,2),
            new Point(4,3),
            new Point(3,4)
        ])
        Geometry minDiameter = g.minimumDiameter
        assertEquals("LINESTRING (1 2, 2 1)", minDiameter.wkt)
    }

    @Test void getMinimumClearance() {
        String wkt = "POLYGON ((12.998046875 53.4912109375, 16.337890625 49.9755859375, 11.591796875 44.5263671875, 19.5458984375 45.0537109375, 17.6123046875 53.6669921875, 12.998046875 53.4912109375))"
        Geometry g = Geometry.fromWKT(wkt)
        Geometry minClearance = g.minimumClearance
        assertEquals "LINESTRING (16.337890625 49.9755859375, 18.340085760928826 50.42505831495341)", minClearance.wkt
    }

    @Test void isValid() {
        Geometry g1 = new Polygon([0,0],[10,10],[0,10],[10,0],[0,0])
        assertFalse(g1.isValid())
        Geometry g2 = new Polygon([0,0],[0,10],[10,10],[10,0],[0,0])
        assertTrue(g2.isValid())
    }

    @Test void getValidReason() {
        // If geometry is invalid return the reason
        Geometry g = new Polygon([0,0],[10,10],[0,10],[10,0],[0,0])
        assertFalse(g.isValid())
        assertEquals("Self-intersection",g.validReason)
        // If geometry is valid just return empty string
        Geometry g2 = new Polygon([0,0],[0,10],[10,10],[10,0],[0,0])
        assertEquals("", g2.validReason)
    }

    @Test void translate() {
        Geometry g = new Point(20,20)
        assertEquals("POINT (25 20)", g.translate(5,0).wkt)
        assertEquals("POINT (25 25)", g.translate(5,5).wkt)
        assertEquals("POINT (15 20)", g.translate(-5,0).wkt)
        assertEquals("POINT (15 15)", g.translate(-5,-5).wkt)
    }

    @Test void scale() {
        Geometry g = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        assertEquals("POLYGON ((50 50, 50 100, 100 100, 100 50, 50 50))", g.scale(5,5).wkt)
        assertEquals("POLYGON ((-10 -10, -10 40, 40 40, 40 -10, -10 -10))", g.scale(5,5,g.centroid.x, g.centroid.y).wkt)
    }

    @Test void rotate() {
        Geometry g = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        // theta
        assertTrue(new Bounds(-8, 14, 8, 29).geometry.contains(g.rotate(45 * Math.PI / 180)))
        // sin, cos
        assertTrue(new Bounds(10, 18, 29, 37).geometry.contains(g.rotate(15 * Math.PI / 180, 90 * Math.PI / 180)))
        // theta, x, y
        assertTrue(new Bounds(7, 7, 23, 23).geometry.contains(g.rotate(45 * Math.PI / 180, g.centroid.x, g.centroid.y)))
        // sin, cos, x, y
        assertTrue(new Bounds(5, 5, 25, 25).geometry.contains(g.rotate(15 * Math.PI / 180, 90 * Math.PI / 180, g.centroid.x, g.centroid.y)))
    }

    @Test void shear() {
        Geometry g = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        assertTrue(new Bounds(20.0,30.0,40.0,60.0).geometry.contains(g.shear(1,2)))
    }

    @Test void reflect() {
        Geometry g = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        assertTrue(new Bounds(-10,13,5,29).geometry.contains(g.reflect(10,30)))
        assertTrue(new Bounds(-10,30,1,41).geometry.contains(g.reflect(10,30,20,40)))
    }

    @Test void getAt() {
        // LineString
        def line = Geometry.fromWKT("LINESTRING (1 2, 2 1)")
        assertEquals("POINT (1 2)", line[0].wkt)
        assertEquals("POINT (2 1)", line[1].wkt)
        assertNull(line[2])

        // Point
        def point = new Point(111,-47)
        assertEquals(111, point[0], 0.0)
        assertEquals(-47, point[1], 0.0)
        assertNull(point[2])
        def (x,y) = point
        assertEquals(111, x, 0.0)
        assertEquals(-47, y, 0.0)

        // Polygon
        def poly = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        assertEquals("LINEARRING (1 1, 10 1, 10 10, 1 10, 1 1)", poly[0].wkt)
        assertEquals("LINEARRING (2 2, 4 2, 4 4, 2 4, 2 2)", poly[1].wkt)
        assertEquals("LINEARRING (5 5, 6 5, 6 6, 5 6, 5 5)", poly[2].wkt)
        assertNull(poly[3])

        // MultiPoint
        def mp = new MultiPoint([1,2],[3,4])
        assertEquals("POINT (1 2)", mp[0].wkt)
        assertEquals("POINT (3 4)", mp[1].wkt)
        assertNull(mp[2])
        def (p1, p2) = mp
        assertEquals("POINT (1 2)", p1.wkt)
        assertEquals("POINT (3 4)", p2.wkt)
    }

    @Test void norm() {
        Geometry g1 = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        Geometry g2 = g1.norm
        assertNotNull g2
    }

    @Test void createRandomPoints() {
        int number = 100
        String wkt = "POLYGON ((8.603515625 52.919921875, 5.52734375 45.2734375, 20.9521484375 45.9326171875, 19.150390625 53.798828125, 8.603515625 52.919921875))"
        Geometry g = Geometry.fromWKT(wkt)
        Geometry pts = Geometry.createRandomPoints(g, number)
        assertEquals number, pts.numPoints
        pts.coordinates.each {coord ->
            assertTrue g.contains(new Point(coord.x, coord.y))
        }
    }

    @Test void createRandomPointsInGrid() {
        int number = 100
        Bounds b = new Bounds(4, 45, 19, 53)
        Geometry g = b.geometry
        Geometry pts = Geometry.createRandomPointsInGrid(b, number, true, 0.75)
        assertEquals 100, pts.numPoints
        pts.coordinates.each {coord ->
            assertTrue g.contains(new Point(coord.x, coord.y))
        }
    }

    @Test void getGeometryType() {
        Point p = new Point(145, -47)
        assertEquals "Point", p.geometryType

        LineString lineString = new LineString([[0,0],[1,1],[2,2]])
        assertEquals "LineString", lineString.geometryType
    }

    @Test void getOctagonalEnvelope() {
        Geometry g = Geometry.fromWKT("POLYGON ((1254084.2451712033 681826.3362917757, 1255211.7905953382 677551.0598919304, 1247506.896863749 673651.6319667968, 1247459.9158044101 669047.4881515788, 1252392.9270350009 668859.563914223, 1260802.5366566745 668953.5260329009, 1260802.5366566745 678725.5863754044, 1259862.9154698953 685161.9915048417, 1255916.5064854226 685068.0293861638, 1254084.2451712033 681826.3362917757))")
        Geometry octalEnvelope = g.octagonalEnvelope
        assertNotNull octalEnvelope
        assertTrue octalEnvelope.isValid()
        assertFalse octalEnvelope.isEmpty()
    }
    
    /*@Test void createFromText() {
        Geometry g = Geometry.createFromText("B")
        assertEquals "Polygon", g.geometryType
        
        g = Geometry.createFromText("BAJ")
        assertEquals "MultiPolygon", g.geometryType
    }*/
    
    @Test void createSierpinskiCarpet() {
        Geometry g = Geometry.createSierpinskiCarpet(new Bounds(0,0,10,10), 200)
        assertNotNull g
        assertEquals "Polygon", g.geometryType
    }
    
    @Test void createKochSnowflake() {
        Geometry g = Geometry.createKochSnowflake(new Bounds(0,0,10,10), 200)
        assertNotNull g
        assertEquals "Polygon", g.geometryType
    }
    
    @Test void snap() {
        Geometry g1 = Geometry.fromWKT("POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))")
        Geometry g2 = Geometry.fromWKT("POLYGON ((11 0, 11 10, 20 10, 20 0, 11 0))")
        Geometry snapped = g1.snap(g2, 1.2)
        assertEquals "GEOMETRYCOLLECTION (" +
                "POLYGON ((0 0, 0 10, 11 10, 11 0, 0 0)), " +
                "POLYGON ((11 0, 11 10, 20 10, 20 0, 11 0)))", snapped.wkt
    }

    @Test void reducePrecision() {
        Geometry g1 = new Point(5.19775390625, 51.07421875)
        
        // floating
        Geometry g2 = g1.reducePrecision()
        assertEquals "POINT (5.19775390625 51.07421875)", g2.wkt
        
        // fixed
        Geometry g3 = g1.reducePrecision("fixed", scale: 100)
        assertEquals "POINT (5.2 51.07)", g3.wkt

        // floating single
        Geometry g4 = g1.reducePrecision("floating_single", pointwise: true, removecollapsed: true)
        assertEquals "POINT (5.19775390625 51.07421875)", g4.wkt
    }

    @Test void asType() {
        Geometry g = Geometry.fromWKT("POLYGON ((0 0, 0 10, 10 10, 10 0, 0 0))")
        Bounds b = g as Bounds
        assertEquals "(0.0,0.0,10.0,10.0)", b.toString()
        Point p = g as Point
        assertEquals "POINT (5 5)", p.wkt
    }

    @Test void fromString() {
        // WKT
        Geometry g = Geometry.fromString("POINT (1 1)")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GeoJSON
        g = Geometry.fromString("""{ "type": "Point", "coordinates": [1.0, 1.0] }""")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GeoRSS
        g = Geometry.fromString("<georss:point>1 1</georss:point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GML 2
        g = Geometry.fromString("<gml:Point><gml:coordinates>1,1</gml:coordinates></gml:Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GML 3
        g = Geometry.fromString("<gml:Point><gml:pos>1 1</gml:pos></gml:Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // KML
        g = Geometry.fromString("<Point><coordinates>1,1</coordinates></Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // WKB
        g = Geometry.fromString("00000000013FF00000000000003FF0000000000000")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GPX
        g = Geometry.fromString("<wpt lat='2.0' lon='1.0'/>")
        assertNotNull g
        assertEquals "POINT (1 2)", g.wkt
        // Bounds
        g = Geometry.fromString("1,1,10,10")
        assertNotNull g
        assertEquals "POLYGON ((1 1, 1 10, 10 10, 10 1, 1 1))", g.wkt
        // Bounds with projection
        g = Geometry.fromString("1,1,10,10,EPSG:4326")
        assertEquals "POLYGON ((1 1, 1 10, 10 10, 10 1, 1 1))", g.wkt
        // Point
        g = Geometry.fromString("1,1")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // Bounds
        g = Geometry.fromString("1 1 10 10")
        assertNotNull g
        assertEquals "POLYGON ((1 1, 1 10, 10 10, 10 1, 1 1))", g.wkt
        // Point
        g = Geometry.fromString("1 1")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // Null
        g = Geometry.fromString(null)
        assertNull g
        // Empty String
        g = Geometry.fromString("    ")
        assertNull g
        // Bad String
        g = Geometry.fromString("asfasd")
        assertNull g
        // Geobuf
        g = Geometry.fromString("10021806320c08001a0880e7ed69ffa6e92c")
        assertEquals "POINT (111 -47)", g.wkt
    }

    @Test void cascadedUnion() {
        def pts = Geometry.createRandomPoints(new Bounds(0,0,10,10).geometry, 100)
        def polys = pts.collect{it.buffer(0.7)}
        def union = Geometry.cascadedUnion(polys)
        assertTrue union instanceof Geometry
        assertTrue union.numGeometries < pts.numGeometries
    }

    @Test void smooth() {
        def geom = Geometry.fromWKT("POLYGON((10 0, 10 20, 0 20, 0 30, 30 30, 30 20, 20 20, 20 0, 10 0))")
        def smoothed = geom.smooth(0.75)
        assertTrue smoothed instanceof Geometry
        assertEquals "Polygon", smoothed.geometryType
    }

    @Test void getPoints() {
        def line = new LineString([new Point(0,0), new Point(5,5)])
        assertEquals 2, line.numPoints
        assertEquals "POINT (0 0)", line.points[0].wkt
        assertEquals "POINT (5 5)", line.points[1].wkt
    }

    @Test void getNearestPoints() {
        def geom1 = Geometry.fromWKT("POLYGON ((90 90, 90 110, 110 110, 110 90, 90 90))")
        def geom2 = Geometry.fromWKT("POLYGON ((173.96210441769105 -94.53669248798772, 193.14058991095382 -88.86344877872318, 198.81383362021836 -108.04193427198595, 179.6353481269556 -113.71517798125049, 173.96210441769105 -94.53669248798772))")
        List points = geom1.getNearestPoints(geom2)
        assertEquals 2, points.size()
        assertEquals "POINT (110 90)", points[0].wkt
        assertEquals "POINT (173.96210441769105 -94.53669248798772)", points[1].wkt
    }

    @Test void isCurved() {
        assertFalse Geometry.fromWKT("POINT (1 1)").isCurved()
        assertFalse Geometry.fromWKT("LINESTRING (1 1, 10 10)").isCurved()
        assertTrue Geometry.fromWKT("CIRCULARSTRING (6.12 10.0, 7.07 7.07, 10.0 0.0)").isCurved()
    }

    @Test void getDimension() {
        assertEquals 0, Geometry.fromWKT("POINT (1 1)").dimension
        assertEquals 1, Geometry.fromWKT("LINESTRING (1 1, 10 10)").dimension
        assertEquals 2, Geometry.fromWKT("POLYGON ((90 90, 90 110, 110 110, 110 90, 90 90))").dimension
    }

    @Test void offset() {
        Geometry g = Geometry.fromWKT("LINESTRING (0 5, 5 5)").offset(2)
        assertEquals "LINESTRING (0 7, 5 7)", g.wkt
        g = Geometry.fromWKT("LINESTRING (0 5, 5 5)").offset(-2)
        assertEquals "LINESTRING (0 3, 5 3)", g.wkt
    }

    @Test void convexHull() {
        Geometry geometry = new MultiPoint(
                new Point(-119.882, 47.279),
                new Point(-100.195, 46.316),
                new Point(-111.796, 42.553),
                new Point(-90.7031, 34.016)
        )
        Geometry convexHull = geometry.convexHull
        assertEquals("POLYGON ((-90.7031 34.016, -111.796 42.553, -119.882 47.279, " +
                "-100.195 46.316, -90.7031 34.016))", convexHull.wkt)
    }

    @Test void covers() {

        Polygon polygon1 = new Polygon([[
                [-120.739, 48.151],
                [-121.003, 47.070],
                [-119.465, 47.137],
                [-119.553, 46.581],
                [-121.267, 46.513],
                [-121.168, 45.706],
                [-118.476, 45.951],
                [-118.762, 48.195],
                [-120.739, 48.151]
        ]])

        Polygon polygon2 = new Polygon([[
                [-120.212, 47.591],
                [-119.663, 47.591],
                [-119.663, 47.872],
                [-120.212, 47.872],
                [-120.212, 47.591]
        ]])

        Polygon polygon3 = new Polygon([[
                [-120.563, 46.739],
                [-119.948, 46.739],
                [-119.948, 46.965],
                [-120.563, 46.965],
                [-120.563, 46.739]
        ]])

        assertTrue(polygon1.covers(polygon2))
        assertFalse(polygon1.covers(polygon3))
    }

    @Test void coveredBy() {

        Polygon polygon1 = new Polygon([[
                [-120.739, 48.151],
                [-121.003, 47.070],
                [-119.465, 47.137],
                [-119.553, 46.581],
                [-121.267, 46.513],
                [-121.168, 45.706],
                [-118.476, 45.951],
                [-118.762, 48.195],
                [-120.739, 48.151]
        ]])

        Polygon polygon2 = new Polygon([[
                [-120.212, 47.591],
                [-119.663, 47.591],
                [-119.663, 47.872],
                [-120.212, 47.872],
                [-120.212, 47.591]
        ]])

        Polygon polygon3 = new Polygon([[
                [-120.563, 46.739],
                [-119.948, 46.739],
                [-119.948, 46.965],
                [-120.563, 46.965],
                [-120.563, 46.739]
        ]])

        assertTrue(polygon2.coveredBy(polygon1))
        assertFalse(polygon3.coveredBy(polygon1))
    }

    @Test void crosses() {
        LineString line1 = new LineString([[-122.486, 47.256], [-121.695, 46.822]])
        LineString line2 = new LineString([[-122.387, 47.613], [-121.750, 47.353]])
        LineString line3 = new LineString([[-122.255, 47.368], [-121.882, 47.746]])

        assertFalse(line1.crosses(line2))
        assertFalse(line1.crosses(line3))
        assertTrue(line2.crosses(line3))
    }

    @Test void difference() {
        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        assertEquals("POLYGON ((-122.11534856491807 46.59496055948802, " +
                "-122.64 46.995, -121.915 47.39, " +
                "-121.409 47.413, -120.981 47.316, " +
                "-121.15214608098509 46.82269659010183, " +
                "-121.541 46.995, " +
                "-122.11534856491807 46.59496055948802))", polygon1.difference(polygon2).wkt)
    }

    @Test void disjoint() {

        Polygon polygon1 = new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
                [-120.794, 46.664],
                [-121.541, 46.995],
                [-122.200, 46.536],
                [-121.937, 45.890],
                [-120.959, 46.096],
                [-120.794, 46.664]
        ]])

        Polygon polygon3 = new Polygon([[
                [-120.541, 47.376],
                [-120.695, 47.047],
                [-119.794, 46.830],
                [-119.586, 47.331],
                [-120.102, 47.509],
                [-120.541, 47.376]
        ]])

        assertFalse(polygon1.disjoint(polygon2))
        assertTrue(polygon1.disjoint(polygon3))
        assertTrue(polygon2.disjoint(polygon3))
    }

    @Test void distance() {
        Point point1 = new Point(-122.442, 47.256)
        Point point2 = new Point(-122.321, 47.613)
        assertEquals(0.37694827231332195, point1.distance(point2), 0.0001)
    }

    @Test void getArea() {
        Polygon polygon = new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
        ]])
        assertEquals(1.0652629999999994, polygon.area, 0.0001)
    }

    @Test void getBoundary() {
        Polygon polygon = new Polygon([
            [
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
            ],
            [
                [-122.255, 46.935],
                [-121.992, 46.935],
                [-121.992, 47.100],
                [-122.255, 47.100],
                [-122.255, 46.935]
            ],
            [
                [-121.717, 46.777],
                [-121.398, 46.777],
                [-121.398, 47.002],
                [-121.717, 47.002],
                [-121.717, 46.777]
            ]
        ])
        assertEquals("MULTILINESTRING (" +
                "(-121.915 47.39, -122.64 46.995, -121.739 46.308, -121.168 46.777, -120.981 47.316, " +
                "-121.409 47.413, -121.915 47.39), " +
                "(-122.255 46.935, -121.992 46.935, -121.992 47.1, -122.255 47.1, -122.255 46.935), " +
                "(-121.717 46.777, -121.398 46.777, -121.398 47.002, -121.717 47.002, -121.717 46.777" +
                "))", polygon.boundary.wkt)
    }

    @Test void getEnvelope() {
        Polygon polygon = new Polygon([[
               [-121.915, 47.390],
               [-122.640, 46.995],
               [-121.739, 46.308],
               [-121.168, 46.777],
               [-120.981, 47.316],
               [-121.409, 47.413],
               [-121.915, 47.390]
        ]])
        Envelope env = polygon.envelope
        assertEquals(new Envelope(-122.64, -120.981, 46.308, 47.413), env)
    }

    @Test void getGeometryN() {
        MultiPoint multiPoint = new MultiPoint(
            [98.000, -24.688],
            [-43.255, -20.955],
            [-21.128, -50.821],
            [-122.626, -2.611],
            [-10.857, -32.074]
        )
        assertEquals("POINT (98 -24.688)", multiPoint.getGeometryN(0).wkt)
        assertEquals("POINT (-43.255 -20.955)", multiPoint.getGeometryN(1).wkt)
        assertEquals("POINT (-21.128 -50.821)", multiPoint.getGeometryN(2).wkt)
        assertEquals("POINT (-122.626 -2.611)", multiPoint.getGeometryN(3).wkt)
        assertEquals("POINT (-10.857 -32.074)", multiPoint.getGeometryN(4).wkt)
    }

    @Test void getInteriorPoint() {
        Polygon polygon = new Polygon([[
            [-118.937, 48.327],
            [-121.157, 48.356],
            [-121.684, 46.331],
            [-119.355, 46.498],
            [-119.355, 47.219],
            [-120.629, 47.219],
            [-120.607, 47.783],
            [-119.201, 47.739],
            [-118.937, 48.327]
        ]])
        assertEquals("POINT (-120.43206854809475 47.34584003114768)", polygon.centroid.wkt)
        assertEquals("POINT (-121.00204734961912 47.479)", polygon.interiorPoint.wkt)
    }

    @Test void getLength() {
        LineString line = new LineString(
            [-122.321, 47.620],
            [-117.355, 47.694],
            [-113.928, 46.875]
        )
        assertEquals(8.490056675456453, line.length, 0.0001)
    }

    @Test void getGeometries() {
        MultiPoint multiPoint = new MultiPoint(
            [98.000, -24.688],
            [-43.255, -20.955],
            [-21.128, -50.821],
            [-122.626, -2.611],
            [-10.857, -32.074]
        )
        List<Geometry> geometries = multiPoint.geometries
        assertEquals(multiPoint.getNumGeometries(), geometries.size())
        assertEquals(multiPoint.getGeometryN(0), geometries.get(0))
        assertEquals(multiPoint.getGeometryN(1), geometries.get(1))
        assertEquals(multiPoint.getGeometryN(2), geometries.get(2))
        assertEquals(multiPoint.getGeometryN(3), geometries.get(3))
        assertEquals(multiPoint.getGeometryN(4), geometries.get(4))
    }

    @Test void intersects() {

        Polygon polygon1 = new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
                [-120.794, 46.664],
                [-121.541, 46.995],
                [-122.200, 46.536],
                [-121.937, 45.890],
                [-120.959, 46.096],
                [-120.794, 46.664]
        ]])

        Polygon polygon3 = new Polygon([[
                [-120.541, 47.376],
                [-120.695, 47.047],
                [-119.794, 46.830],
                [-119.586, 47.331],
                [-120.102, 47.509],
                [-120.541, 47.376]
        ]])

        assertTrue(polygon1.intersects(polygon2))
        assertFalse(polygon1.intersects(polygon3))
        assertFalse(polygon2.intersects(polygon3))
    }

    @Test void intersection() {

        Polygon polygon1 = new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
                [-120.794, 46.664],
                [-121.541, 46.995],
                [-122.200, 46.536],
                [-121.937, 45.890],
                [-120.959, 46.096],
                [-120.794, 46.664]
        ]])

        assertEquals("POLYGON ((-121.15214608098509 46.82269659010183, " +
                "-121.168 46.777, -121.739 46.308, " +
                "-122.11534856491807 46.59496055948802, " +
                "-121.541 46.995, " +
                "-121.15214608098509 46.82269659010183))", polygon1.intersection(polygon2).wkt)
    }

    @Test void isRectangle() {

        Polygon polygon1 = new Polygon([[
            [-126.562, 43.580],
            [-113.203, 43.580],
            [-113.203, 49.610],
            [-126.562, 49.610],
            [-126.562, 43.580]
        ]])

        Polygon polygon2 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        assertTrue(polygon1.isRectangle())
        assertFalse(polygon2.isRectangle())

    }

    @Test void isSimple() {
        Geometry geom1 = new LineString(
            [-122.323, 47.599],
            [-122.385, 47.581]
        )
        assertTrue(geom1.simple)

        Geometry geom2 = new LineString(
            [-122.356, 47.537],
            [-122.295, 47.580],
            [-122.284, 47.532],
            [-122.353, 47.574]
        )
        assertFalse(geom2.simple)
    }

    @Test void isWithinDistance() {
        Point point1 = new Point(-122.442, 47.256)
        Point point2 = new Point(-122.321, 47.613)
        assertTrue(point1.isWithinDistance(point2, 0.4))
        assertFalse(point1.isWithinDistance(point2, 0.2))
    }

    @Test void normalize() {
        Geometry geom = new Polygon([10,10],[10,20],[20,20],[20,10],[10,10])
        geom.normalize()
        assertEquals("POLYGON ((10 10, 10 20, 20 20, 20 10, 10 10))", geom.wkt)
    }

    @Test void overlaps() {
        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        Polygon polygon3 = new Polygon([[
            [-120.541, 47.376],
            [-120.695, 47.047],
            [-119.794, 46.830],
            [-119.586, 47.331],
            [-120.102, 47.509],
            [-120.541, 47.376]
        ]])

        assertTrue(polygon1.overlaps(polygon2))
        assertFalse(polygon1.overlaps(polygon3))
        assertFalse(polygon2.overlaps(polygon3))
    }

    @Test void relateIntersectionMatrix() {

        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        IntersectionMatrix matrix = polygon1.relate(polygon2)
        assertEquals("212101212", matrix.toString())
        assertFalse(matrix.contains)
        assertFalse(matrix.coveredBy)
        assertFalse(matrix.covers)
        assertFalse(matrix.disjoint)
        assertTrue(matrix.intersects)
        assertFalse(matrix.within)
    }

    @Test void relate() {

        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        assertTrue(polygon1.relate(polygon2,  "212101212"))
        assertFalse(polygon1.relate(polygon2, "111111111"))
        assertFalse(polygon1.relate(polygon2, "222222222"))
    }

    @Test void symDifference() {

        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        assertEquals("MULTIPOLYGON (" +
                "((-122.11534856491807 46.59496055948802, -122.64 46.995, " +
                "-121.915 47.39, -121.409 47.413, -120.981 47.316, " +
                "-121.15214608098509 46.82269659010183, -121.541 46.995, " +
                "-122.11534856491807 46.59496055948802)), " +
                "((-122.11534856491807 46.59496055948802, -121.739 46.308, " +
                "-121.168 46.777, -121.15214608098509 46.82269659010183, " +
                "-120.794 46.664, -120.959 46.096, -121.937 45.89, -122.2 46.536, " +
                "-122.11534856491807 46.59496055948802)))", polygon1.symDifference(polygon2).wkt)
    }

    @Test void touches() {
        LineString line1 = new LineString([-122.4408030, 47.25315], [-122.4401056, 47.25322])
        LineString line2 = new LineString([-122.4401056, 47.25322], [-122.4399930, 47.25271])
        LineString line3 = new LineString([-122.4394243, 47.25331], [-122.4392044, 47.25241])

        assertTrue(line1.touches(line2))
        assertTrue(line2.touches(line1))
        assertFalse(line1.touches(line3))
        assertFalse(line3.touches(line1))
        assertFalse(line2.touches(line3))
        assertFalse(line3.touches(line2))
    }

    @Test void unionWithOther() {

        Polygon polygon1 = new Polygon([[
            [-121.915, 47.390],
            [-122.640, 46.995],
            [-121.739, 46.308],
            [-121.168, 46.777],
            [-120.981, 47.316],
            [-121.409, 47.413],
            [-121.915, 47.390]
        ]])

        Polygon polygon2 = new Polygon([[
            [-120.794, 46.664],
            [-121.541, 46.995],
            [-122.200, 46.536],
            [-121.937, 45.890],
            [-120.959, 46.096],
            [-120.794, 46.664]
        ]])

        assertEquals("POLYGON ((-122.11534856491807 46.59496055948802, " +
                "-122.64 46.995, -121.915 47.39, " +
                "-121.409 47.413, -120.981 47.316, " +
                "-121.15214608098509 46.82269659010183, " +
                "-120.794 46.664, -120.959 46.096, " +
                "-121.937 45.89, -122.2 46.536, " +
                "-122.11534856491807 46.59496055948802))", polygon1.union(polygon2).wkt)
    }

    @Test void union() {
        MultiPolygon multiPolygon = new MultiPolygon(
            new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
            ]]),
            new Polygon([[
                [-120.794, 46.664],
                [-121.541, 46.995],
                [-122.200, 46.536],
                [-121.937, 45.890],
                [-120.959, 46.096],
                [-120.794, 46.664]
            ]])
        )

        assertEquals("POLYGON ((-121.15214608098509 46.82269659010183, " +
                "-120.794 46.664, -120.959 46.096, -121.937 45.89, " +
                "-122.2 46.536, -122.11534856491807 46.59496055948802, " +
                "-122.64 46.995, -121.915 47.39, -121.409 47.413, -120.981 47.316, " +
                "-121.15214608098509 46.82269659010183))", multiPolygon.union().wkt)
    }

    @Test void within() {

        Polygon polygon1 = new Polygon([[
            [-121.926, 47.279],
            [-120.520, 47.279],
            [-120.520, 48.283],
            [-121.926, 48.283],
            [-121.926, 47.279]
        ]])

        Polygon polygon2 = new Polygon([[
            [-121.508, 47.842],
            [-121.091, 47.842],
            [-121.091, 48.122],
            [-121.508, 48.122],
            [-121.508, 47.842]
        ]])

        Polygon polygon3 = new Polygon([[
            [-120.146, 46.845],
            [-118.586, 46.845],
            [-118.586, 47.620],
            [-120.146, 47.620],
            [-120.146, 46.845]
        ]])

        assertTrue(polygon2.within(polygon1))
        assertFalse(polygon1.within(polygon2))
        assertFalse(polygon2.within(polygon3))
        assertFalse(polygon3.within(polygon2))
        assertFalse(polygon3.within(polygon1))
        assertFalse(polygon1.within(polygon3))
    }

    @Test void prepare() {
        Geometry geom = new Polygon([[
                [-121.915, 47.390],
                [-122.640, 46.995],
                [-121.739, 46.308],
                [-121.168, 46.777],
                [-120.981, 47.316],
                [-121.409, 47.413],
                [-121.915, 47.390]
        ]])
        PreparedGeometry preparedGeom = geom.prepare()
        assertTrue(preparedGeom instanceof PreparedGeometry)
        assertEquals(geom.toString(), preparedGeom.toString())
    }

    @Test void prepareStatic() {
        Geometry geom = new Polygon([[
             [-121.915, 47.390],
             [-122.640, 46.995],
             [-121.739, 46.308],
             [-121.168, 46.777],
             [-120.981, 47.316],
             [-121.409, 47.413],
             [-121.915, 47.390]
        ]])
        PreparedGeometry preparedGeom = Geometry.prepare(geom)
        assertTrue(preparedGeom instanceof PreparedGeometry)
        assertEquals(geom.toString(), preparedGeom.toString())
    }
}

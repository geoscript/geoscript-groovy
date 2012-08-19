package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*
import com.vividsolutions.jts.geom.Point as JtsPoint
import com.vividsolutions.jts.geom.Coordinate

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
       String wkt = "LINESTRING (0.693359375 46.591796875, 5.703125 51.337890625, 9.306640625 48.0419921875, 12.03125 53.0078125, 19.2822265625 45.80078125)"
       Geometry g = Geometry.fromWKT(wkt)
       Geometry buffer = g.singleSidedBuffer(-2)
       assertEquals "POLYGON ((0.693359375 46.591796875, 5.703125 51.337890625, 9.306640625 48.0419921875, 12.03125 53.0078125, 19.2822265625 45.80078125, 17.8723180343991 44.38227571867897, 12.506275490911024 49.71579679220651, 11.060054369792876 47.07994216823311, 10.832564011572027 46.749104791663626, 10.543576944652168 46.470372468507726, 10.20473972588441 46.25497847602753, 9.82970793845354 46.11160347389942, 9.43359585401137 46.04602566227302, 9.032367307756616 46.06088791274607, 8.642192334992414 46.155591257181634, 8.278795497549897 46.32631902690761, 7.9568221633754534 46.566190669457164, 5.727082494228705 48.605586708310895, 2.0688486708215663 45.139891507188345, 0.693359375 46.591796875))", buffer.wkt
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
        assertEquals """{ "type": "Point", "coordinates": [111.0, -47.0] }""", g.geoJSON
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
        Geometry g = new Polygon([0,0],[10,10],[0,10],[10,0],[0,0])
        assertFalse(g.isValid())
        assertEquals("Self-intersection",g.validReason)
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
        // Yes, this is actually correct.  More random points can be generated than the number
        // given if required
        assertEquals 121, pts.numPoints
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
}
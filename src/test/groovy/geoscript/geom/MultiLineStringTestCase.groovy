package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiLineStringTestCase {

    @Test void constructors() {
        MultiLineString m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m1.wkt

        MultiLineString m2 = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m2.wkt

        MultiLineString m3 = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m3.wkt

        MultiLineString m4 = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m4.wkt
    }

    @Test void plus() {
        def m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        def m2 = m1 + new LineString([11,12],[13,14])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8), (11 12, 13 14))", m2.wkt
        def m3 = new MultiLineString([])
        assertEquals "MULTILINESTRING EMPTY", m3.wkt
        def m4 = m3 + new LineString([11,12],[13,14])
        assertEquals "MULTILINESTRING ((11 12, 13 14))", m4.wkt
    }

    @Test void node() {
        String wkt = "LINESTRING (5.19775390625 51.07421875, 7.52685546875 53.7548828125, 11.65771484375 49.931640625, " +
            "7.52685546875 47.20703125, 9.50439453125 54.501953125, 7.35107421875 52.4365234375, 4.53857421875 " +
            "52.65625, 6.38427734375 50.634765625)"
        def lines = new MultiLineString(Geometry.fromWKT(wkt))
        def noded = lines.node(5)
        assertEquals "MULTILINESTRING ((5.19775390625 51.07421875, 5.6 51.6), (5.6 51.6, 6.4 52.6), " +
            "(6.4 52.6, 7.52685546875 53.7548828125), (7.52685546875 53.7548828125, 8.2 53.2), " +
            "(8.2 53.2, 9 52.4), (9 52.4, 11.65771484375 49.931640625), (11.65771484375 49.931640625, " +
            "7.52685546875 47.20703125), (7.52685546875 47.20703125, 9 52.4), (9 52.4, 9.50439453125 " +
            "54.501953125), (9.50439453125 54.501953125, 8.2 53.2), (8.2 53.2, 7.35107421875 52.4365234375), " +
            "(7.35107421875 52.4365234375, 6.4 52.6), (6.4 52.6, 4.53857421875 52.65625), (4.53857421875 " +
            "52.65625, 5.6 51.6), (5.6 51.6, 6.38427734375 50.634765625))", noded.wkt
    }
    
    @Test void merge() {
        String wkt = "MULTILINESTRING((-29 -27,-30 -29.7,-36 -31,-45 -33),(-45 -33,-46 -32))"
        def lines = Geometry.fromWKT(wkt) as MultiLineString
        def mergedLines = lines.merge()
        assertEquals "MULTILINESTRING ((-29 -27, -30 -29.7, -36 -31, -45 -33, -46 -32))", mergedLines.wkt
    }
    
    @Test void polygonize() {
        def lines = new MultiLineString(
            new LineString ([-5.70068359375, 45.1416015625], [2.47314453125, 53.9306640625]),
            new LineString ([-1.21826171875, 53.9306640625], [8.88916015625, 46.1962890625]),
            new LineString ([0.71533203125, 42.63671875], [7.13134765625, 50.37109375]),
            new LineString ([-5.83251953125, 46.943359375], [4.45068359375, 42.98828125])
        )
        def polygons = lines.node(5).polygonize()
        assertEquals "MULTIPOLYGON (((-4.6 46.4, 1 52.2, 5.6 48.6, 1.8 44, -4.6 46.4)))", polygons.wkt
    }
    
}

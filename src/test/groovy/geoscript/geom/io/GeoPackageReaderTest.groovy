package geoscript.geom.io

import geoscript.geom.*
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * The GeoPackageReader UnitTest
 * @author Jared Erickson
 */
class GeoPackageReaderTest {
    
    @Test void readPoint() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "4750000200000000405bc00000000000405bc00000000000c047800000000000c0478000000000000000000001405bc00000000000c047800000000000"
        Point expected = new Point(111,-47)
        Point actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 64, 91, -64, 0, 0, 0, 0, 0, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readLineString() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "4750000200000000405b800000000000405ec00000000000c048000000000000c047800000000000000000000200000003405bc00000000000c047800000000000405ec00000000000c048000000000000405b800000000000c047800000000000"
        LineString expected = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        LineString actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 64, 91, -128, 0, 0, 0, 0, 0, 64, 94, -64, 0, 0, 0, 0, 0, -64, 72, 0, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 3, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, 64, 94, -64, 0, 0, 0, 0, 0, -64, 72, 0, 0, 0, 0, 0, 0, 64, 91, -128, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygon() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "47500002000000003ff000000000000040240000000000003ff00000000000004024000000000000000000000300000003000000053ff00000000000003ff000000000000040240000000000003ff0000000000000402400000000000040240000000000003ff000000000000040240000000000003ff00000000000003ff0000000000000000000054000000000000000400000000000000040100000000000004000000000000000401000000000000040100000000000004000000000000000401000000000000040000000000000004000000000000000000000054014000000000000401400000000000040180000000000004014000000000000401800000000000040180000000000004014000000000000401800000000000040140000000000004014000000000000"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        Polygon actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 3, 0, 0, 0, 5, 63, -16, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 64, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 64, 20, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readPolygonNoHoles() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "47500002000000003ff000000000000040240000000000003ff00000000000004024000000000000000000000300000001000000053ff00000000000003ff000000000000040240000000000003ff0000000000000402400000000000040240000000000003ff000000000000040240000000000003ff00000000000003ff0000000000000"
        Polygon expected = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]))
        Polygon actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 5, 63, -16, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPoint() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "4750000200000000405b800000000000405bc00000000000c047800000000000c0474000000000000000000004000000020000000001405bc00000000000c0478000000000000000000001405b800000000000c047400000000000"
        MultiPoint expected = new MultiPoint([111,-47],[110,-46.5])
        MultiPoint actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 64, 91, -128, 0, 0, 0, 0, 0, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, -64, 71, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 1, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 64, 91, -128, 0, 0, 0, 0, 0, -64, 71, 64, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiLineString() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "47500002000000003ff0000000000000401c000000000000400000000000000040200000000000000000000005000000020000000002000000023ff000000000000040000000000000004008000000000000401000000000000000000000020000000240140000000000004018000000000000401c0000000000004020000000000000"
        MultiLineString expected = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        MultiLineString actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 28, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 32, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 2, 63, -16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 8, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 64, 20, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 64, 28, 0, 0, 0, 0, 0, 0, 64, 32, 0, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readMultiPolygon() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "47500002000000003ff0000000000000402600000000000040000000000000004028000000000000000000000600000002000000000300000001000000043ff0000000000000400000000000000040080000000000004010000000000000401400000000000040180000000000003ff0000000000000400000000000000000000000030000000100000004401c00000000000040200000000000004022000000000000402400000000000040260000000000004028000000000000401c0000000000004020000000000000"
        MultiPolygon expected = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        MultiPolygon actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 38, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 6, 0, 0, 0, 2, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 63, -16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 64, 8, 0, 0, 0, 0, 0, 0, 64, 16, 0, 0, 0, 0, 0, 0, 64, 20, 0, 0, 0, 0, 0, 0, 64, 24, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 4, 64, 28, 0, 0, 0, 0, 0, 0, 64, 32, 0, 0, 0, 0, 0, 0, 64, 34, 0, 0, 0, 0, 0, 0, 64, 36, 0, 0, 0, 0, 0, 0, 64, 38, 0, 0, 0, 0, 0, 0, 64, 40, 0, 0, 0, 0, 0, 0, 64, 28, 0, 0, 0, 0, 0, 0, 64, 32, 0, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readGeometryCollection() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "47500002000000004059000000000000405980000000000000000000000000003ff00000000000000000000007000000020000000001405900000000000000000000000000000000000002000000024059400000000000000000000000000040598000000000003ff0000000000000"
        GeometryCollection expected = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        GeometryCollection actual = reader.read(hexString)
        assertEquals expected.wkt, actual.wkt

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 64, 89, 0, 0, 0, 0, 0, 0, 64, 89, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 7, 0, 0, 0, 2, 0, 0, 0, 0, 1, 64, 89, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 2, 64, 89, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 89, -128, 0, 0, 0, 0, 0, 63, -16, 0, 0, 0, 0, 0, 0] as byte[]
        actual = reader.read(bytes)
        assertEquals expected.wkt, actual.wkt
    }

    @Test void readBounds() {
        GeoPackageReader reader = new GeoPackageReader()
        String hexString = "4750000200000000405b800000000000405bc00000000000c047800000000000c0474000000000000000000004000000020000000001405bc00000000000c0478000000000000000000001405b800000000000c047400000000000"
        Bounds expected = new Bounds(110.0,-47.0,111.0,-46.5)
        Bounds actual = reader.readBounds(hexString)
        assertEquals expected, actual

        byte[] bytes = [71, 80, 0, 2, 0, 0, 0, 0, 64, 91, -128, 0, 0, 0, 0, 0, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, -64, 71, 64, 0, 0, 0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 2, 0, 0, 0, 0, 1, 64, 91, -64, 0, 0, 0, 0, 0, -64, 71, -128, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 64, 91, -128, 0, 0, 0, 0, 0, -64, 71, 64, 0, 0, 0, 0, 0] as byte[]
        actual = reader.readBounds(bytes)
        assertEquals expected, actual
    }
}

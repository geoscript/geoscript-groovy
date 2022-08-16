package geoscript.geom.io

import geoscript.geom.*
import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertArrayEquals
import static org.junit.jupiter.api.Assertions.assertEquals

/**
 * The TwkbWriter UnitTest
 */
class TWkbWriterTest {

    @Test void writePoint() {
        TWkbWriter writer = new TWkbWriter()
        Point p = new Point(111,-47)
        String expected = "E108018086CAA208FF859DC00300"
        String actual = writer.write(p)
        assertEquals expected, actual

        byte[] expecteds = [-31, 8, 1, -128, -122, -54, -94, 8, -1, -123, -99, -64, 3, 0] as byte[]
        byte[] actuals = writer.writeBytes(p)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeLineString() {
        TWkbWriter writer = new TWkbWriter()
        LineString l = new LineString([[111.0, -47],[123.0, -48],[110.0, -47]])
        String expected = "E20801038086CAA208FF859DC0030080B8B872FFD9C40900FF91FD7B80DAC40900"
        String actual = writer.write(l)
        assertEquals expected, actual

        byte[] expecteds = [-30, 8, 1, 3, -128, -122, -54, -94, 8, -1, -123, -99, -64, 3, 0, -128, -72, -72, 114, -1, -39, -60, 9, 0, -1, -111, -3, 123, -128, -38, -60, 9, 0] as byte[]
        byte[] actuals = writer.writeBytes(l)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeLinearRing() {
        TWkbWriter writer = new TWkbWriter()
        LinearRing l = new LinearRing([[111.0, -47],[123.0, -48],[110.0, -47],[111.0, -47]])
        String expected = "E20801048086CAA208FF859DC0030080B8B872FFD9C40900FF91FD7B80DAC4090080DAC4090000"
        String actual = writer.write(l)
        assertEquals expected, actual

        byte[] expecteds = [-30, 8, 1, 4, -128, -122, -54, -94, 8, -1, -123, -99, -64, 3, 0, -128, -72, -72, 114, -1, -39, -60, 9, 0, -1, -111, -3, 123, -128, -38, -60, 9, 0, -128, -38, -60, 9, 0, 0] as byte[]
        byte[] actuals = writer.writeBytes(l)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writePolygon() {
        TWkbWriter writer = new TWkbWriter()
        Polygon p = new Polygon(new LinearRing([1,1], [10,1], [10,10], [1,10], [1,1]),
            [
                new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
            ]
        )
        String expected = "E30801030580DAC40980DAC4090080AAEA5500000080AAEA5500FFA9EA55000000FFA9EA55000580DAC40980DAC4090080B4891300000080B4891300FFB38913000000FFB389130005808ECE1C808ECE1C0080DAC40900000080DAC40900FFD9C409000000FFD9C40900"
        String actual = writer.write(p)
        assertEquals expected, actual

        byte[] expecteds = [-29, 8, 1, 3, 5, -128, -38, -60, 9, -128, -38, -60, 9, 0, -128, -86, -22, 85, 0, 0, 0, -128, -86, -22, 85, 0, -1, -87, -22, 85, 0, 0, 0, -1, -87, -22, 85, 0, 5, -128, -38, -60, 9, -128, -38, -60, 9, 0, -128, -76, -119, 19, 0, 0, 0, -128, -76, -119, 19, 0, -1, -77, -119, 19, 0, 0, 0, -1, -77, -119, 19, 0, 5, -128, -114, -50, 28, -128, -114, -50, 28, 0, -128, -38, -60, 9, 0, 0, 0, -128, -38, -60, 9, 0, -1, -39, -60, 9, 0, 0, 0, -1, -39, -60, 9, 0] as byte[]
        byte[] actuals = writer.writeBytes(p)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeMultiPoint() {
        TWkbWriter writer = new TWkbWriter()
        MultiPoint p = new MultiPoint([111,-47],[110,-46.5])
        String expected = "E40801028086CAA208FF859DC00300FFD9C40980ADE20400"
        String actual = writer.write(p)
        assertEquals expected, actual

        byte[] expecteds = [-28, 8, 1, 2, -128, -122, -54, -94, 8, -1, -123, -99, -64, 3, 0, -1, -39, -60, 9, -128, -83, -30, 4, 0] as byte[]
        byte[] actuals = writer.writeBytes(p)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeMultiLineString() {
        TWkbWriter writer = new TWkbWriter()
        MultiLineString m = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        String expected = "E50801020280DAC40980B489130080B4891380B48913000280B4891380B489130080B4891380B4891300"
        String actual = writer.write(m)
        assertEquals expected, actual

        byte[] expecteds = [-27, 8, 1, 2, 2, -128, -38, -60, 9, -128, -76, -119, 19, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0, 2, -128, -76, -119, 19, -128, -76, -119, 19, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0] as byte[]
        byte[] actuals = writer.writeBytes(m)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeMultiPolygon() {
        TWkbWriter writer = new TWkbWriter()
        MultiPolygon mp = new MultiPolygon([[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]])
        String expected =  "E6080102010480DAC40980B489130080B4891380B489130080B4891380B4891300FFE79226FFE79226000104809C9C39809C9C390080B4891380B489130080B4891380B4891300FFE79226FFE7922600"
        String actual = writer.write(mp)
        assertEquals expected, actual

        byte[] expecteds = [-26, 8, 1, 2, 1, 4, -128, -38, -60, 9, -128, -76, -119, 19, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0, -1, -25, -110, 38, -1, -25, -110, 38, 0, 1, 4, -128, -100, -100, 57, -128, -100, -100, 57, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0, -128, -76, -119, 19, -128, -76, -119, 19, 0, -1, -25, -110, 38, -1, -25, -110, 38, 0] as byte[]
        byte[] actuals = writer.writeBytes(mp)
        assertArrayEquals(expecteds, actuals)
    }

    @Test void writeGeometryCollection() {
        TWkbWriter writer = new TWkbWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0,1.0]))
        String expected = "E7080102E1080180A8D6B9070000E208010280829BC307000080DAC40980DAC40900"
        String actual = writer.write(gc)
        assertEquals expected, actual

        byte[] expecteds = [-25, 8, 1, 2, -31, 8, 1, -128, -88, -42, -71, 7, 0, 0, -30, 8, 1, 2, -128, -126, -101, -61, 7, 0, 0, -128, -38, -60, 9, -128, -38, -60, 9, 0] as byte[]
        byte[] actuals = writer.writeBytes(gc)
        assertArrayEquals(expecteds, actuals)
    }

}

package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.LinearRing
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.assertEquals

class TWkbReaderTest {

    @Test
    void readPoint() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("01000204")
        assertEquals("POINT (1 2)", geometry.wkt)
        geometry = reader.read("01000204".decodeHex())
        assertEquals("POINT (1 2)", geometry.wkt)
    }

    @Test
    void readLineString() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E20801038086CAA208FF859DC0030080B8B872FFD9C40900FF91FD7B80DAC40900")
        LineString l = new LineString([[111.0, -47], [123.0, -48], [110.0, -47]])
        assertEquals(l.wkt, geometry.wkt)
    }

    @Test
    void readLinearRing() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E20801048086CAA208FF859DC0030080B8B872FFD9C40900FF91FD7B80DAC4090080DAC4090000")
        LineString l = new LineString([[111.0, -47], [123.0, -48], [110.0, -47], [111.0, -47]])
        assertEquals(l.wkt, geometry.wkt)
    }

    @Test
    void readPolygon() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E30801030580DAC40980DAC4090080AAEA5500000080AAEA5500FFA9EA55000000FFA9EA55000580DAC40980DAC4090080B4891300000080B4891300FFB38913000000FFB389130005808ECE1C808ECE1C0080DAC40900000080DAC40900FFD9C409000000FFD9C40900")
        Polygon p = new Polygon(new LinearRing([1, 1], [10, 1], [10, 10], [1, 10], [1, 1]),
                [
                        new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                        new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
                ]
        )
        assertEquals(p.wkt, geometry.wkt)
    }

    @Test
    void readMultiPoint() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E40801028086CAA208FF859DC00300FFD9C40980ADE20400")
        MultiPoint p = new MultiPoint([111, -47],[110, -46.5])
        assertEquals(p.wkt, geometry.wkt)
    }

    @Test
    void readMultiLineString() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E50801020280DAC40980B489130080B4891380B48913000280B4891380B489130080B4891380B4891300")
        MultiLineString m = new MultiLineString(new LineString([1, 2],[3, 4]), new LineString([5, 6],[7, 8]))
        assertEquals(m.wkt, geometry.wkt)
    }

    @Test
    void readMultiPolygon() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E6080102010480DAC40980B489130080B4891380B489130080B4891380B4891300FFE79226FFE79226000104809C9C39809C9C390080B4891380B489130080B4891380B4891300FFE79226FFE7922600")
        MultiPolygon mp = new MultiPolygon([[[[1, 2], [3, 4], [5, 6], [1, 2]]], [[[7, 8], [9, 10], [11, 12], [7, 8]]]])
        assertEquals(mp.wkt, geometry.wkt)
    }

    @Test
    void readGeometryCollection() {
        TWkbReader reader = new TWkbReader()
        Geometry geometry = reader.read("E7080102E1080180A8D6B9070000E208010280829BC307000080DAC40980DAC40900")
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0, 1.0]))
        assertEquals(gc.wkt, geometry.wkt)
    }
}

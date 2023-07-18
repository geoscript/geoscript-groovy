package geoscript.geom.io

import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.LinearRing
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*;

class YamlWriterTest {

    @Test void writePoint() {
        YamlWriter writer = new YamlWriter()
        Point p = new Point(-122.23, 45.67)
        assertEquals("""---
geometry:
  type: Point
  coordinates:
  - -122.23
  - 45.67
""", writer.write(p))
    }

    @Test void writeLineString() {
        YamlWriter writer = new YamlWriter()
        LineString l = new LineString([[111.0, -47], [123.0, -48], [110.0, -47]])
        assertEquals """---
geometry:
  type: LineString
  coordinates:
  - - 111.0
    - -47.0
  - - 123.0
    - -48.0
  - - 110.0
    - -47.0
""", writer.write(l)
    }

    @Test void writePolygon() {
        YamlWriter writer = new YamlWriter()
        Polygon p = new Polygon(new LinearRing([1, 1], [10, 1], [10, 10], [1, 10], [1, 1]),
                [
                        new LinearRing([2,2], [4,2], [4,4], [2,4], [2,2]),
                        new LinearRing([5,5], [6,5], [6,6], [5,6], [5,5])
                ]
        )
        String expected = """---
geometry:
  type: Polygon
  coordinates:
  - - - 1.0
      - 1.0
    - - 10.0
      - 1.0
    - - 10.0
      - 10.0
    - - 1.0
      - 10.0
    - - 1.0
      - 1.0
  - - - 2.0
      - 2.0
    - - 4.0
      - 2.0
    - - 4.0
      - 4.0
    - - 2.0
      - 4.0
    - - 2.0
      - 2.0
  - - - 5.0
      - 5.0
    - - 6.0
      - 5.0
    - - 6.0
      - 6.0
    - - 5.0
      - 6.0
    - - 5.0
      - 5.0
"""
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiPoint() {
        YamlWriter writer = new YamlWriter()
        MultiPoint p = new MultiPoint([111, -47],[110, -46.5])
        String expected = """---
geometry:
  type: MultiPoint
  coordinates:
  - - 111.0
    - -47.0
  - - 110.0
    - -46.5
"""
        String actual = writer.write(p)
        assertEquals expected, actual
    }

    @Test void writeMultiLineString() {
        YamlWriter writer = new YamlWriter()
        MultiLineString m = new MultiLineString(new LineString([1, 2],[3, 4]), new LineString([5, 6],[7, 8]))
        String expected = """---
geometry:
  type: MultiLineString
  coordinates:
  - - - 1.0
      - 2.0
    - - 3.0
      - 4.0
  - - - 5.0
      - 6.0
    - - 7.0
      - 8.0
"""
        String actual = writer.write(m)
        assertEquals expected, actual
    }

    @Test void writeMultiPolygon() {
        YamlWriter writer = new YamlWriter()
        MultiPolygon mp = new MultiPolygon([[[[1, 2], [3, 4], [5, 6], [1, 2]]], [[[7, 8], [9, 10], [11, 12], [7, 8]]]])
        String expected = """---
geometry:
  type: MultiPolygon
  coordinates:
  - - - - 1.0
        - 2.0
      - - 3.0
        - 4.0
      - - 5.0
        - 6.0
      - - 1.0
        - 2.0
  - - - - 7.0
        - 8.0
      - - 9.0
        - 10.0
      - - 11.0
        - 12.0
      - - 7.0
        - 8.0
"""
        String actual = writer.write(mp)
        assertEquals expected, actual
    }

    @Test void writeGeometryCollection() {
        YamlWriter writer = new YamlWriter()
        GeometryCollection gc = new GeometryCollection(new Point(100.0, 0.0), new LineString([101.0, 0.0], [102.0, 1.0]))
        String expected = """---
geometry:
  type: GeometryCollection
  geometries:
  - type: Point
    coordinates:
    - 100.0
    - 0.0
  - type: LineString
    coordinates:
    - - 101.0
      - 0.0
    - - 102.0
      - 1.0
"""
        String actual = writer.write(gc)
        assertEquals expected, actual
    }

}

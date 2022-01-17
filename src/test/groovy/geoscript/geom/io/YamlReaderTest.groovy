package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.GeometryCollection
import geoscript.geom.LineString
import geoscript.geom.MultiLineString
import geoscript.geom.MultiPoint
import geoscript.geom.MultiPolygon
import geoscript.geom.Point
import geoscript.geom.Polygon
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*;

class YamlReaderTest {

    @Test void readPoint() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "Point"
  coordinates:
  - -122.23
  - 45.67
""")
        assertTrue(g instanceof Point)
        assertEquals("POINT (-122.23 45.67)", g.wkt)
    }

    @Test void readLineString() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "LineString"
  coordinates:
  - - 111.0
    - -47.0
  - - 123.0
    - -48.0
  - - 110.0
    - -47.0
""")
        assertTrue(g instanceof LineString)
        assertEquals("LINESTRING (111 -47, 123 -48, 110 -47)", g.wkt)
    }

    @Test void readPolygon() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "Polygon"
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
""")
        assertTrue(g instanceof Polygon)
        assertEquals("POLYGON ((1 1, 10 1, 10 10, 1 10, 1 1), (2 2, 4 2, 4 4, 2 4, 2 2), (5 5, 6 5, 6 6, 5 6, 5 5))", g.wkt)
    }

    @Test void readMultiPoint() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "MultiPoint"
  coordinates:
  - - 111.0
    - -47.0
  - - 110.0
    - -46.5
""")
        assertTrue(g instanceof MultiPoint)
        assertEquals("MULTIPOINT ((111 -47), (110 -46.5))", g.wkt)
    }

    @Test void readMultiLineString() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "MultiLineString"
  coordinates:
  - - - 1.0
      - 2.0
    - - 3.0
      - 4.0
  - - - 5.0
      - 6.0
    - - 7.0
      - 8.0
""")
        assertTrue(g instanceof MultiLineString)
        assertEquals("MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", g.wkt)
    }

    @Test void readMultiPolygon() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "MultiPolygon"
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
""")
        assertTrue(g instanceof MultiPolygon)
        assertEquals("MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", g.wkt)
    }

    @Test void readGeometryCollection() {
        YamlReader reader = new YamlReader()
        Geometry g = reader.read("""---
geometry:
  type: "GeometryCollection"
  geometries:
  - type: "Point"
    coordinates:
    - 100.0
    - 0.0
  - type: "LineString"
    coordinates:
    - - 101.0
      - 0.0
    - - 102.0
      - 1.0
""")
        assertTrue(g instanceof GeometryCollection)
        assertEquals("GEOMETRYCOLLECTION (POINT (100 0), LINESTRING (101 0, 102 1))", g.wkt)
    }
}

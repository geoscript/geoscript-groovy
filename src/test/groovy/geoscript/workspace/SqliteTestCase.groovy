package geoscript.workspace

import geoscript.feature.Field
import geoscript.geom.Bounds
import geoscript.geom.Geometry
import geoscript.geom.LineString
import geoscript.geom.Point
import geoscript.layer.Layer
import org.geotools.data.DataStoreFinder
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

class SqliteTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void read() {
        File file = new File(getClass().getClassLoader().getResource("data.sqlite").toURI())
        assertNotNull(file)
        Sqlite sqlite = new Sqlite(file)
        assertEquals "Sqlite", sqlite.format
        List<String> names = sqlite.names
        assertEquals(2, names.size())
        assertTrue(names.contains("points"))
        assertTrue(names.contains("polygons"))
        sqlite.close()
    }

    @Test void writeWKB() {
        File file = folder.newFile("features.sqlite")
        assertNotNull(file)
        Sqlite sqlite = new Sqlite(file)
        try {
            assertEquals "Sqlite", sqlite.format

            // Add
            Layer l = sqlite.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
            assertNotNull(l)
            l.add([new Point(1,1), "one"])
            l.add([new Point(2,2), "two"])
            l.add([new Point(3,3), "three"])
            assertEquals 3, l.count()
            // Get
            assertTrue(sqlite.has("widgets"))
            Layer layer = sqlite.get("widgets")
            assertEquals 3, layer.count()
            // Remove
            sqlite.remove("widgets")
            // Get
            assertFalse(sqlite.has("widgets"))

        } finally {
            sqlite.close()
        }
    }

    @Test void writeWKT() {
        File file = folder.newFile("features.sqlite")
        assertNotNull(file)
        Sqlite sqlite = new Sqlite(file, "WKT")
        try {
            assertEquals "Sqlite", sqlite.format

            // Add
            Layer l = sqlite.create('widgets',[new Field("geom", "Point"), new Field("name", "String")])
            assertNotNull(l)
            l.add([new Point(1,1), "one"])
            l.add([new Point(2,2), "two"])
            l.add([new Point(3,3), "three"])
            assertEquals 3, l.count()
            // Get
            assertTrue(sqlite.has("widgets"))
            Layer layer = sqlite.get("widgets")
            assertEquals 3, layer.count()
            // Remove
            sqlite.remove("widgets")
            // Get
            assertFalse(sqlite.has("widgets"))

        } finally {
            sqlite.close()
        }
    }

    @Test void geometries() {
        File file = folder.newFile("features.sqlite")
        assertNotNull(file)
        Sqlite sqlite = new Sqlite(file)
        try {
            assertEquals "Sqlite", sqlite.format
            Bounds bounds = new Bounds(-180, -90, 180, 90, "EPSG:4326")

            // Points
            Layer points = sqlite.create('points',[new Field("geom", "Point", "EPSG:4326"), new Field("fid", "int")])
            Geometry.createRandomPoints(bounds.geometry, 100).geometries.eachWithIndex { Geometry pt, int i ->
                points.add(points.schema.feature([fid: i, geom: pt], "point.${i}"))
            }
            assertEquals(100, points.count)

            // LineString
            Layer lines = sqlite.create('lines',[new Field("geom", "LineString", "EPSG:4326"), new Field("fid", "int")])
            Geometry.createRandomPoints(bounds.geometry, 100).geometries.eachWithIndex { Geometry pt, int i ->
                LineString lineString = new LineString([pt, pt.translate(2,3)])
                lines.add(lines.schema.feature([fid: i, geom: lineString], "line${i}"))
            }
            assertEquals(100, lines.count)

            // Polygons
            Layer polygons = sqlite.create('polygons',[new Field("geom", "Polygon", "EPSG:4326"), new Field("fid", "int")])
            Geometry.createRandomPoints(bounds.geometry, 100).geometries.eachWithIndex { Geometry pt, int i ->
                polygons.add(polygons.schema.feature([fid: i, geom: pt.buffer(2)], "polygon.${i}"))
            }
            assertEquals(100, polygons.count)

        } finally {
            sqlite.close()
        }
    }

    @Test void writeAndReadworkspaceFactory() {
        // Registered
        assertNotNull(WorkspaceFactories.list().contains(Sqlite.Factory))
        // Read
        File file = new File(getClass().getClassLoader().getResource("data.sqlite").toURI())
        assertNotNull(file)
        Sqlite sqlite = Workspace.getWorkspace([dbtype: 'sqlite', database: file])
        assertNotNull(sqlite)
        sqlite = Workspace.getWorkspace("dbtype=sqlite database=${file}")
        assertNotNull(sqlite)
        sqlite = Workspace.getWorkspace("${file}")
        assertNotNull(sqlite)
        // Write
        Bounds bounds = new Bounds(-180, -90, 180, 90, "EPSG:4326")
        File newFile = folder.newFile("features.sqlite")
        sqlite = Workspace.getWorkspace("${newFile}")
        Layer points = sqlite.create('points',[new Field("geom", "Point", "EPSG:4326"), new Field("fid", "int")])
        Geometry.createRandomPoints(bounds.geometry, 100).geometries.eachWithIndex { Geometry pt, int i ->
            points.add(points.schema.feature([fid: i, geom: pt], "point.${i}"))
        }
        assertEquals(100, points.count)

        newFile = folder.newFile("features2.sqlite")
        sqlite = Workspace.getWorkspace([type: 'sqlite', database: newFile])
        points = sqlite.create('points',[new Field("geom", "Point", "EPSG:4326"), new Field("fid", "int")])
        Geometry.createRandomPoints(bounds.geometry, 100).geometries.eachWithIndex { Geometry pt, int i ->
            points.add(points.schema.feature([fid: i, geom: pt], "point.${i}"))
        }
        assertEquals(100, points.count)
    }

}

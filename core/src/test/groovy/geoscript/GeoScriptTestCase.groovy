package geoscript

import geoscript.feature.Feature
import geoscript.feature.Schema
import geoscript.filter.Expression
import geoscript.geom.Point
import geoscript.layer.Cursor
import geoscript.proj.Geodetic
import geoscript.layer.Raster
import geoscript.workspace.Memory
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.junit.Assert.*

import geoscript.geom.*
import geoscript.layer.Layer
import geoscript.filter.Color
import geoscript.proj.Projection
import geoscript.workspace.Workspace

/**
 * The unit test for the GeoScript class.
 * @author Jared Erickson
 */
class GeoScriptTestCase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Test void wrap() {
        assertTrue GeoScript.wrap(new Feature([the_geom: "POINT (1 1)"],"pt").f) instanceof Feature
        assertTrue GeoScript.wrap(new Schema("widgets", "geom:Point:srid=4326").featureType) instanceof Schema
        assertTrue GeoScript.wrap(new Point(111,-47).g) instanceof Point
        assertTrue GeoScript.wrap(new Bounds(0,0,10,10).env) instanceof Bounds
        assertTrue GeoScript.wrap(new Expression(12).expr) instanceof Expression
        assertTrue GeoScript.wrap(new Layer().getCursor().col) instanceof Cursor
        assertTrue GeoScript.wrap(new Layer().fs) instanceof Layer
        assertTrue GeoScript.wrap(new Projection("EPSG:4326").crs) instanceof Projection
        assertTrue GeoScript.wrap(new Geodetic("clrk66").ellipsoid) instanceof Geodetic
        assertTrue GeoScript.wrap(new Memory().ds) instanceof Workspace
        List data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        assertTrue GeoScript.wrap(new Raster(data, bounds).coverage) instanceof Raster
        assertTrue GeoScript.wrap(1) instanceof Integer
        assertTrue GeoScript.wrap("ABC") instanceof String
    }

    @Test void unwrap() {
        assertTrue GeoScript.unwrap(new Point(111,-47)) instanceof com.vividsolutions.jts.geom.Point
        assertTrue GeoScript.unwrap(new Feature([the_geom: "POINT (1 1)"],"pt")) instanceof org.opengis.feature.simple.SimpleFeature
        assertTrue GeoScript.unwrap(new Schema("widgets", "geom:Point:srid=4326")) instanceof org.opengis.feature.simple.SimpleFeatureType
        assertTrue GeoScript.unwrap(new Bounds(0,0,10,10)) instanceof org.geotools.geometry.jts.ReferencedEnvelope
        assertTrue GeoScript.unwrap(new Expression(12)) instanceof org.opengis.filter.expression.Expression
        assertTrue GeoScript.unwrap(new Layer().getCursor()) instanceof org.geotools.feature.FeatureCollection
        assertTrue GeoScript.unwrap(new Layer()) instanceof org.geotools.data.FeatureSource
        assertTrue GeoScript.unwrap(new Projection("EPSG:4326")) instanceof org.opengis.referencing.crs.CoordinateReferenceSystem
        assertTrue GeoScript.unwrap(new Geodetic("clrk66")) instanceof org.geotools.referencing.datum.DefaultEllipsoid
        assertTrue GeoScript.unwrap(new Memory()) instanceof org.geotools.data.DataStore
        List data = [
                [0,0,0,0,0,0,0],
                [0,1,1,1,1,1,0],
                [0,1,2,3,2,1,0],
                [0,1,1,1,1,1,0],
                [0,0,0,0,0,0,0]
        ]
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")
        assertTrue GeoScript.unwrap(new Raster(data, bounds)) instanceof org.geotools.coverage.grid.GridCoverage2D
        assertTrue GeoScript.unwrap(1) instanceof Integer
        assertTrue GeoScript.unwrap("ABC") instanceof String
    }

    @Test void listAsPoint() {
        use (GeoScript) {
            Point pt = [1,2] as Point
            assertEquals "POINT (1 2)", pt.wkt
        }
    }

    @Test void listAsMultiPoint() {
        use(GeoScript) {
            MultiPoint p = [[1,1],[2,2]] as MultiPoint
            assertEquals "MULTIPOINT ((1 1), (2 2))", p.wkt
        }
    }

    @Test void listAsLineString() {
        use(GeoScript) {
            LineString line = [[1,2],[2,3],[3,4]] as LineString
            assertEquals "LINESTRING (1 2, 2 3, 3 4)", line.wkt
        }
    }

    @Test void listAsMultiLineString() {
        use(GeoScript) {
            MultiLineString line = [[[1,2],[3,4]], [[5,6],[7,8]]] as MultiLineString
            assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", line.wkt
        }
    }

    @Test void listAsBounds() {
        use(GeoScript) {
            Bounds b = [1,3,2,4] as Bounds
            assertEquals "(1.0,3.0,2.0,4.0)", b.toString()
        }
    }

    @Test void listAsPolygon() {
        use(GeoScript) {
            Polygon p = [[[1,2],[3,4],[5,6],[1,2]]] as Polygon
            assertEquals "POLYGON ((1 2, 3 4, 5 6, 1 2))", p.wkt
        }
    }

    @Test void listAsMultiPolygon() {
        use(GeoScript) {
            MultiPolygon p = [[[[1,2],[3,4],[5,6],[1,2]]], [[[7,8],[9,10],[11,12],[7,8]]]] as MultiPolygon
            assertEquals "MULTIPOLYGON (((1 2, 3 4, 5 6, 1 2)), ((7 8, 9 10, 11 12, 7 8)))", p.wkt
        }
    }

    @Test void stringAsColor() {
        use(GeoScript) {
            Color c = "255,255,255" as Color
            assertEquals "#ffffff", c.hex
        }
    }

    @Test void stringAsProjection() {
        use(GeoScript) {
            Projection p = "EPSG:2927" as Projection
            assertEquals "EPSG:2927", p.id
        }
    }

    @Test void stringAsGeometry() {
        use(GeoScript) {
            Geometry g = "POINT (1 1)" as Geometry
            assertEquals "POINT (1 1)", g.wkt
        }
    }

    @Test void stringAsExpression() {
        use(GeoScript) {
            Expression expr = "max(2,4)" as Expression
            assertEquals "max([2], [4])", expr.toString()
        }
    }

    @Test void stringAsGeodetic() {
        use(GeoScript) {
            Geodetic geod = "clrk66" as Geodetic
            assertEquals "Geodetic [SPHEROID[\"Clarke 1866\", 6378206.4, 294.9786982138982]]", geod.toString()
        }
    }

    @Test void stringAsWorkspace() {
        use(GeoScript) {
            URL url = getClass().getClassLoader().getResource("states.shp")
            String str = "url='${url}' 'create spatial index'=true".toString()
            Workspace w = str as Workspace
            assertNotNull(w)
            assertEquals("org.geotools.data.shapefile.ShapefileDataStore", w.format)
        }
    }

    @Test void mapAsWorkspace() {
        use(GeoScript) {
            URL url = getClass().getClassLoader().getResource("states.shp")
            Workspace w = ["url": url] as Workspace
            assertNotNull(w)
            assertEquals("org.geotools.data.shapefile.ShapefileDataStore", w.format)
        }
    }

    @Test void zipUnzip() {
        File dir = folder.newFolder("files")
        List files = ["file1","file2","file3"].collect {
            File file = new File(dir, "${it}.txt")
            file.write("123")
            file
        }
        File zipFile = folder.newFile("files.zip")
        GeoScript.zip(files, zipFile)
        assertTrue zipFile.exists()
        assertTrue zipFile.length() > 0
        File newDir = folder.newFolder("unzipped")
        GeoScript.unzip(zipFile, newDir)
        ["file1","file2","file3"].each { String name ->
            File f = new File(newDir, "${name}.txt")
            assertTrue f.exists()
            assertEquals "123", f.text
        }
    }

    @Test void download() {
        URL url = getClass().getClassLoader().getResource("points.zip")
        File file = folder.newFile("zipped_points")
        GeoScript.download(url, file)
        assertTrue file.exists()
        assertTrue file.length() > 100
    }

}

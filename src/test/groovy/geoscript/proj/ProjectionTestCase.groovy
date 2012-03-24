package geoscript.proj

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point
import geoscript.proj.Projection
import geoscript.geom.Bounds

/**
 * The Projection UnitTest
 */
class ProjectionTestCase {

    @Test void constructors() {

        Projection p1 = new Projection("EPSG:2927")
        assertEquals "EPSG:2927", p1.id

        Projection p2 = new Projection("""GEOGCS["GCS_WGS_1984",DATUM["D_WGS_1984",SPHEROID["WGS_1984",6378137,298.257223563]],PRIMEM["Greenwich",0],UNIT["Degree",0.017453292519943295]]""")
        assertEquals "EPSG:4326", p2.id

        Projection p3 = new Projection(org.geotools.referencing.CRS.decode("EPSG:2927"))
        assertEquals "EPSG:2927", p3.id

        Projection p4 = new Projection(p1)
        assertEquals "EPSG:2927", p4.id

    }

    @Test void getId() {
        Projection p1 = new Projection("EPSG:2927")
        assertEquals "EPSG:2927", p1.id
    }

    @Test void getWkt() {
        Projection p1 = new Projection("EPSG:4326")

        String expected = """GEOGCS["WGS 84", 
  DATUM["World Geodetic System 1984", 
    SPHEROID["WGS 84", 6378137.0, 298.257223563, AUTHORITY["EPSG","7030"]], 
    AUTHORITY["EPSG","6326"]], 
  PRIMEM["Greenwich", 0.0, AUTHORITY["EPSG","8901"]], 
  UNIT["degree", 0.017453292519943295], 
  AXIS["Geodetic longitude", EAST], 
  AXIS["Geodetic latitude", NORTH], 
  AUTHORITY["EPSG","4326"]]"""

        String actual = p1.wkt
        
        assertEquals expected, actual

    }
    
    @Test void getBounds() {
        Bounds bounds = new Projection("EPSG:2927").bounds
        assertNotNull(bounds)
        assertEquals "EPSG:4326", bounds.proj.id
        assertEquals(-124.5, bounds.west, 0.1)
        assertEquals(45.55, bounds.south, 0.1)
        assertEquals(-116.9, bounds.east, 0.1)
        assertEquals(47.6, bounds.north, 0.1)
    }
    

    @Test void transform() {
        Point point = new Point(1181199.82, 652100.72)
        Projection src = new Projection("EPSG:2927")
        Projection dest = new Projection("EPSG:4326")
        Point projectedPoint = src.transform(point, dest)
        assertEquals "POINT (-122.34429002073523 47.10679261700989)", projectedPoint.toString()
    }

    @Test void testToString() {
        Projection p1 = new Projection("EPSG:2927")
        assertEquals "EPSG:2927", p1.toString()
    }

    @Test void testEquals() {
        assertTrue new Projection("EPSG:2927") == new Projection("EPSG:2927")
        assertFalse new Projection("EPSG:2927") == new Projection("EPSG:4326")
    }

    @Test void staticTransform() {
        Point point = new Point(1181199.82, 652100.72)
        Projection src = new Projection("EPSG:2927")
        Projection dest = new Projection("EPSG:4326")
        Point projectedPoint = Projection.transform(point, src, dest)
        assertEquals "POINT (-122.34429002073523 47.10679261700989)", projectedPoint.toString()
    }

    @Test void transform26916To4326() {
        Point point = new Point(776041.0, 3386618.0)
        Point projectedPoint = Projection.transform(point, "EPSG:26916", "EPSG:4326")
        assertEquals "POINT (-84.121611219545 30.580286157377163)", projectedPoint.wkt
    }
}


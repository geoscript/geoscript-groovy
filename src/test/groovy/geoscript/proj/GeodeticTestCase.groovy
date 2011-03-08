package geoscript.proj

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.Point

/**
 * The Geodetic UnitTest
 */
class GeodeticTestCase {

    @Test void constructors() {
        def g1 = new Geodetic()
        assertEquals Geodetic.WGS84, g1.ellipsoid

        def g2 = new Geodetic("clrk66")
        assertEquals Geodetic.CLRK66, g2.ellipsoid

        def g3 = new Geodetic(Geodetic.MERIT)
        assertEquals Geodetic.MERIT, g3.ellipsoid
    }

    @Test void inverse() {
        def g = new Geodetic("clrk66")

        double boston_lat = 42.0+(15.0/60.0)
        double boston_lon = -71.0-(7.0/60.0)
        Point bostonPoint = new Point(boston_lon, boston_lat)

        double portland_lat = 45.0+(31.0/60.0)
        double portland_lon = -123.0-(41.0/60.0)
        Point portlandPoint = new Point(portland_lon, portland_lat)

        def inverse = g.inverse(bostonPoint, portlandPoint)
        assertEquals(-66.531, inverse.forwardAzimuth, 0.001)
        assertEquals(75.654, inverse.backAzimuth, 0.001)
        assertEquals(4164192.708, inverse.distance, 0.001)
    }

    @Test void forward() {
        def g = new Geodetic("clrk66")

        double boston_lat = 42.0+(15.0/60.0)
        double boston_lon = -71.0-(7.0/60.0)
        Point bostonPoint = new Point(boston_lon, boston_lat)
        
        double portland_lat = 45.0+(31.0/60.0)
        double portland_lon = -123.0-(41.0/60.0)
        Point portlandPoint = new Point(portland_lon, portland_lat)

        def forward = g.forward(bostonPoint, -66.531, 4164192.708)
        // 45.517  -123.683        75.654
        assertEquals(45.517, forward.point.y, 0.001)
        assertEquals(-123.683, forward.point.x, 0.001)
        assertEquals(75.654, forward.backAzimuth, 0.001)
    }

    @Test void placePoints() {
        def g = new Geodetic("clrk66")

        double boston_lat = 42.0+(15.0/60.0)
        double boston_lon = -71.0-(7.0/60.0)
        Point bostonPoint = new Point(boston_lon, boston_lat)

        
        double portland_lat = 45.0+(31.0/60.0)
        double portland_lon = -123.0-(41.0/60.0)
        Point portlandPoint = new Point(portland_lon, portland_lat)

        def points = g.placePoints(bostonPoint, portlandPoint, 10)
        assertEquals 10, points.size()

        def expectedPoints = [
            new Point(-75.414, 43.528), 
            new Point(-79.883, 44.637),
            new Point(-84.512, 45.565), 
            new Point(-89.279, 46.299), 
            new Point(-94.156, 46.830), 
            new Point(-99.112, 47.149), 
            new Point(-104.106, 47.251), 
            new Point(-109.100, 47.136), 
            new Point(-114.051, 46.805), 
            new Point(-118.924, 46.262) 
        ]

        points.eachWithIndex{actual, i->
            def expected = expectedPoints[i]
            assertEquals(expected.x, actual.x, 0.001)
            assertEquals(expected.y, actual.y, 0.001)
        }
    }

    @Test void string() {
        assertEquals """Geodetic [SPHEROID["WGS 84", 6378137.0, 298.257223563]]""", (new Geodetic()).toString()
    }
}

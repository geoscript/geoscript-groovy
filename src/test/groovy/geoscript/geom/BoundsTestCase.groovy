package geoscript.geom

import org.junit.Test
import static org.junit.Assert.*
import geoscript.proj.Projection
import org.geotools.geometry.jts.ReferencedEnvelope
import com.vividsolutions.jts.geom.Envelope

/**
 * The Bounds unit test
 */
class BoundsTestCase {
	
    @Test void constructors() {
        ReferencedEnvelope e = new ReferencedEnvelope(1,3,2,4,null)
        Bounds b1 = new Bounds(e)
        assertEquals "(1.0,2.0,3.0,4.0)", b1.toString()
		
        Bounds b2 = new Bounds(1,2,3,4, new Projection("EPSG:2927"))
        assertEquals "(1.0,2.0,3.0,4.0,EPSG:2927)", b2.toString()
		
        Bounds b3 = new Bounds(1,2,3,4)
        assertEquals "(1.0,2.0,3.0,4.0)", b3.toString()

        Bounds b4 = new Bounds(1,2,3,4, "EPSG:2927")
        assertEquals "(1.0,2.0,3.0,4.0,EPSG:2927)", b4.toString()

        Bounds b5 = new Bounds(new Envelope(1,2,3,4))
        assertEquals "(1.0,3.0,2.0,4.0)", b5.toString()
        assertNull b5.proj
    }
	
    @Test void l() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 1.0, b.l, 0.0
    }

    @Test void west() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 1.0, b.west, 0.0
    }

    @Test void b() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 2.0, b.b, 0.0
    }

    @Test void south() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 2.0, b.south, 0.0
    }

    @Test void r() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 3.0, b.r, 0.0
    }

    @Test void east() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 3.0, b.east, 0.0
    }
	
    @Test void t() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 4.0, b.t, 0.0
    }

    @Test void north() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 4.0, b.north, 0.0
    }

    @Test void getWidth() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 2.0, b.width, 0.0
    }

    @Test void getHeight() {
        Bounds b = new Bounds(1,2,3,5)
        assertEquals 3.0, b.height, 0.0
    }

    @Test void getArea() {
        Bounds b = new Bounds(1,2,3,5)
        assertEquals 6.0, b.area, 0.1
    }

    @Test void quadTree() {
        Bounds b = new Bounds(-180, -90, 180, 90, "EPSG:4326")
        List quads = [
            // Level 0
            [-180,-90,180,90],
            // Level 1
            [-180,-90,0,0],
            [-180,0,0,90],
            [0,-90,180,0],
            [0,0,180,90],
            // Level 2
            [-180,-90,-90,-45],
            [-180,-45,-90,0],
            [-180,0,-90,45],
            [-180,45,-90,90],
            [-90,-90,0,-45],
            [-90,-45,0,0],
            [-90,0,0,45],
            [-90,45,0,90],
            [0,-90,90,-45],
            [0,-45,90,0],
            [0,0,90,45],
            [0,45,90,90],
            [90,-90,180,-45],
            [90,-45,180,0],
            [90,0,180,45],
            [90,45,180,90]
        ]

        int c = 0;
        b.quadTree(0,2,{bounds ->
            assertEquals(bounds.west, quads[c][0], 0.1)
            assertEquals(bounds.south, quads[c][1], 0.1)
            assertEquals(bounds.east, quads[c][2], 0.1)
            assertEquals(bounds.north, quads[c][3], 0.1)
            c++
        })
    }

    @Test void expandBy() {
        Bounds b = new Bounds(1,2,3,4)
        Bounds b2 = b.expandBy(10)
        assertEquals b, b2
        assertEquals(-9, b.west, 0.0)
        assertEquals(-8, b.south, 0.0)
        assertEquals(13, b.east, 0.0)
        assertEquals(14, b.north, 0.0)
    }

    @Test void expand() {
        Bounds b1 = new Bounds(1,1,4,4)
        Bounds b2 = new Bounds(8,8,20,20)
        Bounds b3 = b1.expand(b2)
        assertEquals b1, b3
        assertEquals(1, b3.west, 0.0)
        assertEquals(1, b3.south, 0.0)
        assertEquals(20, b3.east, 0.0)
        assertEquals(20, b3.north, 0.0)
    }

    @Test void scale() {
        Bounds b1 = new Bounds(5,5,10,10)
        Bounds b2 = b1.scale(2)
        assertEquals(2.5, b2.west, 0.0)
        assertEquals(2.5, b2.south, 0.0)
        assertEquals(12.5, b2.east, 0.0)
        assertEquals(12.5, b2.north, 0.0)

    }

    @Test void getGeometry() {
        Bounds b = new Bounds(1,2,3,4)
        Geometry g = b.geometry
        assertEquals "POLYGON ((1 2, 1 4, 3 4, 3 2, 1 2))", g.wkt
    }

    @Test void getPolygon() {
        Bounds b = new Bounds(1,2,3,4)
        Geometry g = b.polygon
        assertEquals "POLYGON ((1 2, 1 4, 3 4, 3 2, 1 2))", g.wkt
    }

    @Test void reproject() {
        Bounds b1 = new Bounds(-111, 44.7, -110, 44.9, "EPSG:4326")
        Bounds b2 = b1.reproject("EPSG:26912")
        assertEquals(500000, b2.west as int)
        assertEquals(4949625, b2.south as int)
        assertEquals(579225, b2.east as int)
        assertEquals(4972328, b2.north as int)
    }

    @Test void string() {
        ReferencedEnvelope e = new ReferencedEnvelope(1,3,2,4,null)
        Bounds b1 = new Bounds(e)
        assertEquals "(1.0,2.0,3.0,4.0)", b1.toString()
		
        Bounds b2 = new Bounds(1,2,3,4, new Projection("EPSG:2927"))
        assertEquals "(1.0,2.0,3.0,4.0,EPSG:2927)", b2.toString()
		
        Bounds b3 = new Bounds(1,2,3,4)
        assertEquals "(1.0,2.0,3.0,4.0)", b3.toString()
    }

    @Test void getAt() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals(1, b[0], 0)
        assertEquals(2, b[1], 0)
        assertEquals(3, b[2], 0)
        assertEquals(4, b[3], 0)
        assertNull(b[4])
        def (w,s,e,n) = b
        assertEquals(1, w, 0)
        assertEquals(2, s, 0)
        assertEquals(3, e, 0)
        assertEquals(4, n, 0)
    }

    @Test void tile() {
        def b = new Bounds(0,0,100,100)
        def bounds = b.tile(0.50)
        assertEquals 4, bounds.size()
        assertEquals new Bounds(0.0,0.0,50.0,50.0).geometry.wkt, bounds[0].geometry.wkt
        assertEquals new Bounds(50.0,0.0,100.0,50.0).geometry.wkt, bounds[1].geometry.wkt
        assertEquals new Bounds(0.0,50.0,50.0,100.0).geometry.wkt, bounds[2].geometry.wkt
        assertEquals new Bounds(50.0,50.0,100.0,100.0).geometry.wkt, bounds[3].geometry.wkt
    }

    @Test void isEmpty() {
        def b1 = new Bounds(-10, -20, 10, -10)
        def b2 = new Bounds(-10, 0, 10, 20)
        assertTrue b1.intersection(b2).empty
    }

    @Test void equals() {
        def b1 = new Bounds(-10, -20, 10, -10)
        def b2 = new Bounds(-10, 0, 10, 20)
        def b3 = new Bounds(-10, -20, 10, -10)
        assertFalse b1.equals(b2)
        assertFalse b2.equals(b3)
        assertTrue b1.equals(b3)
    }

    @Test void contains() {
        def b1 = new Bounds(0,0,10,10)
        def b2 = new Bounds(3,3,6,6)
        def b3 = new Bounds(5,5,15,15)
        assertTrue b1.contains(b2)
        assertFalse b2.contains(b1)
        assertFalse b2.contains(b3)
        assertFalse b1.contains(b3)
    }

    @Test void intersects() {
        def b1 = new Bounds(0,0,10,10)
        def b2 = new Bounds(3,3,6,6)
        def b3 = new Bounds(5,5,15,15)
        def b4 = new Bounds(20,25,25,30)
        assertTrue b1.intersects(b2)
        assertTrue b2.intersects(b1)
        assertTrue b2.intersects(b3)
        assertTrue b1.intersects(b3)
        assertFalse b1.intersects(b4)
    }

    @Test void intersection() {
        def b1 = new Bounds(0,0,10,10)
        def b2 = new Bounds(3,3,6,6)
        def b3 = new Bounds(20,25,25,30)
        assertEquals new Bounds(3,3,6,6), b1.intersection(b2)
        assertTrue b1.intersection(b3).empty
    }

    @Test void ensureWidthAndHeight() {

        // Horizontal Line
        def b1 = new LineString([0,0], [0,10]).bounds
        assertEquals new Bounds(0,0,0,10), b1
        assertEquals new Bounds(-5,0,5,10), b1.ensureWidthAndHeight()

        // Vertical Line
        def b2 = new LineString([0,0],[10,0]).bounds
        assertEquals new Bounds(0.0,0.0,10.0,0.0), b2
        assertEquals new Bounds(0.0,-5.0,10.0,5.0), b2.ensureWidthAndHeight()

        // Point
        def b3 = new Point(10,20).bounds
        assertEquals new Bounds(10.0,20.0,10.0,20.0), b3
        assertEquals new Bounds(9.9,19.9,10.1,20.1), b3.ensureWidthAndHeight()
    }

}

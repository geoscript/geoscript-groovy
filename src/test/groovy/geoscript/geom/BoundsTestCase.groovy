package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.proj.Projection
import org.geotools.geometry.jts.ReferencedEnvelope

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
    }
	
    @Test void l() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 1.0, b.l, 0.0
    }
	
    @Test void b() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 2.0, b.b, 0.0
    }
	
    @Test void r() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 3.0, b.r, 0.0
    }
	
    @Test void t() {
        Bounds b = new Bounds(1,2,3,4)
        assertEquals 4.0, b.t, 0.0
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
}
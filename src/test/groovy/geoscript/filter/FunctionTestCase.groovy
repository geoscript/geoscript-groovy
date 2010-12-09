package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.*
import geoscript.layer.Shapefile

/**
 * The Function UnitTest
 * @author Jared Erickson
 */
class FunctionTestCase {

    @Test void constructors() {
        def maxFunction = new Function("max(2,4)")
        assertEquals 4, maxFunction(), 0.1

        def minFunction = new Function("min(2,4)")
        assertEquals 2, minFunction(), 0.1

        def sinPiFunc = new Function("sin(pi()/4)")
        assertNotNull sinPiFunc
        assertEquals 0.7071, sinPiFunc(), 0.0001

        assertTrue new Function("greaterThan(3,2)")()
        assertFalse new Function("greaterThan(2,3)")()

        // Fails because between is a key word in CQL and ECQL!
        //assertTrue new Function("'between'(4,2,6)")()
    }

    @Test void functionWithLayer() {
        def shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def quantileFunction = new Function("Quantile(PERSONS,5)")
        assertNotNull quantileFunction
        def quantiles = quantileFunction(shp)
        assertEquals 5, quantiles.titles.size()
    }

    @Test void functionWithFeature() {
        def shp = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def getGeometryN = new Function("getGeometryN(the_geom,0)")
        assertNotNull getGeometryN
        assertNotNull getGeometryN(shp.features[0])
    }

    @Test void string() {
        def maxFunction = new Function("max(2,4)")
        assertEquals "max([2], [4])", maxFunction.toString()

        def sinPiFunc = new Function("sin(pi()/4)")
        assertEquals "sin([(PI()/4)])", sinPiFunc.toString()
    }

}


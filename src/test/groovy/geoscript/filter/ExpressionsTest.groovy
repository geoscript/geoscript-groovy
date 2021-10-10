package geoscript.filter

import geoscript.feature.Field
import geoscript.geom.Geometry
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import static geoscript.filter.Expressions.*

/**
 * The Expressions UnitTest.
 * @author Jared Erickson
 */
class ExpressionsTest {

    @Test void expression() {
        // From Literal
        Expression e = expression(12)
        assertEquals 12, e.value

        // From GeoTools Literal
        e = expression(Expression.filterFactory.literal(12))
        assertEquals 12, e.value

        // Null
        assertNull(expression(null))
    }

    @Test void color() {
        // Named Color
        Color c = color("wheat")
        assertEquals "#f5deb3", c.value

        // Color wrapped in an Expression
        Expression e = expression(color("black"))
        assertTrue e instanceof Color
        assertEquals "#000000", e.value
    }

    @Test void property() {
        // From String
        Property p = property("NAME")
        assertEquals "NAME", p.value

        // From Field
        p = property(new Field("NAME", "String"))
        assertEquals "NAME", p.value
    }

    @Test void function() {
        // From CQL
        Function f = function("max(2,4)")
        assertEquals "max([2], [4])", f.toString()
        assertTrue f.value instanceof Function

        // From GeoTools Function
        f = function(Function.ff.function("centroid", Function.ff.property("the_geom")))
        assertEquals "centroid([the_geom])", f.toString()

        // From CQL and Closure
        f = function("geom_centroid(the_geom)", { Geometry g ->
            g.centroid
        })
        assertEquals "geom_centroid([the_geom])", f.toString()
    }
}

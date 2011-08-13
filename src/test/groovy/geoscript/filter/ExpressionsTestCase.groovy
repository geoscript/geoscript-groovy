package geoscript.filter

import geoscript.feature.Field
import org.junit.Test
import static org.junit.Assert.*
import static geoscript.filter.Expressions.*

/**
 * The Expressions UnitTest.
 * @author Jared Erickson
 */
class ExpressionsTestCase {

    @Test void methods() {

        Expression e = expression(12)
        assertEquals 12, e.value

        e = expression(color("black"))
        assertTrue e instanceof Color
        assertEquals "#000000", e.value

        e = expression(Expression.filterFactory.literal(12))
        assertEquals 12, e.value

        Color c = color("wheat")
        assertEquals "#f5deb3", c.value

        Property p = property("NAME")
        assertEquals "NAME", p.value

        p = property(new Field("NAME","String"))
        assertEquals "NAME", p.value

        Function f = function("max(2,4)")
        assertEquals "max([2], [4])", f.toString()
        assertTrue f.value instanceof Function
    }
}

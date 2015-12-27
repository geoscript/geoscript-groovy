package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Expression UnitTest
 * @author Jared Erickson
 */
class ExpressionTestCase {

    @Test void constructors() {

        // Create an Expression from a value
        Expression e = new Expression(12)
        assertTrue e.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "12", e.toString()

        // Create an Expression from another Expression
        e = new Expression(e)
        assertTrue e.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "12", e.toString()

        // Create an Expression from a GeoTools Expression
        e = new Expression(Expression.filterFactory.literal(12))
        assertTrue e.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "12", e.toString()
    }

    @Test void evaluate() {
        Expression e = new Expression(12)
        assertEquals 12, e.evaluate()
    }

    @Test void fromCQL() {
        // Literal Number
        Expression e = Expression.fromCQL("12")
        assertTrue e.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "12", e.toString()
        // Literal String
        e = Expression.fromCQL("'A String'")
        assertTrue e.expr instanceof org.opengis.filter.expression.Literal
        assertEquals "A String", e.toString()
        // Property
        e = Expression.fromCQL("NAME")
        assertTrue e instanceof Property
        // Function
        e = Expression.fromCQL("centroid(the_geom)")
        assertTrue e instanceof Function
    }

}

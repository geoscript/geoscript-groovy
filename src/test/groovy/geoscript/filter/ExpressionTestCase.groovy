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

}

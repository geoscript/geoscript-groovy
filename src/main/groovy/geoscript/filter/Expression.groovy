package geoscript.filter

import org.opengis.filter.expression.Expression as GtExpression
import org.geotools.factory.CommonFactoryFinder
import org.opengis.filter.FilterFactory
import org.geotools.filter.text.cql2.CQL

/**
 * A base class for all Expressions.
 * @author Jared Erickson
 */
class Expression {

    /**
     * The wrapped GeoTools Expression
     */
    GtExpression expr

    /**
     * The GeoTools FilterFactory
     */
    protected static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory(null)

    /**
     * Create a new Expression from a GeoTools Expression
     * @param e The GeoTools Expression
     */
    Expression(GtExpression e) {
        this.expr = e
    }

    /**
     * Create a new Expression from an existing Expression
     * @param e The existing Expression
     */
    Expression(Expression e) {
        this(e.expr)
    }

    /**
     * Create a new Expression from a value
     * @param value The value
     */
    Expression(def value) {
        this(filterFactory.literal(value))
    }

    /**
     * Get the underlying value of the Expression
     * @return The value
     */
    def getValue() {
        if (expr instanceof org.opengis.filter.expression.Literal) {
            return (expr as org.opengis.filter.expression.Literal).value
        } else if (expr instanceof org.opengis.filter.expression.Function) {
            return new Function(expr as org.opengis.filter.expression.Function)
        } else if (expr instanceof org.opengis.filter.expression.PropertyName) {
            return (expr as org.opengis.filter.expression.PropertyName).propertyName
        } else {
            return expr
        }
    }

    /**
     * The String representation
     * @return The String representation
     */
    String toString() {
        this.expr
    }

    /**
     * Create a new Expression from a CQL statement
     * @param cql The CQL statement
     * @return An Expression
     */
    static Expression fromCQL(String cql) {
        new Expression(CQL.toExpression(cql))
    }
}

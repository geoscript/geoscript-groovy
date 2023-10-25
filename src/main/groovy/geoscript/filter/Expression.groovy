package geoscript.filter

import geoscript.GeoScript
import org.geotools.api.filter.expression.Expression as GtExpression
import org.geotools.factory.CommonFactoryFinder
import org.geotools.api.filter.FilterFactory
import org.geotools.filter.text.cql2.CQL

/**
 * A base class for all Expressions.
 * <p>You can create a literal Expression by passing a value:</p>
 * <p><blockquote><pre>
 * Expression e = new Expression(12)
 * </pre></blockquote></p>
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
        if (expr instanceof org.geotools.api.filter.expression.Literal) {
            return (expr as org.geotools.api.filter.expression.Literal).value
        } else if (expr instanceof org.geotools.api.filter.expression.Function) {
            return new Function(expr as org.geotools.api.filter.expression.Function)
        } else if (expr instanceof org.geotools.api.filter.expression.PropertyName) {
            return (expr as org.geotools.api.filter.expression.PropertyName).propertyName
        } else {
            return expr
        }
    }

    /**
     * Evaluate the Filter against an Object (commonly a Feature)
     * @param obj Some Object
     * @return A value
     */
    Object evaluate(Object obj = null) {
        GeoScript.wrap(this.expr.evaluate(GeoScript.unwrap(obj)))
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
        def e = CQL.toExpression(cql)
        if (e instanceof org.geotools.api.filter.expression.Literal) {
            return new Expression(e)
        } else if (e instanceof org.geotools.api.filter.expression.Function) {
            return new Function(e)
        } else if (e instanceof org.geotools.api.filter.expression.PropertyName) {
            return new Property(e)
        } else {
            return new Expression(e)
        }
    }
}

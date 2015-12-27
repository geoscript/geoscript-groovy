package geoscript.filter

import geoscript.feature.Field
import org.opengis.filter.expression.Function as GtFunction
import org.opengis.filter.expression.Expression as GtExpression

/**
 * The Expressions class holds static methods for creating new Expressions ({@link Expression}, {@link Color}, {@link Function}, {@link Property}).
 * <p><blockquote><pre>
 * import static geoscript.filter.Expressions.*
 * Expression e = expression(12)
 * Property p = property("NAME")
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Expressions {

    /**
     * Create a new Expression from an existing Expression
     * @param expression The Expression.
     * @return An Expression
     */
    static Expression expression(Expression expression) {
        expression
    }

    /**
     * Create a new Expression from a GeoTools Expression
     * @param expression The GeoTools Expression
     * @return An Expression
     */
    static Expression expression(GtExpression expression) {
        new Expression(expression)
    }

    /**
     * Create a new Expression from a value
     * @param value The vaule
     * @return An Expression
     */
    static Expression expression(def value) {
        if (value == null) {
            return null
        } else {
            return new Expression(value)
        }
    }

    /**
     * Create a new Property Expression from a Field name
     * @param fieldName The Field name
     * @return A new Property Expression
     */
    static Property property(String fieldName) {
        new Property(fieldName)
    }

    /**
     * Create a new Property Expression from a Field
     * @param field The Field
     * @return A new Property Expression
     */
    static Property property(Field field) {
        new Property(field)
    }

    /**
     * Create a new Color Expression from a color convertable object.
     * @param value A value convertable to a Color
     * @return A new Color Expression
     */
    static Color color(def value) {
        new Color(value)
    }

    /**
     * Create a new Function from a GeoTools Function
     * @param function A GeoTools Function
     * @return A GeoScript Function
     */
    static Function function(GtFunction function) {
        new Function(function)
    }

    /**
     * Create a new Function from a CQL statement
     * @param cql A CQL statement
     * @return A new Function
     */
    static Function function(String cql) {
        new Function(cql)
    }

    /**
     * Create a new Function from a Closure
     * @param cql The CQL statement
     * @param closure A Closure
     * @return A new Function
     */
    static Function function(String cql, Closure closure) {
        new Function(cql, closure)
    }
}

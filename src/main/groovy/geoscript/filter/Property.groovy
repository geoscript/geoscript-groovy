package geoscript.filter

import geoscript.feature.Field
import org.geotools.api.filter.expression.PropertyName

/**
 * Property is an {@link Expression} that is a {@link geoscript.feature.Field Field} value
 * <p>You can create a Property by passing a String:</p>
 * <p><blockquote><pre>
 * Property p = new Property("NAME")
 * </pre></blockquote></p>
 * <p>or by passing a {@link geoscript.feature.Field Field}:</p>
 * <p><blockquote><pre>
 * Property p = new Property(new {@link geoscript.feature.Field Field}("NAME","String"))
 * </pre></blockquote></p>
 * @author Jared Erickson
 */
class Property extends Expression {

    /**
     * Create a new Property from a Field name
     * @param name The Field name
     */
    Property(String name) {
        super(Expression.filterFactory.property(name))
    }

    /**
     * Create a new Property from a Field
     * @param field The Field
     */
    Property(Field field) {
        super(Expression.filterFactory.property(field.name))
    }

    /**
     * Create a new Property from a GeoTools PropertyName
     * @param propertyName The GeoTools PropertyName
     */
    Property(PropertyName propertyName) {
        super(propertyName)
    }
}

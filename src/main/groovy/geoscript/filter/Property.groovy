package geoscript.filter

import geoscript.feature.Field
import geoscript.filter.Expression

/**
 * Property is an Expression that is a Field value
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
}

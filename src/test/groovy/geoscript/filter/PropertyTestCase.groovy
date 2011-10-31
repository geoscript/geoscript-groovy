package geoscript.filter

import org.junit.Test
import static org.junit.Assert.*
import geoscript.feature.Field

/**
 * The Property UnitTest
 * @author Jared Erickson
 */
class PropertyTestCase {

    @Test void constructors() {

        // Create a Property from a String
        Property p = new Property("NAME")
        assertTrue p.expr instanceof org.opengis.filter.expression.PropertyName
        assertEquals "NAME", p.toString()

        // Create a Property from a Field
        p = new Property(new Field("NAME","String"))
        assertTrue p.expr instanceof org.opengis.filter.expression.PropertyName
        assertEquals "NAME", p.toString()
    }
}

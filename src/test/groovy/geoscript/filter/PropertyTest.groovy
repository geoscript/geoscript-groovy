package geoscript.filter

import geoscript.feature.Feature
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*
import geoscript.feature.Field

/**
 * The Property UnitTest
 * @author Jared Erickson
 */
class PropertyTest {

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

    @Test void evaluate(){
        Feature f = new Feature([type:"house", price: 24.4], "building1")
        Property p = new Property("type")
        assertEquals "house", p.evaluate(f)
    }
}

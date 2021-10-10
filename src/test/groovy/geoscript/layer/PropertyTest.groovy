package geoscript.layer

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.*

/**
 * The Property Layer Unit Test
 * @author Jared Erickson
 */
class PropertyTest {

    @Test void constructors() {
        File file = new File(getClass().getClassLoader().getResource("points.properties").toURI())
        assertNotNull(file)

        Property property = new Property(file)
        assertNotNull(property)
        assertEquals file, property.file

        assertEquals 4, property.count()
        assertEquals "(0.0,0.0,30.0,30.0)", property.bounds().toString()
    }

}

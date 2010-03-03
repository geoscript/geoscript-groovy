package geoscript.feature

import org.junit.Test
import static org.junit.Assert.*
import geoscript.proj.Projection

/**
 * The Field Unit Test
 */
class FieldTestCase {

    @Test void constructors() {

        Field f1 = new Field("name","String")
        assertEquals "name: String", f1.toString()
        assertEquals "name", f1.name
        assertEquals "String", f1.typ

        Field f2 = new Field("geom","Point", "EPSG:2927")
        assertEquals "geom: Point(EPSG:2927)", f2.toString()
        assertEquals "geom", f2.name
        assertEquals "Point", f2.typ
        assertEquals "EPSG:2927", f2.proj.toString()

        Field f3 = new Field("geom","Point", new Projection("EPSG:2927"))
        assertEquals "geom: Point(EPSG:2927)", f3.toString()
        assertEquals "geom", f3.name
        assertEquals "Point", f3.typ
        assertEquals "EPSG:2927", f3.proj.toString()
    }

    @Test void isGeometry() {

        Field f1 = new Field("name","String")
        assertFalse f1.isGeometry()

        Field f2 = new Field("geom","Point", "EPSG:2927")
        assertTrue f2.isGeometry()

        Field f3 = new Field("geom","Point", new Projection("EPSG:2927"))
        assertTrue f3.isGeometry()
    }

}


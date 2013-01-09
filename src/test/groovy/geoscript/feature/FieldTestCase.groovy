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

        // Create a Field from a List of Parts
        Field f4 = new Field(["name","String"])
        assertEquals "name: String", f4.toString()
        assertEquals "name", f4.name
        assertEquals "String", f4.typ

        Field f5 = new Field(["geom","Point", new Projection("EPSG:2927")])
        assertEquals "geom: Point(EPSG:2927)", f5.toString()
        assertEquals "geom", f5.name
        assertEquals "Point", f5.typ
        assertEquals "EPSG:2927", f5.proj.toString()

        // Create a Field from a Map
        Field f6 = new Field(["name": "name", "type": "String"])
        assertEquals "name: String", f6.toString()
        assertEquals "name", f6.name
        assertEquals "String", f6.typ

        Field f7 = new Field(["name": "geom", "type": "Point", "proj": new Projection("EPSG:2927")])
        assertEquals "geom: Point(EPSG:2927)", f7.toString()
        assertEquals "geom", f7.name
        assertEquals "Point", f7.typ
        assertEquals "EPSG:2927", f7.proj.toString()
    }

    @Test void isGeometry() {

        Field f1 = new Field("name","String")
        assertFalse f1.isGeometry()

        Field f2 = new Field("geom","Point", "EPSG:2927")
        assertTrue f2.isGeometry()

        Field f3 = new Field("geom","Point", new Projection("EPSG:2927"))
        assertTrue f3.isGeometry()
    }

    @Test void equalsAndHashCode() {
        // Same name and type
        assertTrue new Field("name","String").equals(new Field("name","String"))
        // Same name, type, and projection
        assertTrue new Field("geom","Point","EPSG:2927").equals(new Field("geom","Point","EPSG:2927"))
        // Different name, same type
        assertFalse new Field("name","String").equals(new Field("description","String"))
        // Same name, different type
        assertFalse new Field("name","String").equals(new Field("name","int"))
        // Same name and type, different projection
        assertFalse new Field("geom","Point","EPSG:2927").equals(new Field("geom","Point","EPSG:4326"))
    }

}


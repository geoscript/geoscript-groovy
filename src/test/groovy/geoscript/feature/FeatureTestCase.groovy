package geoscript.feature

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*
import com.vividsolutions.jts.geom.Geometry as JtsGeometry

/**
 * The Feature UnitTest
 */
class FeatureTestCase {

    @Test void constructors() {

        // The Schema
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])

        // Create a Feature from a List of values
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f1.toString()
        assertTrue(f1.geom instanceof Geometry)
        assertTrue(f1.f.defaultGeometry instanceof JtsGeometry)

        // Create a Feature from a Map of values
        Feature f2 = new Feature(["geom": new Point(111,-47), "name": "House", "price": 12.5], "house1", s1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f2.toString()
        assertTrue(f2.geom instanceof Geometry)
        assertTrue(f2.f.defaultGeometry instanceof JtsGeometry)

        // Create a Feature from a Map of values with no Schema
        Feature f3 = new Feature(["geom": new Point(111,-47), "name": "House", "price": 12.5], "house1")
        assertEquals "feature.house1 geom: POINT (111 -47), name: House, price: 12.5", f3.toString()
        assertEquals "feature geom: Point, name: String, price: java.math.BigDecimal", f3.schema.toString()
        assertTrue(f3.geom instanceof Geometry)
        assertTrue(f3.f.defaultGeometry instanceof JtsGeometry)

        // Create a feature from a Map of values where some keys don't match the Schema
        Feature f4 = new Feature(["geom": new Point(111,-47), "name": "House", "price": 12.5, "description": "very nice", "rating": 4], "house1", s1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f4.toString()
    }

    @Test void getId() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "house1", f1.id
    }

    @Test void getGeom() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "POINT (111 -47)", f1.geom.toString()
        assertTrue(f1.geom instanceof Geometry)
    }

    @Test void setGeom() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "POINT (111 -47)", f1.geom.toString()
        f1.geom = new Point(121, -49)
        assertEquals "POINT (121 -49)", f1.geom.toString()
    }

    @Test void getBounds() {
        Schema s1 = new Schema("houses", [new Field("geom","LineString", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new LineString([1,1], [10,10]), "House", 12.5], "house1", s1)
        Bounds b = f1.bounds
        assertEquals(1, b.minX, 0.0)
        assertEquals(1, b.minY, 0.0)
        assertEquals(10, b.maxX, 0.0)
        assertEquals(10, b.maxY, 0.0)
        assertEquals("EPSG:4326", b.proj.id)
    }

    @Test void get() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "POINT (111 -47)", f1.get("geom").toString()
        assertEquals 12.5, f1.get("price"), 0.1
        assertEquals "House", f1.get("name")
    }

    @Test void getAt() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "POINT (111 -47)", f1["geom"].toString()
        assertEquals 12.5, f1["price"], 0.1
        assertEquals "House", f1["name"]
    }

    @Test void set() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)

        assertEquals "POINT (111 -47)", f1.get("geom").toString()
        f1.set("geom",  new Point(121, -49))
        assertEquals "POINT (121 -49)", f1.get("geom").toString()

        assertEquals 12.5, f1.get("price"), 0.1
        f1.set("price", 23.9)
        assertEquals 23.9, f1.get("price"), 0.1

        assertEquals "House", f1.get("name")
        f1.set("name", "Work")
        assertEquals "Work", f1.get("name")
    }

    @Test void putAt() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)

        assertEquals "POINT (111 -47)", f1["geom"].toString()
        f1["geom"] = new Point(121, -49)
        assertEquals "POINT (121 -49)", f1["geom"].toString()

        assertEquals 12.5, f1["price"], 0.1
        f1["price"] =  23.9
        assertEquals 23.9, f1["price"], 0.1

        assertEquals "House", f1["name"]
        f1["name"] = "Work"
        assertEquals "Work", f1["name"]
    }

    @Test void getAttributes() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        Map attributes = f1.attributes
        assertEquals "POINT (111 -47)", attributes['geom'].toString()
        assertTrue(attributes['geom'] instanceof Geometry)
        assertEquals "House", attributes['name']
        assertEquals 12.5, attributes['price'], 0.1
    }

    @Test void toStringTest() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = new Feature([new Point(111,-47), "House", 12.5], "house1", s1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f1.toString()
    }

}


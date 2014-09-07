package geoscript.feature

import org.junit.Test
import static org.junit.Assert.*
import geoscript.geom.*

/**
 * The Schema UniTest
 */
class SchemaTestCase {

    @Test void constructors() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets geom: Point, name: String, price: Float", s1.toString()
        assertEquals "http://geoscript.org/feature", s1.uri

        Schema s2 = new Schema("widgets", [["geom","Point"], ["name","string"], ["price","float"]])
        assertEquals "widgets geom: Point, name: String, price: Float", s2.toString()
        assertEquals "http://geoscript.org/feature", s2.uri

        Schema s3 = new Schema("widgets", [[name: "geom",type: "Point"], [name: "name", type: "string"], [name: "price", type: "float"]])
        assertEquals "widgets geom: Point, name: String, price: Float", s3.toString()
        assertEquals "http://geoscript.org/feature", s3.uri

        Schema s4 = new Schema("widgets", "geom:Point:srid=4326,name:String,price:float")
        assertEquals "widgets geom: Point(EPSG:4326), name: String, price: Float", s4.toString()
        assertEquals "http://geoscript.org/feature", s4.uri

        Schema s5 = new Schema("widgets", "geom:Point:srid=4326,name:String,price:float", "http://geotools.org/feature")
        assertEquals "widgets geom: Point(EPSG:4326), name: String, price: Float", s5.toString()
        assertEquals "http://geotools.org/feature", s5.uri

        Schema s6 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")], "http://geotools.org/feature")
        assertEquals "widgets geom: Point, name: String, price: Float", s6.toString()
        assertEquals "http://geotools.org/feature", s6.uri
    }

    @Test void getName() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets", s1.name
    }

    @Test void getGeom() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Field field = s1.geom
        assertEquals "geom: Point", field.toString()

        Schema s2 = new Schema("widgets", [new Field("name","string"), new Field("price","float")])
        assertNull s2.geom
    }

    @Test void getProj() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point", "EPSG:4326"), new Field("name","string"), new Field("price","float")])
        assertEquals "EPSG:4326", s1.proj.id

        Schema s2 = new Schema("widgets", [new Field("name","string"), new Field("price","float")])
        assertNull s2.proj
    }

    @Test void field() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "geom: Point", s1.field("geom").toString()
        assertEquals "name: String", s1.field("name").toString()
        assertEquals "price: Float", s1.field("price").toString()
    }

    @Test void get() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "geom: Point", s1.get("geom").toString()
        assertEquals "name: String", s1.get("name").toString()
        assertEquals "price: Float", s1.get("price").toString()
    }

    @Test void getFields() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        List<Field> fields = s1.fields
        assertEquals 3, fields.size()
        assertEquals "geom: Point", fields[0].toString()
        assertEquals "name: String", fields[1].toString()
        assertEquals "price: Float", fields[2].toString()
    }

    @Test void has() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertTrue s1.has("geom")
        assertTrue s1.has("name")
        assertTrue s1.has("price")
        assertFalse s1.has("asdfasd")
        assertFalse s1.has("NOT A FIELD NAME")
        assertTrue s1.has(s1.get("geom"))
    }

    @Test void featureWithDefaultId() {
        Schema schema = new Schema("parcels", [new Field("geom","Polygon", "EPSG:4326"), new Field("number","string")])
        Feature feature = schema.feature([new Bounds(0,0,10,10).polygon, "123456"])
        assertNotNull feature.id
        assertTrue feature.id.length() > 10
        feature = schema.feature([geom: new Bounds(0,0,10,10).polygon, number: "123456"])
        assertNotNull feature.id
        assertTrue feature.id.length() > 10
    }

    @Test void feature() {
        Schema s1 = new Schema("houses", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        Feature f1 = s1.feature([new Point(111,-47), "House", 12.5],"house1")
        assertNotNull(f1)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f1.toString()

        Feature f2 = s1.feature(["geom": new Point(111,-47), "name": "House", "price": 12.5],"house1")
        assertNotNull(f2)
        assertEquals "houses.house1 geom: POINT (111 -47), name: House, price: 12.5", f2.toString()

        Feature f3 = s1.feature()
        assertNotNull(f3)
        assertNull f3.geom
        assertNull f3['name']
        assertNull f3['price']
    }

    @Test void toStringTest() {
        Schema s1 = new Schema("widgets", [new Field("geom","Point"), new Field("name","string"), new Field("price","float")])
        assertEquals "widgets geom: Point, name: String, price: Float", s1.toString()
    }

    @Test void reproject() {
        Schema s1 = new Schema("houses", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        assertEquals "houses geom: Point(EPSG:2927), name: String, price: Float", s1.toString()
        Schema s2 = s1.reproject("EPSG:4326","houses in lat/lon")
        assertEquals "houses in lat/lon geom: Point(EPSG:4326), name: String, price: Float", s2.toString()
    }

    @Test void reprojectWithNull() {
        Schema s1 = new Schema("houses", [new Field("geom","Point", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        assertEquals "houses geom: Point(EPSG:2927), name: String, price: Float", s1.toString()
        Schema s2 = s1.reproject(null,"houses")
        assertEquals "houses geom: Point, name: String, price: Float", s2.toString()
        assertNull s2.geom.proj
    }

    @Test void changeGeometryType() {
        Schema s1 = new Schema("houses", [new Field("geom","Polygon", "EPSG:2927"), new Field("name","string"), new Field("price","float")])
        assertEquals "houses geom: Polygon(EPSG:2927), name: String, price: Float", s1.toString()
        Schema s2 = s1.changeGeometryType("Polygon","houses buffered")
        assertEquals "houses buffered geom: Polygon(EPSG:2927), name: String, price: Float", s2.toString()
    }

    @Test void addField() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String")])
        assertEquals "points geom: Point(EPSG:4326), name: String", s1.toString()
        Schema s2 = s1.addField(new Field("description","String"), "points_with_description")
        assertEquals "points_with_description geom: Point(EPSG:4326), name: String, description: String", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String", s1.toString()
    }

    @Test void addFields() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String")])
        assertEquals "points geom: Point(EPSG:4326), name: String", s1.toString()
        Schema s2 = s1.addFields([
            new Field("x","double"),
            new Field("y","double")],
            "points_with_xy"
        )
        assertEquals "points_with_xy geom: Point(EPSG:4326), name: String, x: Double, y: Double", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String", s1.toString()
    }

    @Test void removeField() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String"), new Field("x", "Double"), new Field("y", "Double")])
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s2 = s1.removeField(s1.get("name"), "points_without_name")
        assertEquals "points_without_name geom: Point(EPSG:4326), x: Double, y: Double", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
    }

    @Test void removeFields() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String"), new Field("x", "Double"), new Field("y", "Double")])
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s2 = s1.removeFields([s1.get("x"), s1.get("y")], "points_without_xy")
        assertEquals "points_without_xy geom: Point(EPSG:4326), name: String", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
    }

    @Test void changeField() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String"), new Field("x", "Double"), new Field("y", "Double")])
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s2 = s1.changeField(s1.get("name"), new Field("id","int"), "points_with_id")
        assertEquals "points_with_id geom: Point(EPSG:4326), id: Integer, x: Double, y: Double", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
    }

    @Test void changeFields() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String"), new Field("x", "Double"), new Field("y", "Double")])
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s2 = s1.changeFields([
            (s1.get("geom")): new Field("geom","Polygon", "EPSG:2927"),
            (s1.get("name")): new Field("id","int")
        ], "points_new")
        assertEquals "points_new geom: Polygon(EPSG:2927), id: Integer, x: Double, y: Double", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
    }

    @Test void equalsAndHashCode() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String")])
        Schema s2 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String")])
        Schema s3 = new Schema("facilities", [new Field("geom","Point","EPSG:4326"), new Field("name","String")])
        Schema s4 = new Schema("hours", [new Field("the_geom","Polygon","EPSG:2927"), new Field("address","String")])
        // Equals
        assertTrue s1.equals(s2)
        assertFalse s1.equals(s3)
        assertFalse s1.equals(s4)
        assertFalse s2.equals(s3)
        assertFalse s2.equals(s4)
        assertFalse s3.equals(s4)
        // Hashcode
        assertTrue s1.hashCode() == s2.hashCode()
        assertFalse s1.hashCode() == s3.hashCode()
        assertFalse s1.hashCode() == s4.hashCode()
        assertFalse s2.hashCode() == s3.hashCode()
        assertFalse s2.hashCode() == s4.hashCode()
        assertFalse s3.hashCode() == s4.hashCode()
    }

    @Test void addSchema() {
        Schema s1 = new Schema("points", [new Field("geom", "Point", "EPSG:4326"), new Field("id","int"), new Field("name","String")])
        Schema s2 = new Schema("houses", [new Field("geom", "Point", "EPSG:4326"), new Field("id","int"), new Field("description","String"), new Field("price","Double")])

        // No prefix all, No duplicates,
        Map result = s1.addSchema(s2, "points_houses")
        Schema s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), id: Integer, name: String, description: String, price: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "id", result.fields[0]["id"]
        assertEquals "name", result.fields[0]["name"]
        assertEquals "description", result.fields[1]["description"]
        assertEquals "price", result.fields[1]["price"]

        // Postfix all, No duplicates
        result = s1.addSchema(s2, "points_houses", postfixAll: true, includeDuplicates: false)
        s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), id1: Integer, name1: String, id2: Integer, description2: String, price2: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "id1", result.fields[0]["id"]
        assertEquals "name1", result.fields[0]["name"]
        assertEquals "id2", result.fields[1]["id"]
        assertEquals "description2", result.fields[1]["description"]
        assertEquals "price2", result.fields[1]["price"]

        // No postfix all, Duplicates
        result = s1.addSchema(s2, "points_houses", postfixAll: false, includeDuplicates: true)
        s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), id: Integer, name: String, id2: Integer, description: String, price: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "id", result.fields[0]["id"]
        assertEquals "name", result.fields[0]["name"]
        assertEquals "id2", result.fields[1]["id"]
        assertEquals "description", result.fields[1]["description"]
        assertEquals "price", result.fields[1]["price"]

        // Prefix all, Duplicates
        result = s1.addSchema(s2, "points_houses", postfixAll: true, includeDuplicates: true)
        s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), id1: Integer, name1: String, id2: Integer, description2: String, price2: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "id1", result.fields[0]["id"]
        assertEquals "name1", result.fields[0]["name"]
        assertEquals "id2", result.fields[1]["id"]
        assertEquals "description2", result.fields[1]["description"]
        assertEquals "price2", result.fields[1]["price"]

        // Max Field name length (shapefiles)
        result = s1.addSchema(s2, "points_houses", maxFieldNameLength: 10, postfixAll: true)
        s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), id1: Integer, name1: String, id2: Integer, descriptio2: String, price2: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "id1", result.fields[0]["id"]
        assertEquals "name1", result.fields[0]["name"]
        assertEquals "id2", result.fields[1]["id"]
        assertEquals "descriptio2", result.fields[1]["description"]
        assertEquals "price2", result.fields[1]["price"]

        // Postfix all, custom postfixes
        result = s1.addSchema(s2, "points_houses", postfixAll: true, firstPostfix: 'A', secondPostfix: 'B')
        s3 = result.schema
        assertEquals "points_houses geom: Point(EPSG:4326), idA: Integer, nameA: String, idB: Integer, descriptionB: String, priceB: Double", s3.toString()
        assertEquals "geom", result.fields[0]["geom"]
        assertEquals "idA", result.fields[0]["id"]
        assertEquals "nameA", result.fields[0]["name"]
        assertEquals "idB", result.fields[1]["id"]
        assertEquals "descriptionB", result.fields[1]["description"]
        assertEquals "priceB", result.fields[1]["price"]
    }

    @Test void includeFields() {
        Schema s1 = new Schema("points", [new Field("geom","Point","EPSG:4326"), new Field("name","String"), new Field("x", "Double"), new Field("y", "Double")])
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s2 = s1.includeFields([s1.get("geom"), s1.get("name")], "points_new")
        assertEquals "points_new geom: Point(EPSG:4326), name: String", s2.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
        Schema s3 = s1.includeFields(["geom","x","y"], "points_new")
        assertEquals "points_new geom: Point(EPSG:4326), x: Double, y: Double", s3.toString()
        assertEquals "points geom: Point(EPSG:4326), name: String, x: Double, y: Double", s1.toString()
    }

    @Test void lookUpAlias() {
        assertEquals "CircularRing", Schema.lookUpAlias("org.geotools.geometry.jts.CircularRing")
        assertEquals "CircularString", Schema.lookUpAlias("org.geotools.geometry.jts.CircularString")
        assertEquals "CompoundRing", Schema.lookUpAlias("org.geotools.geometry.jts.CompoundRing")
        assertEquals "CompoundCurve", Schema.lookUpAlias("org.geotools.geometry.jts.CompoundCurve")
    }

    @Test void lookUpBinding() {
        assertEquals "org.geotools.geometry.jts.CircularRing", Schema.lookUpBinding("CircularRing")
        assertEquals "org.geotools.geometry.jts.CircularString", Schema.lookUpBinding("CircularString")
        assertEquals "org.geotools.geometry.jts.CompoundRing", Schema.lookUpBinding("CompoundRing")
        assertEquals "org.geotools.geometry.jts.CompoundCurve", Schema.lookUpBinding("CompoundCurve")
    }

}


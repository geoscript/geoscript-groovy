package geoscript.filter

import org.junit.Test
import static org.junit.Assert.assertEquals
import geoscript.geom.*
import geoscript.layer.*

class FilterTestCase {
    
    @Test void constructors() {
        Filter f1 = new Filter("name='foobar'")
        assertEquals "[ name = foobar ]", f1.toString()

        Filter f2 = new Filter("<filter><PropertyIsEqualTo><PropertyName>name</PropertyName><Literal>foobar</Literal></PropertyIsEqualTo></filter>")
        assertEquals "[ name = foobar ]", f2.toString()

        Filter f3 = new Filter(f1)
        assertEquals f1.toString(), f3.toString()
    }
    
    @Test void getCql() {
        Filter f = new Filter("name='foobar'")
        assertEquals "name = 'foobar'", f.cql
    }
    
    @Test void stringRepresentation() {
        Filter f = new Filter("name='foobar'")
        assertEquals "[ name = foobar ]", f.toString()
    }

    @Test void getXml() {
        Filter f = new Filter("name='foobar'")
        String actual = f.xml
        String expected = """<ogc:Filter xmlns:ogc="http://www.opengis.net/ogc" xmlns:gml="http://www.opengis.net/gml">
    <ogc:PropertyIsEqualTo>
        <ogc:PropertyName>name</ogc:PropertyName>
        <ogc:Literal>foobar</ogc:Literal>
    </ogc:PropertyIsEqualTo>
</ogc:Filter>
"""
        assertEquals expected, actual
    }

    @Test void bbox() {

        Filter f1 = Filter.bbox(new Bounds(10,20,30,40))
        assertEquals "BBOX(the_geom, 10.0,20.0,30.0,40.0)", f1.cql

        Filter f2 = Filter.bbox(new Bounds(-102, 43.5, -100, 47.5))
        assertEquals "BBOX(the_geom, -102.0,43.5,-100.0,47.5)", f2.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(f2)
        assertEquals 2, features.size()
        assertEquals 2, layer.getFeatures("BBOX(the_geom, -102.0,43.5,-100.0,47.5)").size()
    }

    @Test void contains() {
        Filter f1 = Filter.contains(Geometry.fromWKT("POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45))"))
        assertEquals "CONTAINS(the_geom, POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45)))", f1.cql

        Layer layer = new Shapefile(new File(getClass().getClassLoader().getResource("states.shp").toURI()))
        def features = layer.getFeatures(Filter.contains(Geometry.fromWKT("POINT (-100 47)")))
        assertEquals 1, features.size()
        assertEquals "ND", features[0].get("STATE_ABBR")
    }

    @Test void dwithin() {
        Filter f1 = Filter.dwithin("the_geom", Geometry.fromWKT("POINT (-100 47)"), 10.2, "feet")
        assertEquals "DWITHIN(the_geom, POINT (-100 47), 10.2, feet)", f1.cql
    }

    @Test void cross() {
        Filter f1 = Filter.cross("the_geom", Geometry.fromWKT("LINESTRING (-104 45, -95 45)"))
        assertEquals "CROSS(the_geom, LINESTRING (-104 45, -95 45))", f1.cql
    }

    @Test void intersect() {
        Filter f1 = Filter.intersect(Geometry.fromWKT("POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45))"))
        assertEquals "INTERSECT(the_geom, POLYGON ((-104 45, -95 45, -95 50, -104 50, -104 45)))", f1.cql
    }
}


package geoscript.filter

import org.junit.Test
import static org.junit.Assert.assertEquals

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
}


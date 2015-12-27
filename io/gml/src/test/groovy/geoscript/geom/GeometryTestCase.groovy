package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class GeometryTestCase {

    @Test void getGml2() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>", g.gml2
    }

    @Test void fromGml2() {
        Geometry g = Geometry.fromGML2("<gml:Point><gml:coordinates>111.0,-47.0</gml:coordinates></gml:Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void getGml3() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals "<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>", g.gml3
    }

    @Test void fromGml3() {
        Geometry g = Geometry.fromGML3("<gml:Point><gml:pos>111.0 -47.0</gml:pos></gml:Point>")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void fromString() {
        // GML 2
        Geometry g = Geometry.fromString("<gml:Point><gml:coordinates>1,1</gml:coordinates></gml:Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
        // GML 3
        g = Geometry.fromString("<gml:Point><gml:pos>1 1</gml:pos></gml:Point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
    }

}

package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class GeometryTestCase {

    @Test void getGeoJSON() {
        Geometry g = Geometry.fromWKT("POINT (111 -47)")
        assertEquals """{"type":"Point","coordinates":[111,-47]}""", g.geoJSON
    }

    @Test void getGeoJSONWithDecimals() {
        Geometry g = Geometry.fromWKT("POINT (111.123456 -47.123456)")
        assertEquals """{"type":"Point","coordinates":[111.123456,-47.123456]}""", g.getGeoJSON(decimals: 6)
    }

    @Test void fromGeoJSON() {
        Geometry g = Geometry.fromGeoJSON("""{ "type": "Point", "coordinates": [111.0, -47.0] }""")
        assertEquals "POINT (111 -47)", g.toString()
    }

    @Test void fromString() {
        Geometry g = Geometry.fromString("""{ "type": "Point", "coordinates": [1.0, 1.0] }""")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
    }
}

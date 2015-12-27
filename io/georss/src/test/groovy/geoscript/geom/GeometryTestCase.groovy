package geoscript.geom

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

/**
 * Created by jericks on 11/22/15.
 */
class GeometryTestCase {

    @Test void fromString() {
        Geometry g = Geometry.fromString("<georss:point>1 1</georss:point>")
        assertNotNull g
        assertEquals "POINT (1 1)", g.wkt
    }

}

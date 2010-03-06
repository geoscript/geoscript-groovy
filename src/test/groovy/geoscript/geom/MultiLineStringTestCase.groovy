package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiLineStringTestCase {

    @Test void constructors() {
        MultiLineString m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m1.wkt

        MultiLineString m2 = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m2.wkt
    }
	
}
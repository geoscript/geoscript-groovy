package geoscript.geom

import org.junit.Test
import static org.junit.Assert.assertEquals

class MultiLineStringTestCase {

    @Test void constructors() {
        MultiLineString m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m1.wkt

        MultiLineString m2 = new MultiLineString([[1,2],[3,4]], [[5,6],[7,8]])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m2.wkt

        MultiLineString m3 = new MultiLineString([new LineString([1,2],[3,4]), new LineString([5,6],[7,8])])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m3.wkt

        MultiLineString m4 = new MultiLineString([[[1,2],[3,4]], [[5,6],[7,8]]])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8))", m4.wkt
    }

    @Test void plus() {
        def m1 = new MultiLineString(new LineString([1,2],[3,4]), new LineString([5,6],[7,8]))
        def m2 = m1 + new LineString([11,12],[13,14])
        assertEquals "MULTILINESTRING ((1 2, 3 4), (5 6, 7 8), (11 12, 13 14))", m2.wkt
        def m3 = new MultiLineString([])
        assertEquals "MULTILINESTRING EMPTY", m3.wkt
        def m4 = m3 + new LineString([11,12],[13,14])
        assertEquals "MULTILINESTRING ((11 12, 13 14))", m4.wkt
    }

}

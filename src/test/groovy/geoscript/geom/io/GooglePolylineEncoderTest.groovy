package geoscript.geom.io

import geoscript.geom.Geometry
import geoscript.geom.LineString
import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

/**
 * The GooglePolylineEnocder Unit Test.
 * @author Jared Erickson
 */
class GooglePolylineEncoderTest {

    @Test void write() {
        GooglePolylineEncoder encoder = new GooglePolylineEncoder()
        LineString lineString = new LineString([-120.2, 38.5], [-120.95, 40.7], [-126.453, 43.252])
        String actual = encoder.write(lineString)
        String expected = "_p~iF~ps|U_ulLnnqC_mqNvxq`@"
        assertEquals expected, actual
    }

    @Test void read() {
        GooglePolylineEncoder encoder = new GooglePolylineEncoder()
        LineString actual = encoder.read("_p~iF~ps|U_ulLnnqC_mqNvxq`@")
        LineString expected = new LineString([-120.2, 38.5], [-120.95, 40.7], [-126.453, 43.252])
        assertEquals expected, actual
    }

    @Test void writeRead() {
        GooglePolylineEncoder encoder = new GooglePolylineEncoder()
        LineString lineString = new LineString([-120.2, 38.5], [-120.95, 40.7], [-126.453, 43.252])
        LineString result = encoder.read(encoder.write(lineString))
        assertEquals lineString, result
    }

    @Test void readWrite() {
        GooglePolylineEncoder encoder = new GooglePolylineEncoder()
        String str = "_p~iF~ps|U_ulLnnqC_mqNvxq`@"
        String result = encoder.write(encoder.read(str))
        assertEquals str, result
    }

    @Test void writers() {
        assertNotNull Writers.find("GooglePolylineEncoder")
    }

    @Test void readers() {
        assertNotNull Readers.find("GooglePolylineEncoder")
    }
}

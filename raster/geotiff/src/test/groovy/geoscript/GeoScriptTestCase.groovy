package geoscript

import geoscript.layer.Format
import geoscript.layer.GeoTIFF
import org.junit.Test

import static org.junit.Assert.assertTrue


class GeoScriptTestCase {

    @Test void wrap() {
        assertTrue GeoScript.wrap(new GeoTIFF(null).gridFormat) instanceof Format
    }

    @Test void unwrap() {
        assertTrue GeoScript.unwrap(new GeoTIFF()) instanceof org.geotools.coverage.grid.io.AbstractGridFormat
    }

}

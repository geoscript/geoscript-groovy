package geoscript.raster

import geoscript.geom.Bounds
import geoscript.proj.Projection
import org.junit.Test
import static org.junit.Assert.*

/**
 * The Raster unit test
 */
class RasterTestCase {
	
    @Test void raster() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)

        assertEquals("GeoTIFF", raster.format)
        assertEquals("EPSG:2927", raster.proj.id)

        Bounds bounds = raster.bounds
        assertEquals(1166191.0260847565, bounds.west, 0.0000000001)
        assertEquals(1167331.8522748263, bounds.east, 0.0000000001)
        assertEquals(822960.0090852415, bounds.south, 0.0000000001)
        assertEquals(824226.3820666744, bounds.north, 0.0000000001)
        assertEquals("EPSG:2927", bounds.proj.id)

        def (double w, double h) = raster.size
        assertEquals(761, w, 0.1)
        assertEquals(844, h, 0.1)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        def (int bw, int bh) = raster.blockSize
        assertEquals(761, bw)
        assertEquals(3, bh)

        def (double pw, double ph) = raster.pixelSize
        assertEquals(1.499114573022, pw, 0.000000000001)
        assertEquals(1.500441921129, ph, 0.000000000001)
    }
	
}
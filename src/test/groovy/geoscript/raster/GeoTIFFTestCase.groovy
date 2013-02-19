package geoscript.raster

import geoscript.geom.Bounds
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import geoscript.geom.Point

/**
 * The Raster unit test
 */
class GeoTIFFTestCase {
	
    @Test void geotiff() {

        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geoTIFF = new GeoTIFF()
        assertNotNull(geoTIFF)
        assertEquals("GeoTIFF", geoTIFF.format)

        Raster raster = geoTIFF.read(file)
        assertEquals("EPSG:2927", raster.proj.id)

        Bounds bounds = raster.bounds
        assertEquals(1166191.0260847565, bounds.minX, 0.0000000001)
        assertEquals(1167331.8522748263, bounds.maxX, 0.0000000001)
        assertEquals(822960.0090852415, bounds.minY, 0.0000000001)
        assertEquals(824226.3820666744, bounds.maxY, 0.0000000001)
        assertEquals("EPSG:2927", raster.proj.id)

        def (double w, double h) = raster.size
        assertEquals(760, w, 0.1)
        assertEquals(843, h, 0.1)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        def (int bw, int bh) = raster.blockSize
        assertEquals(761, bw)
        assertEquals(3, bh)

        def (double pw, double ph) = raster.pixelSize
        assertEquals(1.5010870921970836, pw, 0.000000000001)
        assertEquals(1.5022218047840352, ph, 0.000000000001)
    }
}
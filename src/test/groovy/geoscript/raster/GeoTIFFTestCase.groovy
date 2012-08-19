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

        GeoTIFF geoTIFF = new GeoTIFF(file)
        assertNotNull(geoTIFF)

        assertEquals("GeoTIFF", geoTIFF.format)
        assertEquals("EPSG:2927", geoTIFF.proj.id)

        Bounds bounds = geoTIFF.bounds
        assertEquals(1166191.0260847565, bounds.minX, 0.0000000001)
        assertEquals(1167331.8522748263, bounds.maxX, 0.0000000001)
        assertEquals(822960.0090852415, bounds.minY, 0.0000000001)
        assertEquals(824226.3820666744, bounds.maxY, 0.0000000001)
        assertEquals("EPSG:2927", bounds.proj.id)

        def (double w, double h) = geoTIFF.size
        assertEquals(761, w, 0.1)
        assertEquals(844, h, 0.1)

        List<Band> bands = geoTIFF.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        def (int bw, int bh) = geoTIFF.blockSize
        assertEquals(761, bw)
        assertEquals(3, bh)

        def (double pw, double ph) = geoTIFF.pixelSize
        assertEquals(1.499114573022, pw, 0.000000000001)
        assertEquals(1.500441921129, ph, 0.000000000001)

        geoTIFF.dump()
    }

    @Test void createFromList() {

        List data = [
            [0,0,0,0,0,0,0],
            [0,1,1,1,1,1,0],
            [0,1,2,3,2,1,0],
            [0,1,1,1,1,1,0],
            [0,0,0,0,0,0,0]
        ]
        Bounds bounds = new Bounds(0, 0, 7, 5, "EPSG:4326")

        GeoTIFF tiff = new GeoTIFF(data, bounds)
        assertNotNull tiff
        assertNotNull tiff.bounds
        assertEquals 0, tiff.evaluate(new Point(0.5,0.5))[0], 0.1
        assertEquals 1, tiff.evaluate(new Point(1.5,1.5))[0], 0.1
        assertEquals 2, tiff.evaluate(new Point(2.5,2.5))[0], 0.1
        assertEquals 3, tiff.evaluate(new Point(3.5,2.5))[0], 0.1
    }
}
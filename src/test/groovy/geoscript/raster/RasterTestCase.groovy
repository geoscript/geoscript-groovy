package geoscript.raster

import geoscript.geom.*
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

    @Test void crop() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)

        Bounds bounds = new Bounds(1166191.0260847565, 822960.0090852415, 1166761.4391797914, 823593.1955759579)
        Raster cropped = raster.crop(bounds)
        assertNotNull(cropped)
        assertEquals(bounds.west, cropped.bounds.west, 1d)
        assertEquals(bounds.east, cropped.bounds.east, 1d)
        assertEquals(bounds.north, cropped.bounds.north, 1d)
        assertEquals(bounds.south, cropped.bounds.south, 1d)
    }

    @Test void reproject() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)
        assertEquals("EPSG:2927", raster.proj.id)

        Raster reprojected = raster.reproject(new Projection("EPSG:4326"))
        assertNotNull(reprojected)
        assertEquals("EPSG:4326", reprojected.proj.id)
    }

    @Test void write() {
        // Read a GeoTIFF
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)
        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)
        assertEquals("EPSG:2927", raster.proj.id)

        // Reproject it to 4326
        Raster reprojected = raster.reproject(new Projection("EPSG:4326"))
        assertNotNull(reprojected)
        assertEquals("EPSG:4326", reprojected.proj.id)

        // Write the reprojected GeoTIFF to a file
        File file1 = File.createTempFile("reprojected_raster",".tif")
        println(file1)
        reprojected.write(file1)

        // Read the written reprojected GeoTIFF
        Raster raster2 = new GeoTIFF(file1)
        assertNotNull(raster2)
        assertEquals("EPSG:4326", raster2.proj.id)
    }

    @Test void evalulate() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)
        Raster raster = new GeoTIFF(file)
        assertNotNull(raster)

        def value = raster.evaluate(new Point(1166761.4391797914, 823593.1955759579))
        assertEquals(-30, value[0])
        assertEquals(-22, value[1])
        assertEquals(-46, value[2])
    }
}
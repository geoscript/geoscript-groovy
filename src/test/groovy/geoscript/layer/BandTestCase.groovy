package geoscript.layer

import geoscript.layer.Band
import geoscript.layer.GeoTIFF
import geoscript.layer.Raster
import org.junit.Test
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull

/**
 * The Raster unit test
 */
class BandTestCase {
	
    @Test void bands() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geotiff = new GeoTIFF()
        Raster raster = geotiff.read(file)
        assertNotNull(raster)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        assertFalse(bands[0].isNoData(-99))

        bands.eachWithIndex {band,i ->
            println("Band ${i}")
            println("  ${band}")
            println("  Min Value = ${band.min}")
            println("  Max Value = ${band.max}")
            println("  No Data = ${band.noData}")
            println("  Unit = ${band.unit}")
            println("  Scale = ${band.scale}")
            println("  Offset = ${band.offset}")
            println("  Type = ${band.type}")
        }
    }
}
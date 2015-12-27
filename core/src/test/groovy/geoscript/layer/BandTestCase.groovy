package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

/**
 * The Raster unit test
 */
class BandTestCase {
	
    @Test void bands() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        Format format = Format.getFormat(file)
        Raster raster = format.read()
        assertNotNull(raster)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        assertFalse(bands[0].isNoData(-99))

        bands.eachWithIndex {band,i ->
            band.min
            band.max
            band.noData
            band.unit
            band.scale
            band.offset
            band.type
        }
    }
}
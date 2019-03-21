package geoscript.layer

import org.junit.Test
import si.uom.SI

import static org.junit.Assert.*

/**
 * The Raster unit test
 */
class BandTestCase {
	
    @Test void bands() {
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        assertNotNull(file)

        GeoTIFF geotiff = new GeoTIFF(file)
        Raster raster = geotiff.read()
        assertNotNull(raster)

        List<Band> bands = raster.bands
        assertEquals(3, bands.size())
        assertEquals("RED_BAND", bands[0].toString())
        assertEquals("GREEN_BAND", bands[1].toString())
        assertEquals("BLUE_BAND", bands[2].toString())

        assertEquals("RED_BAND", bands[0].getDescription())
        assertEquals("GREEN_BAND", bands[1].getDescription())
        assertEquals("BLUE_BAND", bands[2].getDescription())

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

    @Test void createMinMax() {
        Band band = new Band("Red",0,255)
        assertEquals("Red", band.toString())
        assertEquals(0, band.min, 0.1)
        assertEquals(255, band.max, 0.1)
    }

    @Test void createMinMaxNoValue() {
        Band band = new Band("Elevation",100,200, 200)
        assertEquals("Elevation", band.toString())
        assertEquals(100, band.min, 0.1)
        assertEquals(200, band.max, 0.1)
        assertEquals(200, band.noData[0], 0.1)
        assertTrue(band.isNoData(200))

        band = new Band("Elevation",100,200, 999)
        assertEquals("Elevation", band.toString())
        assertEquals(100, band.min, 0.1)
        assertEquals(999, band.max, 0.1)
        assertEquals(999, band.noData[0], 0.1)
        assertTrue(band.isNoData(999))

        band = new Band("Elevation",100,200, -999)
        assertEquals("Elevation", band.toString())
        assertEquals(-999, band.min, 0.1)
        assertEquals(200, band.max, 0.1)
        assertEquals(-999, band.noData[0], 0.1)
        assertTrue(band.isNoData(-999))
    }

    @Test void createWithBuilder() {
        Band band = Band.builder()
                .description("Blue")
                .type("REAL_32BITS")
                .noDataValues([0])
                .minimum(0)
                .maximum(255)
                .scale(1)
                .offset(0)
                .unit(SI.METRE)
                .build()
        assertNotNull band
        assertEquals("Blue", band.toString())
        assertEquals("REAL_32BITS", band.dim.sampleDimensionType.name())
        assertEquals(0, band.noData[0], 0.1)
        assertEquals(0, band.min, 0.1)
        assertEquals(255, band.max, 0.1)
        assertEquals(1, band.scale, 0.1)
        assertEquals(0, band.offset, 0.1)
        assertEquals(SI.METRE, band.unit)
    }
}
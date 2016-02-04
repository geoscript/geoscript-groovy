package geoscript.proj

import geoscript.geom.Point
import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

/**
 * The DecimalDegrees Unit Test
 * @author Jared Erickson
 */
class DecimalDegreesTestCase {

    @Test void constructors() {
        // Longitude and Latitude
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // DMS with glyphs
        dd = new DecimalDegrees("122\u00B0 31' 32.23\" W", "47\u00B0 12' 43.28\" N")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // DMS with characters
        dd = new DecimalDegrees("122d 31m 32.23s W", "47d 12m 43.28s N")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // DMS with characters in one string
        dd = new DecimalDegrees("122d 31m 32.23s W, 47d 12m 43.28s N")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // DDM with glyphs in one string
        dd = new DecimalDegrees("122\u00B0 31.5372' W, 47\u00B0 12.7213' N")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // DDM with characters in one string
        dd = new DecimalDegrees("122d 31.5372m W, 47d 12.7213m N")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // Longitude, Latitude comma delimited string
        dd = new DecimalDegrees("-122.525619, 47.212023")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // Longitude, Latitude pipe delimited string
        dd = new DecimalDegrees("-122.525619|47.212023")
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        // Point
        dd = new DecimalDegrees(new Point(-122.525619,47.212023))
        assertEquals(-122.525619, dd.longitude, 0.00001)
        assertEquals(47.212023, dd.latitude, 0.00001)

        dd = new DecimalDegrees("38d 53m 55s S, 77d 2m 16s E")
        assertEquals(-38.89861, dd.longitude, 0.00001)
        assertEquals(77.03778, dd.latitude, 0.00001)

    }

    @Test void getDms() {
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        def dms = dd.dms
        // Longitude
        assertEquals(-122, dms.longitude.degrees)
        assertEquals(31, dms.longitude.minutes)
        assertEquals(32.22839, dms.longitude.seconds, 0.00001)
        // Latitude
        assertEquals(47, dms.latitude.degrees)
        assertEquals(12, dms.latitude.minutes)
        assertEquals(43.2828, dms.latitude.seconds, 0.00001)
    }

    @Test void toDms() {
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        assertEquals "-122\u00B0 31' 32.2284\" W, 47\u00B0 12' 43.2828\" N", dd.toDms()
        assertEquals "-122d 31m 32.2284s W, 47d 12m 43.2828s N", dd.toDms(false)
    }

    @Test void getDdm() {
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        def ddm = dd.ddm
        assertEquals(-122, ddm.longitude.degrees)
        assertEquals(31.537139999, ddm.longitude.minutes, 0.00001)
        assertEquals(47, ddm.latitude.degrees)
        assertEquals(12.72138, ddm.latitude.minutes, 0.00001)
    }

    @Test void toDdm() {
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        assertEquals("-122\u00B0 31.5371' W, 47\u00B0 12.7214' N", dd.toDdm())
        assertEquals("-122d 31.5371m W, 47d 12.7214m N", dd.toDdm(false))
    }

    @Test void getPoint() {
        def dd = new DecimalDegrees(-122.525619, 47.212023)
        assertTrue(dd.point instanceof Point)
        assertEquals("POINT (-122.525619 47.212023)", dd.point.wkt)
    }
}

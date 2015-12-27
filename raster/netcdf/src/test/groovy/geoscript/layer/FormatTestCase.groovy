package geoscript.layer

import org.junit.Test

import static org.junit.Assert.*

class FormatTestCase {

    @Test void getFormat() {
        File file = new File(getClass().getClassLoader().getResource("O3-NO2.nc").toURI())
        Format format = Format.getFormat(file)
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof NetCDF)
        assertTrue(format.has("NO2"))
        assertTrue(format.has("O3"))
        assertFalse(format.has("A123"))
    }

}

package geoscript.layer

import org.junit.Test
import static org.junit.Assert.*

class FormatTestCase {

    @Test void getFormat() {
        // Existing file
        File file = new File(getClass().getClassLoader().getResource("alki.tif").toURI())
        Format format = Format.getFormat(file)
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof GeoTIFF)
        assertTrue(format.has("alki"))
        assertFalse(format.has("badname"))

        // New tif File
        file = new File("doesnotexist.tif")
        format = Format.getFormat(file)
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof GeoTIFF)

        // New tif File name
        format = Format.getFormat("doesnotexist.tif")
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof GeoTIFF)
    }

}

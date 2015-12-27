package geoscript.layer

import org.junit.Test

import static org.junit.Assert.*

class FormatTestCase {

    @Test void getFormat() {
        // Existing file
        File file = new File(getClass().getClassLoader().getResource("alki.png").toURI())
        Format format = Format.getFormat(file)
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof WorldImage)
        assertTrue(format.has("alki"))
        assertFalse(format.has("badname"))

        // File Doesn't Exist
        file = new File("doesnotexist.png")
        format = Format.getFormat(file)
        assertNotNull(format)
        assertNotNull(format.stream)
        assertTrue(format instanceof WorldImage)
    }

}

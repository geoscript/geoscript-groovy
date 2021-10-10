package geoscript.carto

import org.junit.jupiter.api.Test

import static org.junit.jupiter.api.Assertions.assertEquals
import static org.junit.jupiter.api.Assertions.assertTrue

class PngCartoFactoryTest {

    @Test
    void getName() {
        assertEquals("png", new PngCartoFactory().getName())
    }

    @Test
    void getMimeType() {
        assertEquals("image/png", new PngCartoFactory().getMimeType())
    }

    @Test
    void create() {
        PngCartoFactory cartoFactory = new PngCartoFactory()
        CartoBuilder cartoBuilder = cartoFactory.create(PageSize.LETTER_LANDSCAPE)
        assertTrue(cartoBuilder instanceof ImageCartoBuilder)
    }
}

package geoscript.carto

import org.junit.jupiter.api.Test
import static org.junit.jupiter.api.Assertions.*

class JpegCartoFactoryTest {

    @Test
    void getName() {
        assertEquals("jpeg", new JpegCartoFactory().getName())
    }

    @Test
    void getMimeType() {
        assertEquals("image/jpeg", new JpegCartoFactory().getMimeType())
    }

    @Test
    void create() {
        JpegCartoFactory cartoFactory = new JpegCartoFactory()
        CartoBuilder cartoBuilder = cartoFactory.create(PageSize.LETTER_LANDSCAPE)
        assertTrue(cartoBuilder instanceof ImageCartoBuilder)
    }
}

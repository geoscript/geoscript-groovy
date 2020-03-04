package geoscript.carto

import org.junit.Test
import static org.junit.Assert.*

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

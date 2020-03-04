package geoscript.carto

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class PdfCartoFactoryTest {

    @Test
    void getName() {
        assertEquals("pdf", new PdfCartoFactory().getName())
    }

    @Test
    void getMimeType() {
        assertEquals("application/pdf", new PdfCartoFactory().getMimeType())
    }

    @Test
    void create() {
        PdfCartoFactory cartoFactory = new PdfCartoFactory()
        CartoBuilder cartoBuilder = cartoFactory.create(PageSize.LETTER_LANDSCAPE)
        assertTrue(cartoBuilder instanceof PdfCartoBuilder)
    }
}

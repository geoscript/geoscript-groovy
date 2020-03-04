package geoscript.carto

import org.junit.Test

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue

class SvgCartoFactoryTest {

    @Test
    void getName() {
        assertEquals("svg", new SvgCartoFactory().getName())
    }

    @Test
    void getMimeType() {
        assertEquals("application/svg", new SvgCartoFactory().getMimeType())
    }

    @Test
    void create() {
        SvgCartoFactory cartoFactory = new SvgCartoFactory()
        CartoBuilder cartoBuilder = cartoFactory.create(PageSize.LETTER_LANDSCAPE)
        assertTrue(cartoBuilder instanceof SvgCartoBuilder)
    }
}
